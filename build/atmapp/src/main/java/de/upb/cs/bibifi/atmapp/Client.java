package de.upb.cs.bibifi.atmapp;

import java.io.*;
import java.net.Socket;

public class Client implements IClient {
    private String ipAddress = null;
    private int port = 0;

    public Client (String ip, int port){
        ipAddress = ip;
        this.port = port;
    }

    @Override
    public String clientRequest(String msg) throws IOException {
        Socket sock = new Socket(ipAddress, 25000);
        BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
        OutputStream ostream = sock.getOutputStream();
        PrintWriter pwrite = new PrintWriter(ostream, true);
        InputStream istream = sock.getInputStream();
        BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

        String receiveMessage, sendMessage;
        while (true) {
            sendMessage = msg;
            pwrite.println(sendMessage);
            pwrite.flush();
            if ((receiveMessage = receiveRead.readLine()) != null) {
                sock.close();
                return receiveMessage;
            }
        }
    }
}