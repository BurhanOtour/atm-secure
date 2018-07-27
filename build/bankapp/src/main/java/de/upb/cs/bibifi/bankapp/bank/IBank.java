package de.upb.cs.bibifi.bankapp.bank;

import de.upb.cs.bibifi.bankapp.data.Account;

public interface IBank {
    void startup(String authFileName) throws Exception;

    String createBalance(String acc, int balance);

    boolean deposit(String acc, String hashedPin, int balance) throws Exception;

    boolean withdraw(String acc, String hashedPin, int balance) throws Exception;

    int checkBalance(String acc, String hashedPin) throws Exception;
}
