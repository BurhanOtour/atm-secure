package de.upb.cs.bibifi.commons.dto;

import de.upb.cs.bibifi.commons.enums.RequestType;

import java.util.HashMap;
import java.util.UUID;

public class TransmissionPacket {

    private String pktId;

    private RequestType requestType;

    private HashMap<String, String> properties;

    public TransmissionPacket() {
        this.pktId = UUID.randomUUID().toString();
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        if (properties == null)
            properties = new HashMap<>();
        properties.put(key, value);
    }

    public void setPacketId(String id) {
        this.pktId = id;
    }

    public String getPacketId() {
        return pktId;
    }

}