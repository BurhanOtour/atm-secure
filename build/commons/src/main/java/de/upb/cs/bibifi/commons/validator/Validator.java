package de.upb.cs.bibifi.commons.validator;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Validator {

    public Validator() {
    }

    /**
     * check if arguments are duplicated
     *
     * @param option
     * @param options
     * @return
     */
    public boolean checkDuplicates(String option, Set<String> options) {
        return options.add(option);
    }

    /**
     * Check regex validation for numeric value
     *
     * @param numeral
     * @return
     */
    public boolean validateNumerals(String numeral) {
        double max_amount = 4294967295.99;
        double amount = Double.valueOf(numeral);
        Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)\\.\\d{2}|([1-9][0-9]*)$");
        return numeral.matches(pattern.pattern()) ? amount < max_amount : false;
    }

    /**
     * Check regex validation for IP address
     *
     * @param ipString
     * @return
     */
    public boolean validateIP(String ipString) {
        //Pattern pattern = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
        Pattern pattern = Pattern.compile(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        return pattern.matcher(ipString).matches();
    }

    /**
     * Check regex validation for port
     *
     * @param portString
     * @return
     */
    public boolean validatePort(String portString) {
        int min_num = 1024;
        int max_num = 65535;
        int amount = Integer.valueOf(portString);
        Pattern pattern = Pattern.compile("^(102[4-9]|10[3-9][0-9]|1[1-9][0-9]{2}|[2-9][0-9]{3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$");
        boolean b = false;
        if (amount > min_num && amount < max_num) {
            b = portString.matches(pattern.pattern());
        }
        return b;
    }

    /**
     * Check regex validation for File names
     *
     * @param fileName
     * @return
     */
    public boolean validateFileName(String fileName) {
        Pattern pattern = Pattern.compile("[_\\-\\.0-9a-z]+[^.]|[^.][_\\-\\.0-9a-z]+|[_\\-\\.0-9a-z][^.][_\\-\\.0-9a-z]$");
        return fileName.length() < 128 ? pattern.matcher(fileName).matches() : false;
    }

    /**
     * Check regex validation for File names
     *
     * @param accountName
     * @return
     */
    public boolean validateAccountName(String accountName) {
        Pattern pattern = Pattern.compile("[_\\-\\.0-9a-z]+$");
        return accountName.length() < 123 ? pattern.matcher(accountName).matches() : false;
    }

    /**
     * Check regex validation for File names
     *
     * @param initialBalance
     * @return
     */
    public boolean validateInitialBalance(String initialBalance) {
        int amount = Integer.valueOf(initialBalance);
        return amount > 10 ? true : false;
    }

    /**
     * check if arguments contain more than one Operations
     *
     * @param options
     * @return
     */
    public boolean checkOperations(Set<String> options) {

        final String CMD_D = "d";
        final String CMD_W = "w";
        final String CMD_N = "n";
        final String CMD_G = "g";

        Set<String> operations = new HashSet<>();
        operations.add(CMD_D);
        operations.add(CMD_W);
        operations.add(CMD_N);
        operations.add(CMD_G);
        boolean match = false;
        for (String s : options) {
            if (operations.contains(s) && match != true) {
                match = true;
            }else if (operations.contains(s) && match == true){
                match = false;
            }
        }
        return match;
    }
}
