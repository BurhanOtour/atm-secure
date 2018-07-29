package de.upb.cs.bibifi.bankapp;

import de.upb.cs.bibifi.bankapp.bank.impl.Bank;
import de.upb.cs.bibifi.commons.data.AuthFile;



public class Main {
    public static void main(String[] args) throws Exception {


        Bank.getBank().startup("bank.auth");
        String key = AuthFile.getAuthFile("bank.auth").getKey();
        System.out.println(key);
    }
}
