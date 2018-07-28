package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.bankapp.bank.IServer;
import de.upb.cs.bibifi.commons.ITransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.impl.Utilities;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server implements IServer {


//    private String executeOperation(TransmissionPacket transmissionPacket) throws Exception {
//
//        IBank bank = Bank.getBank();
//
//        if (transmissionPacket.getRequestType() == RequestType.CREATE) {
//            String accountName = transmissionPacket.getProperty("accountName");
//            Integer balance = Integer.parseInt(transmissionPacket.getProperty("initialBalance"));
//
//            String pin = bank.createBalance(accountName, balance);
//            TransmissionPacket packet = new TransmissionPacket();
//            packet.setProperty ("pin", pin);
//            packet.setProperty ("balance", balance.toString());
//            packet.setProperty("accountName", accountName);
//            packet.setRequestType(RequestType.CREATE);
//            //return processor.encryptMessage(packet);
//            return Utilities.Serializer(packet);
//        }
//
//        if (transmissionPacket.getRequestType() == RequestType.DEPOSIT) {
//            //Call bank Method Here
//        }
//
//        if (transmissionPacket.getRequestType() == RequestType.WITHDRAW) {
//            //Call bank Method Here
//        }
//
//        if (transmissionPacket.getRequestType() == RequestType.CHECKBALANCE) {
//            //Call bank Method Here
//        }
//        return null;
//    }
//
//    private String processRequest(String requestBody) throws Exception {
//        TransmissionPacket transmissionPacket = Utilities.deserializer(requestBody);
//        return executeOperation(transmissionPacket);
//    }

    @Override
    public void startServer(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        ServerProcessor processor = ServerProcessor.getServerProcessor();

        while (true) {
            Socket sock = serverSocket.accept();
            OutputStream ostream = sock.getOutputStream();
            PrintWriter pwrite = new PrintWriter(ostream, true);
            InputStream istream = sock.getInputStream();
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
            String receiveMessage, sendMessage;
            if ((receiveMessage = receiveRead.readLine()) != null) {

                System.out.println("Request Receeived:"+ receiveMessage);
                //InputStream targetStream = new ByteArrayInputStream(receiveMessage.getBytes());
                TransmissionPacket packet = Utilities.deserializer (receiveMessage);

                //TransmissionPacket packet = processor.decryptMessage(targetStream);
                String resStream = processor.executeOperation (packet);
                pwrite.println(resStream);
                pwrite.flush();
            }

        }
    }

}