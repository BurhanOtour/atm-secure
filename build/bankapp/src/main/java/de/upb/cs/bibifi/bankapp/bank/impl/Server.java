package de.upb.cs.bibifi.bankapp.bank.impl;

import com.sun.org.apache.regexp.internal.RE;
import de.upb.cs.bibifi.bankapp.bank.IServer;
import de.upb.cs.bibifi.commons.Converter.JsonConverter;
import de.upb.cs.bibifi.commons.dto.Request;
import de.upb.cs.bibifi.commons.enums.RequestType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server implements IServer {

    public Server() {}

    private String executeOperation(Request request) {

        if (request.getRequestType() == RequestType.CREATE) {
            //Call bank Method Here
        }

        if (request.getRequestType() == RequestType.DEPOSIT) {
            //Call bank Method Here
        }

        if (request.getRequestType() == RequestType.WITHDRAW) {
            //Call bank Method Here
        }

        if (request.getRequestType() == RequestType.CHECKBALANCE) {
            //Call bank Method Here
        }
        return "";
    }

    private String processRequest(String requestBody) throws IOException {
        Request request = JsonConverter.deserializer(requestBody);
        return executeOperation(request);
    }

    @Override
    public void startServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket sock = serverSocket.accept();
            OutputStream ostream = sock.getOutputStream();
            PrintWriter pwrite = new PrintWriter(ostream, true);
            InputStream istream = sock.getInputStream();
            // decrypt text

            //

            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));


            String receiveMessage, sendMessage;
            if ((receiveMessage = receiveRead.readLine()) != null) {
                InputStream targetStream = new ByteArrayInputStream(receiveMessage.getBytes());


                //String request = crypto.decrypt(targetStream);
                //Request

                System.out.println(receiveMessage);
            }
            sendMessage = "Response";
            pwrite.println(sendMessage);
            pwrite.flush();
        }
    }

}