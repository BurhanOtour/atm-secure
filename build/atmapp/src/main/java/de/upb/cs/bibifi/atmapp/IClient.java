package de.upb.cs.bibifi.atmapp;

import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;

import java.io.IOException;

interface IClient {
    //Client(ip, port)
    void clientRequest (TransmissionPacket request) throws IOException, Exception;
}