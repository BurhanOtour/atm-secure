package de.upb.cs.bibifi.bankapp;

public interface Bank {
    void startup();
    void sendAuthFile();

    void createBalance();
    void deposit();
    void withdraw();
    void checkBalance();
}
