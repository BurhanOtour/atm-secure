package de.upb.cs.bibifi.atmapp.atm.processors;

import de.upb.cs.bibifi.commons.dto.TransmissionPacket;

public class MonetaryTransaction implements ITransactionProcessor {

    private TransmissionPacket transmissionPacket;


    public MonetaryTransaction(TransmissionPacket transmissionPacket) {
        this.transmissionPacket = transmissionPacket;
    }

    @Override
    public void process() {

        prepareTransaction();

        commitTransaction();
    }

    @Override
    public void prepareTransaction() {

    }

    @Override
    public void commitTransaction() {

    }
}
