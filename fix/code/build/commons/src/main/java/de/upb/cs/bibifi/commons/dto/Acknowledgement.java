package de.upb.cs.bibifi.commons.dto;

import java.util.UUID;

public class Acknowledgement {
    private String ackId;
    private String responseId;

    public Acknowledgement (String responseId){
        this.ackId = UUID.randomUUID().toString();
        this.responseId = responseId;
    }

    public String getAckId() {return ackId;}

    public String getResponseId () {return responseId;}

    public void setResponseId(String id) { this.responseId = id; }

}
