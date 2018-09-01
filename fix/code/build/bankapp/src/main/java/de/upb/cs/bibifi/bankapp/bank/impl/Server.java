package de.upb.cs.bibifi.bankapp.bank.impl;

import com.google.gson.Gson;
import de.upb.cs.bibifi.bankapp.bank.IServer;
import de.upb.cs.bibifi.bankapp.bank.IServerProcessor;
import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.constants.AppConstants;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.dto.Response;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.Utilities;
import de.upb.cs.bibifi.commons.validator.Validator;
import javafx.application.Application;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;


public class Server implements IServer {

    private ServerSocket serverSocket;
    private IServerProcessor processor;

    private IEncryption encryption;
    private String authFile;

    private Socket sock;

    private ArrayList<String> processedPktList = new ArrayList<>();

    public static void main(String[] args) {

        CommandLineParser commandLineParser = new DefaultParser();

        CommandLine commandLine = null;

        Options options = new Options();

        options.addOption("s", "authfile", true, "Authentication File");
        options.addOption("p", "port", true, "port");

        try {
            commandLine = commandLineParser.parse(options, args);

            Validator.applyValidators(commandLine.getOptions());

        } catch (UnrecognizedOptionException ex) {
            System.exit(255);
        } catch (ParseException e) {
            System.exit(255);
        }

        try {
            IServer server = new Server(Integer.parseInt(commandLine.getOptionValue("port", String.valueOf(AppConstants.DEFAULT_PORT_NUMBER)))
                    , commandLine.getOptionValue("authfile", AppConstants.DEFAULT_AUTH_FILE_NAME));
            server.start();
        } catch (Exception e) {
            System.exit(255);
        }
    }

    private Server(int port, String authFile) throws Exception {
        this.authFile = authFile;
        this.serverSocket = new ServerSocket(port);
        this.processor = ServerProcessor.getServerProcessor();
        Bank.getBank().startup(authFile);
        setUpShutDownHock();
        encryption = EncryptionImpl.initialize(AuthFile.getAuthFile(this.authFile).getKey());
    }

    private void setUpShutDownHock() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }


    @Override
    public void start() throws Exception {
        while (true) {
            try {
                processRequest();
            } catch (IllegalArgumentException ex) {
                System.err.println(255);
                fail();
            } catch (IOException ex) {
                if (sock != null) {
                    sock.close();
                }
                System.out.println("protocol_error");
                System.out.flush();
            }
        }
    }


    public void processRequest() throws IOException {
        sock = serverSocket.accept();
        sock.setSoTimeout(AppConstants.SOCKET_TIMEOUT);

        DataInputStream dataInputStream = new DataInputStream(sock.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(sock.getOutputStream());

        String receiveMessage = dataInputStream.readUTF();
        String decryptMsg = encryption.decryptMessage(receiveMessage);


        //Take decrypted msg and make pkt
        if (decryptMsg != null) {
            TransmissionPacket requestPkt = Utilities.deserializer(decryptMsg);

            // Check for replay attack
            if (processedPktList.contains(requestPkt.getPacketId())) {
                System.out.println("protocol_error");
                System.out.flush();
                return;
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
            dataOutputStream.writeUTF(encryptResponse);
            //Wait For Ack
            waitForAcknowledgement(response.getResponseId(), dataInputStream);
            sock.close();
        }
    }

    private void cleanup() throws IOException {
        FileUtils.forceDelete(new File(authFile));
    }

    private void waitForAcknowledgement(String packetId, DataInputStream inputStream) throws IOException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        AcknowledgementHandler handler = new AcknowledgementHandler(inputStream, encryption, processedPktList, packetId);
        try {
            Future<String> f = service.submit(handler);
            String recvAckId = f.get(AppConstants.ACK_TIMEOUT, TimeUnit.SECONDS);     // Wait for Ack max for 10 seconds
            processedPktList.add(recvAckId);

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            System.err.println(e);
            Bank.getBank().undo();
            System.err.println("Roll back request and shutdown ack handler");
            System.err.flush();

            System.out.println("protocol_error");
            System.out.flush();
        } finally {
            service.shutdownNow();
        }
    }


    private class ShutdownHook extends Thread {
        @Override
        public void run() {
            try {
                cleanup();
            } catch (IOException e) {
                System.exit(255);
            }
        }
    }

    private void fail() {
        System.err.println(255);
    }
}