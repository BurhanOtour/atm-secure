package de.upb.cs.bibifi.commons.dto;

import de.upb.cs.bibifi.commons.enums.RequestType;

import java.util.HashMap;

public class TransmissionPacket {

    private RequestType requestType;

    private HashMap<String, String> properties = new HashMap<>();

    private Integer statusCode;

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

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getProperty (String key){ return properties.get(key);}

    public void setProperty (String key, String value) {properties.put(key, value);}
}