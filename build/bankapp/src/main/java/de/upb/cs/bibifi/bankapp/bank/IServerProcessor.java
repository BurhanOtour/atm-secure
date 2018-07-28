package de.upb.cs.bibifi.bankapp.bank;

import de.upb.cs.bibifi.commons.dto.TransmissionPacket;

public interface IServerProcessor {
    String executeOperation(TransmissionPacket packet);
}
