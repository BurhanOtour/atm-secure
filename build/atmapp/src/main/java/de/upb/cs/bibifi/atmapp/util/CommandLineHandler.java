package de.upb.cs.bibifi.atmapp.util;

import de.upb.cs.bibifi.atmapp.atm.impl.Atm;
import de.upb.cs.bibifi.commons.validator.Validator;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


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

        Atm atm = new Atm();

        Arrays.stream(cmdLine.getOptions()).forEach(option -> {

            switch (option.getOpt()) {
                case CMD_N:
                    applyValidators(cmdLine.getOptions());
                    atm.createAccount(cmdLine.getOptions());
                case CMD_D:
                    applyValidators(cmdLine.getOptions());
                    atm.deposit(cmdLine.getOptions());
                case CMD_W:
                    applyValidators(cmdLine.getOptions());
                    atm.withdraw(cmdLine.getOptions());
            }
        });
    }

    /**
     * Apply set of validators to all input parameters
     *
     * @param options
     * @return
     */
    private void applyValidators(Option[] options) {

        Validator validator = new Validator();

        Set<String> duplicateOptionsSet = new HashSet<>();

        Arrays.stream(options).forEach(option -> {

            if (!validator.checkDuplicates(option.getOpt(), duplicateOptionsSet)) fail();

            switch (option.getOpt()) {
                case CMD_N:
                case CMD_D:
                case CMD_W:
                    if (!validator.validateNumerals(option.getValue()))
                        fail();
                case CMD_I:
                    if (!validator.validateIP(option.getValue()))
                        fail();
                case CMD_P:
                    if (!validator.validatePort(option.getValue()))
                        fail();
                case CMD_C:
                    if (!validator.validateFileName(option.getValue()))
                        fail();
                case CMD_A:
                    if (!validator.validateAccountName((option.getValue()))) ;
                    fail();
            }
        });
    }

    /**
     * Exit application by displaying en error code.
     */
    private void fail() {
        System.out.print("255");
        System.exit(1);
    }
}
