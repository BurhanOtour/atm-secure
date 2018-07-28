package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.bankapp.bank.IServerProcessor;
import de.upb.cs.bibifi.commons.ITransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.dto.Status;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;
import de.upb.cs.bibifi.commons.enums.StatusCode;
import de.upb.cs.bibifi.commons.impl.TransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.impl.Utilities;

public class ServerProcessor implements IServerProcessor {

    private static ServerProcessor processor;

    private ServerProcessor (){

    }

    public static ServerProcessor getServerProcessor (){
        if (processor == null)
            processor = new ServerProcessor();
        return processor;
    }

    private String processRequest(String requestBody) throws Exception {
        TransmissionPacket transmissionPacket = Utilities.deserializer(requestBody);
        return executeOperation(transmissionPacket);
    }

    private TransmissionPacket createAccountPacket (String accountName, Integer balance, String pin, Status status){
        TransmissionPacket packet = new TransmissionPacket();
        packet.setProperty("pin", pin);
        packet.setProperty("balance", balance.toString());
        packet.setProperty("accountName", accountName);
        packet.setRequestType(RequestType.CREATE);
        packet.setStatus(status);
        return packet;
    }

    public String executeOperation(TransmissionPacket transmissionPacket) throws Exception {

        IBank bank = Bank.getBank();
        ITransmissionPacketProcessor processor = TransmissionPacketProcessor.getTransmissionPacketProcessor();

        if (transmissionPacket.getRequestType() == RequestType.CREATE) {
            String accountName = transmissionPacket.getProperty("accountName");
            Integer balance = Integer.parseInt(transmissionPacket.getProperty("initialBalance"));
            String pin = bank.createBalance(accountName, balance);

            //Check Burhan
            Status code = new Status();
            code.setStatusCode(StatusCode.OK);
            code.setMessage("");
            TransmissionPacket packet = createAccountPacket (accountName, balance, pin, code);
            //processor.encryptMessage(packet);
            // change it to string encryption
            return Utilities.Serializer(packet);
        }
        return "";
    }
}
