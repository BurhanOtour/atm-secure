package de.upb.cs.bibifi.atmapp.util;

import de.upb.cs.bibifi.atmapp.atm.impl.Atm;
import de.upb.cs.bibifi.atmapp.data.AtmInput;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.validator.Validator;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class CommandLineHandler {

    public static final String CMD_S = "s";
    public static final String CMD_C = "c";
    public static final String CMD_A = "a";
    public static final String CMD_D = "d";
    public static final String CMD_W = "w";
    public static final String CMD_N = "n";
    public static final String CMD_I = "i";
    public static final String CMD_P = "p";
    public static final String CMD_G = "g";

    private CommandLine commandLine;

    private String[] args = null;

    private TransmissionPacket packet;

    private final Validator validator;

    public CommandLineHandler(String[] args) {
        this.args = args;
        this.validator = new Validator();
        init();

        //check arguments length
        if (!this.validator.validateArgumentLength(this.args))
            fail();
    }

    private String cardFileName;

    private String ip;

    Integer port;

    /**
     * Initialize Atm and validate parameters
     */
    public void init() {

        CommandLineParser commandLineParser = new DefaultParser();

        HelpFormatter formatter = new HelpFormatter();

        Options options = new Options();

        Option required = new Option(CMD_A, "account", true, "initial account");
        required.setRequired(true);
        options.addOption(required);
        options.addOption(CMD_S, "auth", true, "authentication file");
        options.addOption(CMD_C, "card", true, "user's bank card");
        options.addOption(CMD_D, "deposit", true, "deposit amount");
        options.addOption(CMD_W, "withdrawal", true, "withdrawal amount");
        options.addOption(CMD_I, "ip", true, "initial ip");
        options.addOption(CMD_P, "port", true, "initial port");
        options.addOption(CMD_N, "initial", true, "initial balance");
        options.addOption(CMD_G, "checkbalance");


        try {

            commandLine = commandLineParser.parse(options, args);

            //validate all commandline parameters
            applyValidators(commandLine.getOptions());

            // Retrieve the AuthFile Content
            String key = AuthFile.getAuthFile(commandLine.getOptionValue(CMD_S)).getKey();

            EncryptionImpl.initialize(key);

            //ip, port and their default values
            this.ip = commandLine.getOptionValue("ip", "127.0.0.1");
            this.port = Integer.parseInt(commandLine.getOptionValue("port", "3000"));

        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(255);
            System.exit(1);
        }

    }

    private String readCardFile() {
        String pin = null;
        try {
            pin = EncryptionImpl.getInstance().decryptMessage(FileUtils.readFileToString(new File(commandLine.getOptionValue(CMD_C)), "UTF-8"));
        } catch (Exception e) {
            System.exit(255);
        }
        return pin;
    }


    /**
     * Call businesslogic methods based on input parameters
     */
    private void setTransmissionPacket(TransmissionPacket packet) {
        this.packet = packet;
    }

    public TransmissionPacket getPacket() {
        return packet;
    }

    public Integer getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public CommandLineHandler processCommandLineArguments() {

        Option[] options = commandLine.getOptions();
        Set<String> opts = new HashSet<>();

        Arrays.stream(commandLine.getOptions()).forEach(option -> {
            opts.add(option.getOpt());
        });
        if (!validator.checkOperations(opts)) {
            fail();
        }
        this.cardFileName = commandLine.getOptionValue(CMD_C);
        Arrays.stream(commandLine.getOptions()).forEach(option -> {
            AtmInput input;
            switch (option.getOpt()) {
                case CMD_N:
                    input = new AtmInput(commandLine, null);
                    setTransmissionPacket(Atm.createAccount(input));
                    break;
                case CMD_D:
                    input = new AtmInput(commandLine, readCardFile());
                    setTransmissionPacket(Atm.deposit(input));
                    break;
                case CMD_W:
                    input = new AtmInput(commandLine, readCardFile());
                    setTransmissionPacket(Atm.withdraw(input));
                    break;
                case CMD_G:
                    input = new AtmInput(commandLine, readCardFile());
                    setTransmissionPacket(Atm.checkBalance(input));
                    break;
            }
        });
        return this;
    }

    public String getCardFileName() {
        return cardFileName;
    }

    /**
     * Apply set of validators to all input parameters
     *
     * @param options
     * @return
     */
    private void applyValidators(Option[] options) {

        Set<String> duplicateOptionsSet = new HashSet<>();

        Arrays.stream(options).forEach(option -> {

            if (!validator.checkDuplicates(option.getOpt(), duplicateOptionsSet))
                fail();

            switch (option.getOpt()) {
                case CMD_N:
                    /*if (!validator.validateNumerals(option.getValue()) || !validator.validateInitialBalance(option.getValue()))
                        fail();*/
                    break;
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
                case CMD_S:
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
        System.exit(255);
    }
}
