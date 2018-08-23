package de.upb.cs.bibifi.bankapp.bank.impl;

import com.google.gson.Gson;
import de.upb.cs.bibifi.bankapp.bank.IServerProcessor;
import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.constants.AppConstants;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.dto.Response;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.Utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends Thread {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private IServerProcessor processor;
    private IEncryption encryption;
    private static ArrayList<String> processedPktList = new ArrayList<>();

    public ClientHandler(Socket socket, String authFile) throws IOException {

        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.socket.setSoTimeout(AppConstants.SOCKET_TIMEOUT);
        this.processor = ServerProcessor.getServerProcessor();
        this.encryption = EncryptionImpl.initialize(AuthFile.getAuthFile(authFile).getKey());
    }

    private void waitForAcknowledgement(String packetId) throws IOException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        AcknowledgementHandler handler = new AcknowledgementHandler(inputStream, encryption, processedPktList, packetId);
        try {
            Future<String> f = service.submit(handler);
            String recvAckId = f.get(AppConstants.ACK_TIMEOUT, TimeUnit.SECONDS);     // Wait for Ack max for 10 seconds
            if (recvAckId != null || !recvAckId.isEmpty()) {
                processedPktList.add(recvAckId);
            }
        } catch (Exception e) {
            Bank.getBank().undo();
            System.err.println("Roll back request and shutdown ack handler");
            System.err.flush();

            System.out.println("protocol_error");
            System.out.flush();
        } finally {
            service.shutdownNow();
        }
    }

    @Override
    public void run() {
        try {
            String receiveMessage = inputStream.readUTF();
            String decryptMsg = encryption.decryptMessage(receiveMessage);
            //Take decrypted msg and make pkt
            if (decryptMsg != null) {
                String json = decryptMsg.toString();
                TransmissionPacket requestPkt = Utilities.deserializer(json);

                // Check for replay attack
                if (processedPktList.contains(requestPkt.getPacketId())) {
                    throw new Exception("Duplicate packet was sent.");
                }

                // Add to the processing list to denote that packet is already processed;
                processedPktList.add(requestPkt.getPacketId());
                Response response = processor.executeOperation(requestPkt);
                if (response.getCode() == 0) {
                    System.out.println(response.getMessage());
                    System.out.flush();
                }

                Gson gson = new Gson();
                String resJson = gson.toJson(response);
                String encryptResponse = encryption.encryptMessage(resJson);
                outputStream.writeUTF(encryptResponse);

                //Wait for Ack
                waitForAcknowledgement(response.getResponseId());

            } else {
                throw new Exception("Decryption Error");
            }

        } catch (Exception e) {
            System.out.println("protocol_error");
            System.out.flush();
        } finally {
            try {
                this.inputStream.close();
                this.outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
