package de.upb.cs.bibifi.commons.dto;

import de.upb.cs.bibifi.commons.enums.RequestType;
import de.upb.cs.bibifi.commons.enums.StatusCode;

import java.util.HashMap;

public class TransmissionPacket {

    private RequestType requestType;

    private HashMap<String, String> properties;

    private Status status;

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        if (properties == null)
            properties = new HashMap<>();
        properties.put(key, value);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStatus(Integer statusCode, String msg) {
        status.setMessage(msg);
        status.setStatusCode(statusCode);
    }

}