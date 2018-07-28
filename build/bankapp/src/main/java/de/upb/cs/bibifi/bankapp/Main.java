package de.upb.cs.bibifi.bankapp;


import de.upb.cs.bibifi.bankapp.bank.impl.Bank;
import de.upb.cs.bibifi.bankapp.bank.impl.Server;
import de.upb.cs.bibifi.commons.ITransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.dto.Status;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;
import de.upb.cs.bibifi.commons.enums.StatusCode;
import de.upb.cs.bibifi.commons.impl.TransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.impl.Utilities;


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

        //Bank.getBank().startup("bank.auth");
        //AuthFile authFile = AuthFile.getAuthFile("bank.auth");
        //EncryptionImpl.Intialize (key)

        //new Server().startServer(25000);



       new Server().startServer(25000);

    }
}
