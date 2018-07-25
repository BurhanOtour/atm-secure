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

        options.addOption(CMD_S, "auth", true, "authentication file");
        options.addOption(CMD_C, "card", true, "user's bank card");
        options.addOption(CMD_A, "user", true, "user's name");
        options.addOption(CMD_D, "deposit", true, "deposit amount");
        options.addOption(CMD_W, "withdrawal", true, "withdrawal amount");
        options.addOption(CMD_N, "initial", true, "initial balance");

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
}
