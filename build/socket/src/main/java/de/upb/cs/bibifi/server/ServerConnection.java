package de.upb.cs.bibifi.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection {
    /**
     * Runs the server.
     */
    public static void main(String[] args) throws IOException {
        serverConnect(3000, "127.0.0.1");
    }

    public static void serverConnect(int port, String ip) throws IOException {
        System.out.println("Server is started!");
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Server is waiting for client request");
        Socket s = ss.accept();
        System.out.println("Client connected!");

        // Fetches data
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String str = br.readLine();

        System.out.println("Client Data: " + str);

        int response = 200;

        // Sends data
        OutputStreamWriter os = new OutputStreamWriter(s.getOutputStream());
        PrintWriter out = new PrintWriter(os);
        out.write(response);
        out.flush();
        System.out.println("S: Response sent from Server to Client");
        out.close();
    }
}
