package de.upb.cs.bibifi.commons.impl;

import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.ITransmissionPacketProcessor;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TransmissionPacketProcessor implements ITransmissionPacketProcessor {

    private final IEncryption encryption;
    private static ITransmissionPacketProcessor processor = null;

    private TransmissionPacketProcessor() {
        this.encryption = null; // Get Singlton Instance
    }

    public static ITransmissionPacketProcessor getTransmissionPacketProcessor() {
        if (processor != null)
            return processor;

        processor = new TransmissionPacketProcessor();
        return processor;
    }

    @Override
    public OutputStream encryptMessage(TransmissionPacket packet) throws IOException {
        return encryption.encryptMessage(Utilities.Serializer(packet));
    }

    @Override
    public TransmissionPacket decryptMessage(InputStream inputStream) throws IOException {
        return Utilities.deserializer(encryption.decryptMessage(inputStream));
    }
}
