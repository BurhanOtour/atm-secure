package de.upb.cs.bibifi.atmapp;

import de.upb.cs.bibifi.commons.ITransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.TransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.impl.Utilities;

import java.io.*;
import java.net.Socket;

public class Client implements IClient {

    private String ipAddress = null;

    private int port = 0;

    public Client(String ip, int port) {
        ipAddress = ip;
        this.port = port;

        // TODO: 28/07/2018 find better way to do it
        AuthFile authFile = AuthFile.getAuthFile("/Users/sajjadpervaiz/git/uni/BabyHack/build/bank-2.auth");
        EncryptionImpl.initialize(authFile.getKey());

    }

    private TransmissionPacket processResponse(String response) {
        return Utilities.deserializer(response);
    }

    @Override
    public TransmissionPacket clientRequest(TransmissionPacket request) throws Exception {

        ITransmissionPacketProcessor packetProcessor = TransmissionPacketProcessor.getTransmissionPacketProcessor();

        OutputStream outPacket = packetProcessor.encryptMessage(request);

        Socket sock = new Socket(ipAddress, 25000);

        BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));

        OutputStream outputStream = sock.getOutputStream();

        PrintWriter printWriter = new PrintWriter(outputStream, true);

        InputStream inputStream = sock.getInputStream();

        BufferedReader receiveRead = new BufferedReader(new InputStreamReader(inputStream));

        printWriter.println(outPacket);

        printWriter.flush();

        String response = receiveRead.readLine();

        System.out.println("Hey I am coming from server: " + response);

        sock.close();

        return processResponse(response);
    }
}