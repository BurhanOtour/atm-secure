package de.upb.cs.bibifi.atmapp;

import com.google.gson.Gson;
import de.upb.cs.bibifi.atmapp.util.CommandLineHandler;
import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.constants.AppConstants;
import de.upb.cs.bibifi.commons.dto.Acknowledgement;
import de.upb.cs.bibifi.commons.dto.CreationResponse;
import de.upb.cs.bibifi.commons.dto.Response;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.Utilities;
import de.upb.cs.bibifi.commons.validator.InputPatternChecker;
import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.DuplicateFormatFlagsException;
import java.util.List;

public class Client implements IClient {

    private final String cardFileName;
    private final String ip;
    private final Integer port;
    private final static List<String> recvPktList = new ArrayList<>();

    private Client(String cardFileName, String ip, Integer port) {
        this.cardFileName = cardFileName;
        this.ip = ip;
        this.port = port;
    }

    private void clientRequest(TransmissionPacket request) throws Exception {

        try {
            Response responseObject = sendRequestOnSocket(request);
            if (responseObject.getCode() == 255 || responseObject.getCode() == 67) {
                System.exit(255);
            }
            if (responseObject.getCode() == 0) {
                if (request.getRequestType() == RequestType.CREATE) {
                    CreationResponse responseCreationObject = (CreationResponse) responseObject;
                    savePin(responseCreationObject.getPin());
                }
                System.out.println(responseObject.getMessage());
                System.out.flush();
            } else {
                System.exit(63);
            }
        } catch (IllegalBlockingModeException | IllegalArgumentException | IOException | InterruptedException ex) {
            System.err.println(63);
            System.err.println(ex.getMessage());
            System.exit(63);
        }
    }

    private Response sendRequestOnSocket(TransmissionPacket transmissionPacket) throws Exception {

        String jsonRequest = Utilities.serializer(transmissionPacket);
        Socket socket = new Socket(ip, port);
        socket.setSoTimeout(AppConstants.SOCKET_TIMEOUT);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        IEncryption encryption = EncryptionImpl.getInstance();
        Gson gson = new Gson();
        Response responseObject = null;
        String recvResponse = null;


        try {
            String encryptRequest = encryption.encryptMessage(jsonRequest);
            //Send request on the socket then wait for response
            dataOutputStream.writeUTF(encryptRequest);
            recvResponse = dataInputStream.readUTF();

            // decryptMessage the recv response
            recvResponse = encryption.decryptMessage(recvResponse);

            if (transmissionPacket.getRequestType() == RequestType.CREATE) {
                responseObject = gson.fromJson(recvResponse, CreationResponse.class);
            } else {
                responseObject = gson.fromJson(recvResponse, Response.class);
            }


            if (!transmissionPacket.getPacketId().equals(responseObject.getRequestId())) {
                throw new IllegalArgumentException("Replay attack detected");
            }

            //Update
            recvPktList.add(responseObject.getResponseId());
            //Send Acknowledgement
            sendAcknowledgement(dataOutputStream, encryption, responseObject);
            return responseObject;

        } catch (Exception ex) {
            throw ex;
        } finally {
            socket.close();
            dataOutputStream.close();
            dataInputStream.close();
        }
    }

    private void sendAcknowledgement(DataOutputStream dataOutputStream, IEncryption encryption, Response responseObject) throws IOException {
        Acknowledgement ack = new Acknowledgement(responseObject.getResponseId());
        String acknowledgement = Utilities.serializer(ack);
        String encryptMsg = encryption.encryptMessage(acknowledgement);
        dataOutputStream.writeUTF(encryptMsg);
    }


    private void savePin(String pin) throws Exception {
        File file = new File(cardFileName);
        if (file.exists()) {
            System.err.println(255);
            fail();
        }
        FileUtils.writeStringToFile(file, EncryptionImpl.getInstance().encryptMessage(pin), "UTF-8");
    }

    public static void main(String[] args) {
        if (!InputPatternChecker.check(args)) {
            System.err.println(255);
            System.exit(255);
        }

        CommandLineHandler commandLineHandler = new CommandLineHandler(args);
        TransmissionPacket packet = commandLineHandler.processCommandLineArguments().getPacket();
        Client client = new Client(commandLineHandler.getCardFileName(), commandLineHandler.getIp(), commandLineHandler.getPort());
        try {
            client.clientRequest(packet);
        } catch (Exception e) {
            System.err.println(255);
            System.exit(255);
        }
    }

    private void fail() {
        System.exit(255);
    }

}