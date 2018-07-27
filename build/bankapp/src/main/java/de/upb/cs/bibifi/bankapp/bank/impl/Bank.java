package de.upb.cs.bibifi.bankapp.bank.impl;

import com.google.gson.Gson;
import de.upb.cs.bibifi.bankapp.bank.IAuthFileContentGenerator;
import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.bankapp.constants.AppConstants;
import de.upb.cs.bibifi.bankapp.data.Account;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;

public class Bank implements IBank {
    private String authFile;

    private Gson gson = new Gson();

    private HashMap<String, Account> accounts = new HashMap<>();

    private Bank() {
    }

    private static IBank bank;

    static {
        bank = null;
    }

    public static IBank getBank() {
        if (bank == null) {
            bank = new Bank();
        }
        return bank;
    }

    @Override
    public void startup(String authFileName) throws Exception {
        // @TODO check auth file name validity
        this.authFile = authFileName == null || authFileName.isEmpty() ? AppConstants.DEFAULT_AUTH_FILE_NAME : authFileName;
        if ((new File(this.authFile)).exists()) {
            throw new IllegalStateException("Auth file already exists");
        }
        createAuthFile(this.authFile);
        System.out.println(AppConstants.BANK_CREATION_CONFIRMATION_MESSAGE);
    }

    /**
     * @param acc     the account name
     * @param balance
     * @return if the account creation protocol is respected it should return the pin back
     */
    @Override
    public String createBalance(String acc, int balance) {
        String generatedPin = generatePIN();
        accounts.put(acc, new Account(balance, acc, hashMessage(generatedPin + getSalt())));
        return generatedPin;
    }

    @Override
    public boolean deposit(String acc, String pin, int balance) throws Exception {
        Account account = validateAccountData(acc, pin);
        if (account == null) {
            throw new Exception("Account is not valid!");
        }
        account.addBalance(balance);
        return account.addBalance(balance);
    }

    @Override
    public boolean withdraw(String acc, String pin, int balance) throws Exception {
        Account account = validateAccountData(acc, pin);
        if (account == null) {
            throw new Exception("Account is not valid!");
        }
        return account.withdrawBalance(balance);
    }

    @Override
    public int checkBalance(String acc, String pin) throws Exception {
        Account account = validateAccountData(acc, pin);
        if (account == null) {
            throw new Exception("Account is not valid!");
        }
        return account.getBalance();
    }

    public String hashMessage(String message) {
        return DigestUtils.sha1Hex(message + getSalt());
    }

    private void createAuthFile(String authFileName) throws Exception {
        // @TODO the one should be replaced by the read generator
        IAuthFileContentGenerator generator = DummyAuthFileContentGenerator.getGenerator();
        File authFile = new File(authFileName);
        FileUtils.copyInputStreamToFile(generator.generateAuthFileContent(), authFile);
    }

    public String generatePIN() {
        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int) (Math.random() * AppConstants.MAX_PIN_NUMBER) + AppConstants.MIN_PIN_NUMBER;
        return String.valueOf(randomPIN);
    }

    private String getSalt() {
        return "SomeSalt";
    }

    private Account validateAccountData(String acc, String pin) {
        Account account = accounts.get(acc);
        if (account == null || !account.getHashedPin().equals(hashMessage(pin + getSalt()))) {
            return null;
        }
        return account;
    }
}
