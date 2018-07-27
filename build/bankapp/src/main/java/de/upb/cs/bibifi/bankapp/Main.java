package de.upb.cs.bibifi.bankapp;

import com.google.gson.Gson;
import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.bankapp.bank.impl.Bank;
import de.upb.cs.bibifi.bankapp.data.Account;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        Gson g = new Gson();
        Account acc = new Account(130,"Burhan","12312412edaskfbasdkdfhsab");
        String json = g.toJson(acc);
        Account deserializedAccount = g.fromJson(new InputStreamReader(new ByteArrayInputStream(json.getBytes())), Account.class);
        System.out.println(json);
        System.out.println(deserializedAccount.equals(acc));
        System.out.println("------------------------");
        IBank bank = Bank.getBank();

        bank.startup("bank.auth");
    }
}
