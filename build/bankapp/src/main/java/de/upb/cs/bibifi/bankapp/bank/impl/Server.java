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
        encryption = EncryptionImpl.initialize(AuthFile.getAuthFile(this.authFile).getKey());
    }


    @Override
    public void start() throws Exception {
        while (true) {
            Socket sock = serverSocket.accept();
            OutputStream out = sock.getOutputStream();
            PrintWriter print = new PrintWriter(out, true);
            InputStream istream = sock.getInputStream();
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
            String receiveMessage;
            if ((receiveMessage = receiveRead.readLine()) != null) {
                System.out.println(receiveMessage);
            }
            String json = receiveMessage.toString();
            TransmissionPacket requestPkt = Utilities.deserializer(json);
            if (validTransmission(requestPkt)) {
                String resStream = processor.executeOperation(requestPkt);
                print.println(resStream);
                print.flush();
            } else {
                continue;
            }
        }
    }

    private boolean validTransmission(TransmissionPacket packet) {
        return true;
    }

    public void cleanup(){
        // @TODO CLEANUP is a mehtod that would be could upon exists using SIGTERM
    }
}