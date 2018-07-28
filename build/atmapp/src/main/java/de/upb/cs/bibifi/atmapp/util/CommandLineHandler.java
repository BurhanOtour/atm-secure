package de.upb.cs.bibifi.atmapp.util;

import de.upb.cs.bibifi.atmapp.Client;
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

    private Atm atm = null;


    private String[] args = null;

    public CommandLineHandler(String[] args) {
        this.args = args;
    }

    /**
     * Initialize Atm and validate parameters
     */
    public void init() {

        CommandLineParser commandLineParser = new DefaultParser();

        HelpFormatter formatter = new HelpFormatter();

        Options options = new Options();

        options.addOption(CMD_A, "initial", true, "initial acount");
        options.addOption(CMD_S, "auth", true, "authentication file");
        options.addOption(CMD_C, "card", true, "user's bank card");
        options.addOption(CMD_D, "deposit", true, "deposit amount");
        options.addOption(CMD_W, "withdrawal", true, "withdrawal amount");
        options.addOption(CMD_I, "ip", true, "initial ip");
        options.addOption(CMD_P, "port", true, "initial port");
        options.addOption(CMD_N, "initial", true, "initial balance");
        options.addOption(CMD_G, "checkbalance");

        CommandLine commandLine = null;

        try {

            commandLine = commandLineParser.parse(options, args);

            //validate all commandline parameters
            applyValidators(commandLine.getOptions());

            //ip, port and their default values
            String ip = commandLine.getOptionValue("ip", "127.0.0.1");
            Integer port = Integer.parseInt(commandLine.getOptionValue("port", "25001"));

            this.atm = new Atm(new Client(ip, port));

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("255");
            System.exit(1);
        }

        processCommandLineArguments(commandLine);

    }

    /**
     * Call businesslogic methods based on input parameters
     *
     * @param cmdLine
     */
    private void processCommandLineArguments(CommandLine cmdLine) {

        Arrays.stream(cmdLine.getOptions()).forEach(option -> {

            switch (option.getOpt()) {
                case CMD_N:
                    atm.createAccount(cmdLine.getOptions());
                case CMD_D:
                    atm.deposit(cmdLine.getOptions());
                case CMD_W:
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
                    break;
                case CMD_I:
                    if (!validator.validateIP(option.getValue()))
                        fail();
                    break;
                case CMD_P:
                    if (!validator.validatePort(option.getValue()))
                        fail();
                    break;
                case CMD_C:
                    if (!validator.validateFileName(option.getValue()))
                        fail();
                    break;
                case CMD_A:
                    if (!validator.validateAccountName((option.getValue())))
                        fail();
                    break;
                default:
                    break;
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
