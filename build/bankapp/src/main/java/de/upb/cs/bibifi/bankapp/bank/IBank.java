package de.upb.cs.bibifi.bankapp.bank;

public interface IBank {
    void startup(String authFileName) throws Exception;
    void createBalance();
    void deposit();
    void withdraw();
    void checkBalance();
}
