package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IServer;
import de.upb.cs.bibifi.bankapp.bank.IServerProcessor;
import de.upb.cs.bibifi.bankapp.constants.AppConstants;
import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.Utilities;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


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
            InputStream is = sock.getInputStream();
            IEncryption e = EncryptionImpl.initialize(AuthFile.getAuthFile(this.authFile).getKey());
            String json = e.decryptMessage(is);
            TransmissionPacket requestPkt = Utilities.deserializer(json);
            if (validTransmission(requestPkt)) {
                String resStream = processor.executeOperation(requestPkt);
                print.println(encryption.encryptMessage(resStream));
                print.flush();
            } else {
                continue;
            }
        }
    }

    private boolean validTransmission(TransmissionPacket packet) {
        return true;
    }
}