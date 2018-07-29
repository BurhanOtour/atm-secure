package de.upb.cs.bibifi.atmapp.atm.impl;

import de.upb.cs.bibifi.atmapp.atm.RequestProcessor;
import de.upb.cs.bibifi.atmapp.data.AtmInput;
import de.upb.cs.bibifi.atmapp.util.CommandLineHandler;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;

import javax.xml.transform.dom.DOMSource;

public class Atm {


    public static TransmissionPacket createAccount(AtmInput input) {
        return RequestProcessor.generateRequest(
                RequestType.CREATE, input.getCommandLine().getOptionValue(CommandLineHandler.CMD_A),
                Double.parseDouble(input.getCommandLine().getOptionValue(CommandLineHandler.CMD_N)), null);
    }

    public static TransmissionPacket deposit(AtmInput input) {
        return RequestProcessor.generateRequest(
                RequestType.DEPOSIT, input.getCommandLine().getOptionValue(CommandLineHandler.CMD_A),
                Double.parseDouble(input.getCommandLine().getOptionValue(CommandLineHandler.CMD_D)), input.getPIN());
    }

    public static TransmissionPacket withdraw(AtmInput input) {
        return RequestProcessor.generateRequest(
                RequestType.WITHDRAW, input.getCommandLine().getOptionValue(CommandLineHandler.CMD_A),
                Double.parseDouble(input.getCommandLine().getOptionValue(CommandLineHandler.CMD_W)), input.getPIN());
    }

    public static TransmissionPacket checkBalance(AtmInput input) {
        return RequestProcessor.generateRequest(
                RequestType.CHECKBALANCE, input.getCommandLine().getOptionValue(CommandLineHandler.CMD_A),
                0, input.getPIN());
    }
}
