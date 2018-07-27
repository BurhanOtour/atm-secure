package de.upb.cs.bibifi.atmapp.atm;


import org.apache.commons.cli.Option;

public interface IATM {
    void createAccount(Option[] params);
    void deposit(Option[] params);
    void withdraw(Option[] params);
    void checkBalance(Option[] params);// TODO: 27/07/2018 check if this is needed
}
