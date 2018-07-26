package de.upb.cs.bibifi.client;

import java.io.*;
import java.net.Socket;


public class ClientConnection {
    /**
     * Runs the client as an application.  First it displays a dialog
     * box asking for the IP address or hostname of a host running
     * the date server, then connects to it and displays the date that
     * it serves.
     */
    public static void main(String [] args) throws IOException {
        clientConnect(3000, "127.0.0.1");
    }

    public static void clientConnect(int port, String ip) throws IOException {
        Socket s = new Socket(ip, port);
        while(true) {
            String str = "Auth file";

            // Sends data to the output port of the socket
            OutputStreamWriter os = new OutputStreamWriter(s.getOutputStream());
            PrintWriter out = new PrintWriter(os);
            out.write(str);
            out.flush();
            out.close();
            Socket s1 = new Socket(ip, port);

            // Fetches data
            BufferedReader br = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            String nickName = br.readLine();

            System.out.println("C: Data from Server: " + nickName);
        }
    }
}
