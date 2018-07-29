package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IServer;
import de.upb.cs.bibifi.bankapp.bank.IServerProcessor;
import de.upb.cs.bibifi.commons.constants.AppConstants;
import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.Utilities;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Server implements IServer {

    private ServerSocket serverSocket;
    private IServerProcessor processor;

    private IEncryption encryption;
    private String authFile = null;
    private int port = 0;

    //@TODO Add input validation and handling
    public static void main(String[] args) {
        // Handle argument input
        String authFileName = AppConstants.DEFAULT_AUTH_FILE_NAME;
        int port = AppConstants.DEFAULT_PORT_NUMBER;

        try {
            IServer server = new Server(port, authFileName);
            server.start();
        } catch (Exception e) {
            System.out.println(255);
            System.exit(-1);
        }
    }

    public Server(int port, String authFile) throws Exception {
        this.authFile = authFile;
        this.port = port;
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
            //Open Socket for accepting request
            Socket sock = serverSocket.accept();
            OutputStream out = sock.getOutputStream();
            PrintWriter print = new PrintWriter(out, true);

            InputStream istream = sock.getInputStream();
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
            String receiveMessage,decryptMsg = null;

            //Receive msg and decrypt the message
            if ((receiveMessage = receiveRead.readLine()) != null) {
                decryptMsg = encryption.decryptMessage(Utilities.convertString(receiveMessage));
                System.out.println(decryptMsg);
            }

            //Take decrypted msg and make pkt
            String json = decryptMsg.toString();
            TransmissionPacket requestPkt = Utilities.deserializer(json);
            if (validTransmission(requestPkt)) {
                String resJson = processor.executeOperation(requestPkt);
                String response = Utilities.convertOutputStream(encryption.encryptMessage(resJson));
                print.println(resJson);
                print.flush();
            } else {
                continue;
            }
        }
    }

    private boolean validTransmission(TransmissionPacket packet) {
        return true;
    }


    public void cleanup() {
        // @TODO CLEANUP is a mehtod that would be could upon exists using SIGTERM
        // stop running threads
        // store data to DB
        // close connection to DB
        // disconnect...
        // release other resources...
    }


    private class ShutdownHook extends Thread {
        @Override
        public void run() {
            System.out.println("Shutdown Server process is activated");
            cleanup();
        }
    }
}