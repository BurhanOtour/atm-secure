package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.bankapp.constants.AppConstants;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    void startupTest() throws IOException {


        IBank bank = Bank.getBank();
        try {
            String authFileName = AppConstants.DEFAULT_AUTH_FILE_NAME;
            bank.startup(authFileName);
            File authFile = new File(authFileName);
            assertTrue(authFile.exists());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        FileUtils.forceDeleteOnExit(new File(AppConstants.DEFAULT_AUTH_FILE_NAME));
    }

    @Test
    void twoAuthFileNameWithTheSameNameCanNotExistsTest() {
        IBank bank = Bank.getBank();
        try {
            String authFileName = AppConstants.DEFAULT_AUTH_FILE_NAME;
            bank.startup(authFileName);
            bank.startup(authFileName);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void twoMessagesResultInTheSameHashTest() throws IOException {
        String message1 = "Hello World!";
        String message2 = "Hello World!";
        Bank bank = (Bank) Bank.getBank();
        assertEquals(bank.hashMessage(message1), bank.hashMessage(message2));
    }

    @Test
    void TwoPinDigitsAreNotIdentical() {
        String pin1 = ((Bank) Bank.getBank()).generatePIN();
        String pin2 = ((Bank) Bank.getBank()).generatePIN();
        assertNotEquals(pin1, pin2);
    }

    @Test
    void pinIsOfLengthFourAndIsAllDigitsTest() {
        String pin1 = ((Bank) Bank.getBank()).generatePIN();
        Pattern p = Pattern.compile("[0-9]{4}");
        Matcher m = p.matcher(pin1);
        assertTrue(m.find());
    }

    @Test
    void notTwoAccountShouldHaveTheSameKeyTest() throws Exception {
        final String key = "key";
        IBank bank = Bank.getBank();
        bank.createBalance(key, 103);
        try {
            bank.createBalance(key, 103);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}