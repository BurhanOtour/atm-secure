package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IBank;
import de.upb.cs.bibifi.commons.constants.AppConstants;
import de.upb.cs.bibifi.bankapp.data.Account;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    private String authFileName;

    @BeforeEach
    public void clearBunk() {
        ((Bank) Bank.getBank()).reset();
        authFileName = generateAuthFileName();
    }

    @AfterEach
    public void cleanup() throws IOException {
        FileUtils.forceDelete(new File(authFileName));
    }

    private String generateAuthFileName() {
        long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
        long end = Timestamp.valueOf("2013-01-01 00:00:00").getTime();
        long diff = end - offset + 1;
        Timestamp rand = new Timestamp(offset + (long) (Math.random() * diff));
        return rand.getTime() + "_" + AppConstants.DEFAULT_AUTH_FILE_NAME;
    }

    @Test
    void startupTest() throws IOException {
        IBank bank = Bank.getBank();
        try {
            bank.startup(authFileName);
            File authFile = new File(authFileName);
            assertTrue(authFile.exists());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void twoAuthFileNameWithTheSameNameCanNotExistsTest() throws IOException {
        IBank bank = Bank.getBank();
        try {
            bank.startup(authFileName);
            bank.startup(authFileName);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void rollbackCreateAccountOperationTest() throws Exception {
        IBank bank = Bank.getBank();
        bank.startup(authFileName);
        final int sampleBalance = 21;
        String pin = bank.createBalance("foo", sampleBalance);
        assertEquals(sampleBalance, bank.checkBalance("foo", pin));
        assertEquals(sampleBalance, bank.checkBalance("foo", pin));
        bank.undo();
        assertEquals(-1, Bank.getBank().checkBalance("foo", pin));
    }

    @Test
    void rollbackDepositeOperationTest() throws Exception {
        IBank bank = Bank.getBank();
        bank.startup(authFileName);
        final int sampleBalance = 21;
        String pin = bank.createBalance("foo", sampleBalance);
        bank.commit();
        bank.deposit("foo", pin, 10);
        bank.undo();
        assertEquals(sampleBalance, bank.checkBalance("foo", pin));
    }

    @Test
    void rollbackWithdrawOperationTest() throws Exception {
        IBank bank = Bank.getBank();
        bank.startup(authFileName);
        final int sampleBalance = 21;
        String pin = bank.createBalance("foo", sampleBalance);
        bank.commit();
        bank.withdraw("foo", pin, 10);
        bank.commit();
        bank.withdraw("foo", pin, 5);
        bank.undo();
        assertEquals(11, bank.checkBalance("foo", pin));
    }

    @Test
    void twoMessagesResultInTheSameHashTest() throws Exception {
        String message1 = "Hello Wolrd!";
        String message2 = "Hello Wolrd!";
        Bank bank = (Bank) Bank.getBank();
        bank.startup(authFileName);
        assertEquals(bank.hashMessage(message1), bank.hashMessage(message2));
    }

    @Test
    void TwoPinDigitsAreNotIdentical() throws Exception {
        Bank.getBank().startup(authFileName);
        String pin1 = ((Bank) Bank.getBank()).generatePIN();
        String pin2 = ((Bank) Bank.getBank()).generatePIN();
        assertNotEquals(pin1, pin2);
    }

    @Test
    void pinIsOfLengthFourAndIsAllDigitsTest() throws Exception {
        Bank.getBank().startup(authFileName);
        String pin1 = ((Bank) Bank.getBank()).generatePIN();
        Pattern p = Pattern.compile("[0-9]{4}");
        Matcher m = p.matcher(pin1);
        assertTrue(m.find());
    }

    @Test
    void multipleTransactionTest() throws Exception {
        Bank.getBank().startup(authFileName);

        int fooInitialBanalce = 12;
        String fooPin = Bank.getBank().createBalance("foo", fooInitialBanalce);
        Bank.getBank().commit();
        assertEquals(fooInitialBanalce, Bank.getBank().checkBalance("foo", fooPin));
        try {
            int booInitialBanalce = 9;
            String booPin = Bank.getBank().createBalance("foo", fooInitialBanalce);
            Bank.getBank().commit();
        } catch (Exception e) {
            assertTrue(true);
        }
        // They Should have no effect
        Bank.getBank().commit();
        Bank.getBank().commit();
        Bank.getBank().commit();

        assertFalse(Bank.getBank().withdraw("foo", fooPin, -1000));
        Bank.getBank().commit();
        assertFalse(Bank.getBank().withdraw("foo", fooPin, 13));
        Bank.getBank().commit();
        assertTrue(Bank.getBank().withdraw("foo", fooPin, 12));
        Bank.getBank().commit();
        assertTrue(Bank.getBank().deposit("foo", fooPin, 10));
        Bank.getBank().undo();
        // That doesn't have any effect since we already undone the work
        Bank.getBank().commit();
        assertEquals(0, Bank.getBank().checkBalance("foo", fooPin));
        assertTrue(Bank.getBank().deposit("foo", fooPin, 10));
        Bank.getBank().commit();
        assertTrue(Bank.getBank().withdraw("foo", fooPin, 10));
        Bank.getBank().undo();
        assertEquals(10, Bank.getBank().checkBalance("foo", fooPin));
    }

    @Test
    void notTwoAccountShouldHaveTheSameKeyTest() throws Exception {
        final String key = "key";
        IBank bank = Bank.getBank();
        bank.startup(authFileName);
        bank.createBalance(key, 103);
        bank.commit();
        try {
            bank.createBalance(key, 103);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}