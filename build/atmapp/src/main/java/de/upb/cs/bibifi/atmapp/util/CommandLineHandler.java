package de.upb.cs.bibifi.atmapp.util;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import de.upd.cd.bibifi.commons.Validator;

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
        Validator validator = new Validator();
        Set<String> options = new HashSet<>();
        Arrays.stream(cmdLine.getOptions()).forEach(option -> {
            if (!checkDuplicates(option.getOpt(), options)) fail();
            if (CMD_D.equals(option.getOpt()) || CMD_W.equals(option.getOpt()) || CMD_N.equals(option.getOpt()))
                if (!validator.validateNumerals(option.getValue())) fail();
            if (CMD_I.equals(option.getOpt())) {
                if (!validator.validateIP(option.getValue()))
                    fail();
            }
            if (CMD_P.equals(option.getOpt())) {
                if (!validator.validatePort(option.getValue()))
                    fail();
            }
            if (CMD_C.equals(option.getOpt())|| CMD_S.equals(option.getOpt())) {
                if (!validator.validateFileName(option.getValue()))
                    fail();
            }
            if (CMD_A.equals(option.getOpt()) ) {
                if (!validator.validateAcountName(option.getValue()))
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
}
