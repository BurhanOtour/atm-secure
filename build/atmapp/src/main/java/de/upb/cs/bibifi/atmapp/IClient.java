package de.upb.cs.bibifi.atmapp;

import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;

import java.io.IOException;

interface IClient {
    TransmissionPacket clientRequest (TransmissionPacket msg) throws Exception;
}