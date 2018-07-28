package de.upb.cs.bibifi.bankapp.bank.impl;

import com.google.gson.Gson;
import de.upb.cs.bibifi.bankapp.bank.IAuthFileContentGenerator;
import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.bankapp.constants.AppConstants;
import de.upb.cs.bibifi.bankapp.data.Account;
import de.upb.cs.bibifi.bankapp.exceptions.SystemException;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.enums.RequestType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Bank implements IBank {
    private String authFile;

    private int currentBalance;

    private Account currentAccount;

    private Gson gson = new Gson();

    private HashMap<String, Account> accounts = new HashMap<>();

    private RequestType type = null;

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
//        // @TODO check auth file name validity
        this.authFile = authFileName == null || authFileName.isEmpty() ? AppConstants.DEFAULT_AUTH_FILE_NAME : authFileName;
        if ((new File(this.authFile)).exists()) {
            throw new IllegalStateException("Auth file already exists");
        }
        createAuthFile(this.authFile);
        System.out.println(AppConstants.BANK_CREATION_CONFIRMATION_MESSAGE);
        //this.authFile = authFileName;
    }

    /**
     * @param acc     the account name
     * @param balance
     * @return if the account creation protocol is respected it should return the pin back
     */
    @Override
    public String createBalance(String acc, int balance) {
        Account account = accounts.get(acc);
        if (account != null) {
            return null;
        }
        if (balance < 10) {
            return null;
        }
        String generatedPin = generatePIN();
        Account newAccount = new Account(balance, acc, hashMessage(generatedPin + getSalt()));
        currentAccount = newAccount;
        this.type = RequestType.CREATE;
        accounts.put(acc, newAccount);
        return generatedPin;
    }

    @Override
    public boolean deposit(String acc, String pin, int balance) {
        Account account = validateAccountData(acc, pin);
        if (account == null) {
            return false;
        }
        if (balance <= 0) {
            return false;
        }
        this.currentAccount = account;
        this.type = RequestType.DEPOSIT;
        this.currentBalance = balance;
        return account.addBalance(balance);
    }

    @Override
    public boolean withdraw(String acc, String pin, int balance) {
        Account account = validateAccountData(acc, pin);
        if (account == null) {
            return false;
        }

        this.type = RequestType.WITHDRAW;
        this.currentBalance = balance;
        this.currentAccount = account;
        return account.withdrawBalance(balance);
    }

    @Override
    public int checkBalance(String acc, String pin) {
        Account account = validateAccountData(acc, pin);
        if (account == null) {
            return -1;
        }
        return account.getBalance();
    }

    @Override
    public void commit() {
        resetTransaction();
    }

    @Override
    public void undo() {
        if (type != null) {
            switch (type) {
                case CREATE:
                    accounts.remove(currentAccount.getName());
                    resetTransaction();
                    break;
                case DEPOSIT:
                    currentAccount.withdrawBalance(currentBalance);
                    resetTransaction();
                    break;
                case WITHDRAW:
                    currentAccount.addBalance(currentBalance);
                    resetTransaction();
                    break;
            }
        }
    }

    private void resetTransaction() {
        this.type = null;
        this.currentAccount = null;
        this.currentBalance = 0;
    }

    public String hashMessage(String message) {
        return DigestUtils.sha1Hex(message + getSalt());
    }

    private void createAuthFile(String authFileName) throws Exception {
        // @TODO the one should be replaced by the read generator
        // @TODO this could be moved later to Server
        IAuthFileContentGenerator generator = AuthFileContentGeneratorImpl.getGenerator();
        File authFile = new File(authFileName);
        FileUtils.copyInputStreamToFile(generator.generateAuthFileContent(), authFile);
    }

    public String generatePIN() {
        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int) (Math.random() * AppConstants.MAX_PIN_NUMBER) + AppConstants.MIN_PIN_NUMBER;
        return String.valueOf(randomPIN);
    }

    private String getSalt() {
        return AuthFile.getAuthFile(this.authFile).getSalt();
    }

    private Account validateAccountData(String acc, String pin) {
        Account account = accounts.get(acc);
        if (account == null || !account.getHashedPin().equals(hashMessage(pin + getSalt()))) {
            return null;
        }
        return account;
    }

    public void reset() {
        resetTransaction();
        accounts = null;
        bank = null;
    }
}
