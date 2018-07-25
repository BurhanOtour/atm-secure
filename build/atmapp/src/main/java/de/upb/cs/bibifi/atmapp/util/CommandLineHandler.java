package de.upb.cs.bibifi.atmapp.util;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class CommandLineHandler {

    private static final String CMD_S = "s";
    private static final String CMD_C = "c";
    private static final String CMD_A = "a";
    private static final String CMD_D = "d";
    private static final String CMD_W = "w";
    private static final String CMD_N = "n";
    private static final String CMD_I = "i";
    private static final String CMD_P = "p";
    private static final String CMD_G = "g";


    private String[] args = null;

    public CommandLineHandler(String[] args) {
        this.args = args;
    }

    /**
     * Initialize commandline set-up
     */
    public void init() {
        CommandLineParser commandLineParser = new DefaultParser();

        HelpFormatter formatter = new HelpFormatter();

        Options options = new Options();

        Option option = new Option(CMD_A, "initial", true, "initial acount");
        option.setRequired(true);
        options.addOption(option);
        options.addOption(CMD_S, "auth", true, "authentication file");
        options.addOption(CMD_C, "card", true, "user's bank card");
        options.addOption(CMD_D, "deposit", true, "deposit amount");
        options.addOption(CMD_W, "withdrawal", true, "withdrawal amount");
        options.addOption(CMD_I, "initial ip", true, "initial ip");
        options.addOption(CMD_P, "initial port", true, "initial port");
        options.addOption(CMD_N, "initial", true, "initial balance");
        options.addOption(CMD_G, "initial balance");


        CommandLine commandLine = null;

        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("255");
            System.exit(1);
        }

        processCommandLineArguments(commandLine);

    }

    /**
     * Process command line arguments based on given rules
     *
     * @param cmdLine
     */
    private void processCommandLineArguments(CommandLine cmdLine) {
        Set<String> options = new HashSet<>();
        Arrays.stream(cmdLine.getOptions()).forEach(option -> {
            if (!checkDuplicates(option.getOpt(), options)) fail();
            if (CMD_D.equals(option.getOpt()) || CMD_W.equals(option.getOpt()) || CMD_N.equals(option.getOpt()))
                if (!validateNumerals(option.getValue())) fail();
            if (CMD_I.equals(option.getOpt())) {
                if (!validateIP(option.getValue()))
                    fail();
            }
            if (CMD_P.equals(option.getOpt())) {
                if (!validatePort(option.getValue()))
                    fail();
            }
            if (CMD_C.equals(option.getOpt())|| CMD_S.equals(option.getOpt())) {
                if (!validateFileName(option.getValue()))
                    fail();
            }
            if (CMD_A.equals(option.getOpt()) ) {
                if (!validateAcountName(option.getValue()))
                    fail();
            }
        });
    }

    /**
     * check if arguments are duplicated
     *
     * @param option
     * @param options
     * @return
     */
    private boolean checkDuplicates(String option, Set<String> options) {
        return options.add(option);
    }

    /**
     * Exit application by displaying en error code.
     */
    private void fail() {
        System.out.print("255");
        System.exit(1);
    }

    /**
     * Check regex validation for numeric value
     *
     * @param numeral
     * @return
     */
    private boolean validateNumerals(String numeral) {
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
    private boolean validateIP(String ipString) {
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
    private boolean validatePort(String portString) {
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
    private boolean validateFileName(String fileName) {
        Pattern pattern = Pattern.compile("[_\\-\\.0-9a-z]+[^.][^-][^_]|[^_][^-][^.][_\\-\\.0-9a-z]+|[_\\-\\.0-9a-z][^-][^_][^.][_\\-\\.0-9a-z]$");
        return fileName.length() < 128 ? pattern.matcher(fileName).matches() : false;
    }

    /**
     * Check regex validation for File names
     *
     * @param acountName
     * @return
     */
    private boolean validateAcountName(String acountName) {
        Pattern pattern = Pattern.compile("[_\\-\\.0-9a-z]+$");
        return acountName.length() < 123 ? pattern.matcher(acountName).matches() : false;
    }
}
