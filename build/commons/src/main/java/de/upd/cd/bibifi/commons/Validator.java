package de.upd.cd.bibifi.commons;

import java.util.regex.Pattern;

public class Validator {

    public Validator() {

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
        Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)\\.\\d{2}$");
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
        Pattern pattern = Pattern.compile("[_\\-\\.0-9a-z]+[^.][^-][^_]|[^_][^-][^.][_\\-\\.0-9a-z]+|[_\\-\\.0-9a-z][^-][^_][^.][_\\-\\.0-9a-z]$");
        return fileName.length() < 128 ? pattern.matcher(fileName).matches() : false;
    }

    /**
     * Check regex validation for File names
     *
     * @param acountName
     * @return
     */
    public boolean validateAcountName(String acountName) {
        Pattern pattern = Pattern.compile("[_\\-\\.0-9a-z]+$");
        return acountName.length() < 123 ? pattern.matcher(acountName).matches() : false;
    }
}
