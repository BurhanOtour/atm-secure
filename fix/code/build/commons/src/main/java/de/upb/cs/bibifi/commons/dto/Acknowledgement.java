package de.upb.cs.bibifi.commons.dto;

import java.util.UUID;

public class Acknowledgement {
    private String ackId;
    private String responseId;

    public Acknowledgement (String responseId){
        this.ackId = UUID.randomUUID().toString();
        this.responseId = responseId;
    }

    public void setAckId(String ackId) { this.ackId = ackId;}
    public String getAckId() {return ackId;}
    public void setResponseId(String responseId) {this.responseId = responseId;}
    public String getResponseId () {return responseId;}

}
