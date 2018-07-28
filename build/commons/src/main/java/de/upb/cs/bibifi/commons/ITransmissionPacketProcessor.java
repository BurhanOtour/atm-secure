package de.upb.cs.bibifi.commons;

import de.upb.cs.bibifi.commons.dto.TransmissionPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ITransmissionPacketProcessor {
    /**
     * @param inputStream
     * @return
     * @throws IOException
     * Decrypts the inputStream into a TransmissionPacket
     */
    TransmissionPacket decryptMessage(InputStream inputStream) throws IOException;

    /**
     * @param packet
     * @return
     * @throws IOException
     * Encrypts the inputStream into a TransmissionPacket
     */
    OutputStream encryptMessage(TransmissionPacket packet) throws IOException;

}

