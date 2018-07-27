package de.upb.cs.bibifi.commons.dto;

import de.upb.cs.bibifi.commons.enums.RequestType;

import java.util.HashMap;

public class Request{
    private RequestType requestType;
    HashMap<String,String> properties;

    public Request (){
        properties = new HashMap<String, String>();
    }

    // Getter Methods

    public RequestType getRequestType() {
        return requestType;
    }

    public String getProperty(String key, String value) {
        return properties.put(key, value);
    }

    // Setter Methods

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

}
