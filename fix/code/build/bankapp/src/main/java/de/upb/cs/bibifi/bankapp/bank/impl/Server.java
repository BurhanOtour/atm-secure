package de.upb.cs.bibifi.bankapp.bank.impl;

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
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;


public class Server implements IServer {

    private ServerSocket serverSocket;
    private IServerProcessor processor;

    private IEncryption encryption;
    private String authFile;

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
            Socket sock = null;
            try {
                //Open Socket for accepting request
                sock = serverSocket.accept();
                Thread thread = new ClientHandler(sock, this.authFile);
                thread.start();

            } catch (IllegalArgumentException ex) {
                System.err.println(255);
                fail();
            } catch (SocketTimeoutException | IllegalBlockingModeException ex) {
                System.out.println("protocol_error");
                System.out.flush();
                sock.close();
            }
        }
    }

    private void cleanup() throws IOException {
        FileUtils.forceDelete(new File(authFile));
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