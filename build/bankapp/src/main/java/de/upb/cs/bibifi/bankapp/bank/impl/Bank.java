package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IAuthFileContentGenerator;
import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.bankapp.constants.AppConstants;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class Bank implements IBank {
    private boolean startedUp = false;

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
        if (startedUp) {
            return;
        }
        createAuthFile(authFileName);
        startedUp = true;
        System.out.println(AppConstants.BANK_CREATION_CONFIRMATION_MESSAGE);
    }

    private void createAuthFile(String authFileName) throws Exception {
        // @TODO the one should be replaced by the read generator
        IAuthFileContentGenerator generator = DummyAuthFileContentGenerator.getGenerator();
        File authFile = new File(authFileName);
        FileUtils.copyInputStreamToFile(generator.generateAuthFileContent(), authFile);
    }

    @Override
    public void createBalance() {

    }

    @Override
    public void deposit() {

    }

    @Override
    public void withdraw() {

    }

    @Override
    public void checkBalance() {

    }
}
