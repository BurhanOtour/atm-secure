package de.upb.cs.bibifi.bankapp.bank;

import de.upb.cs.bibifi.bankapp.data.Account;

public interface IBank {
    void startup(String authFileName) throws Exception;

    String createBalance(String acc, double balance);

    boolean deposit(String acc, String pin, double balance);

    boolean withdraw(String acc, String pin, double balance);

    double checkBalance(String acc, String pin);

    public void commit();

    public void undo();
}
