package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IBank;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    void startupTest() {
        IBank bank = Bank.getBank();
        try {
            String authFileName = "bank.auth";
            bank.startup(authFileName);
            File authFile = new File(authFileName);
            assertTrue(authFile.exists());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}