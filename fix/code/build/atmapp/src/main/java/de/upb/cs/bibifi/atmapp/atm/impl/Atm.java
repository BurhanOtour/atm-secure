package de.upb.cs.bibifi.atmapp.atm.impl;

import de.upb.cs.bibifi.atmapp.atm.RequestProcessor;
import de.upb.cs.bibifi.atmapp.data.AtmInput;
import de.upb.cs.bibifi.atmapp.util.CommandLineHandler;
import de.upb.cs.bibifi.commons.constants.SharedConstants;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;

public class Atm {

    public static TransmissionPacket createAccount(AtmInput input) {
        return RequestProcessor.generateRequest(
                RequestType.CREATE, getAccauntName(input) ,
                Double.parseDouble(input.getCommandLine().getOptionValue(SharedConstants.CMD_N)), null);
    }

    public static TransmissionPacket deposit(AtmInput input) {
        return RequestProcessor.generateRequest(
                RequestType.DEPOSIT, getAccauntName(input),
                Double.parseDouble(input.getCommandLine().getOptionValue(SharedConstants.CMD_D)), input.getPIN());
    }

    public static TransmissionPacket withdraw(AtmInput input) {
        return RequestProcessor.generateRequest(
                RequestType.WITHDRAW, getAccauntName(input),
                Double.parseDouble(input.getCommandLine().getOptionValue(SharedConstants.CMD_W)), input.getPIN());
    }

    public static TransmissionPacket checkBalance(AtmInput input) {
        return RequestProcessor.generateRequest(
                RequestType.CHECKBALANCE, getAccauntName(input),
                0, input.getPIN());
    }

    private static String getAccauntName(AtmInput input){
        return CommandLineHandler.removeHash(input.getCommandLine().getOptionValue(SharedConstants.CMD_A));
    }
}
