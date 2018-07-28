package de.upb.cs.bibifi.bankapp;

import com.google.gson.Gson;
import de.upb.cs.bibifi.bankapp.bank.IAuthFileContentGenerator;
import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.bankapp.bank.impl.AuthFileContentGeneratorImpl;
import de.upb.cs.bibifi.bankapp.bank.impl.Bank;
import de.upb.cs.bibifi.bankapp.bank.impl.Server;
import de.upb.cs.bibifi.bankapp.data.Account;
import de.upb.cs.bibifi.commons.ITransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.TransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.impl.Utilities;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
//        Gson g = new Gson();
//        Account acc = new Account(130,"Burhan","12312412edaskfbasdkdfhsab");
//        String json = g.toJson(acc);
//        Account deserializedAccount = g.fromJson(new InputStreamReader(new ByteArrayInputStream(json.getBytes())), Account.class);
//        System.out.println(json);
//        System.out.println(deserializedAccount.equals(acc));
//        System.out.println("------------------------");
//        IBank bank = Bank.getBank();
//
//
        AuthFile auth = AuthFile.getAuthFile("bank.auth");
        Bank.getBank().startup("bank.auth");
        ITransmissionPacketProcessor processor = new TransmissionPacketProcessor(new EncryptionImpl(auth.getKey()));
        new Server(processor).startServer(25000);



      //  new Server().startServer(35000);

    }
}
