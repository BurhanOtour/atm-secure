package de.upb.cs.bibifi.commons;

import de.upb.cs.bibifi.commons.validator.Validator;
import org.junit.jupiter.api.*;

public class ValidatorTest  {

    @Test
    public void testValidateNumerals() {
        Validator validator = new Validator();
        Assertions.assertTrue(validator.validateNumerals("45.68"));
        Assertions.assertFalse(validator.validateNumerals("45.686"));
    }
    @Test
    public void testValidateIP() {
        Validator validator = new Validator();
        Assertions.assertTrue(validator.validateIP("45.68.0.0"));
        Assertions.assertFalse(validator.validateIP("456.686.78.0"));
    }
    @Test
    public void testValidatePort() {
        Validator validator = new Validator();
        Assertions.assertTrue(validator.validatePort("5670"));
        Assertions.assertFalse(validator.validatePort("75"));
    }
    @Test
    public void testValidateFileName() {
        Validator validator = new Validator();
        Assertions.assertTrue(validator.validateFileName(".fdrr5"));
        Assertions.assertFalse(validator.validateFileName("."));
    }
    @Test
    public void testValidateAccountName() {
        Validator validator = new Validator();
        Assertions.assertTrue(validator.validateAccountName("."));
        //Assertions.assertFalse(validator.validateAccountName("?f§"));
    }
    @Test
    public void validateInitialBalance() {
        Validator validator = new Validator();
        Assertions.assertTrue(validator.validateInitialBalance("67"));
        Assertions.assertFalse(validator.validateInitialBalance("2"));
    }
}
