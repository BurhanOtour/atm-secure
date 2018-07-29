package de.upb.cs.bibifi.atmapp;

import com.google.gson.Gson;
import de.upb.cs.bibifi.atmapp.atm.RequestProcessor;
import de.upb.cs.bibifi.atmapp.util.CommandLineHandler;
import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.dto.CreationResponse;
import de.upb.cs.bibifi.commons.dto.Response;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.Utilities;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client implements IClient {


    public void clientRequest(TransmissionPacket request, String authFileName) throws IOException {
        String jsonRequest = Utilities.Serializer(request);

        IEncryption encryption = EncryptionImpl.initialize(AuthFile.getAuthFile(authFileName).getKey());

        Socket sock = new Socket("127.0.0.1", 3000);
        OutputStream outputStream = sock.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        InputStream inputStream = sock.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String encryptString = encryption.encryptMessage(jsonRequest);
        printWriter.println(encryptString);

        //Receive Response
        String receivedMessage;
        if ((receivedMessage = br.readLine()) != null) {
            receivedMessage = encryption.decryptMessage(receivedMessage);
            Gson gson = new Gson();
            Response responseObject;
            if (request.getRequestType() == RequestType.CREATE) {
                responseObject = gson.fromJson(receivedMessage, CreationResponse.class);
            } else {
                responseObject = gson.fromJson(receivedMessage, Response.class);
            }
            if (responseObject.getCode() == 255 || responseObject.getCode() == 67) {
                System.out.println(responseObject.getCode());
                return;
            }
            if (responseObject.getCode() == 0) {
                if (request.getRequestType() == RequestType.CREATE) {
                    CreationResponse responseCreationObject = (CreationResponse) responseObject;
                    System.out.println(responseCreationObject.getPin());
                    savePin(responseCreationObject.getPin());
                }
                System.out.println(responseObject.getMessage());
                printWriter.flush();
                sock.close();
                return;
            } else {
                System.out.println(responseObject.getCode());
                return;
            }

        }
    }

    private void savePin(String pin) {
    }

    public static void main(String[] args) throws IOException {
        CommandLineHandler commandLineHandler = new CommandLineHandler(args);
        TransmissionPacket packet = RequestProcessor.generateRequest(RequestType.CHECKBALANCE, "dummy5", 5, "3250");
        Client client = new Client();
        client.clientRequest(packet, "bank.auth");
    }
}