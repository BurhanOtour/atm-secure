package de.upb.cs.bibifi.commons.dto;

import java.util.UUID;

public class Response {
    private final String message;
    private final int code;
    private String responseId;
    private String requestId;

    public Response(String message, int code, String packetId) {
        this.message = message;
        this.code = code;
        this.responseId = UUID.randomUUID().toString();
        this.requestId = packetId;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public void setResponseId(String id) { this.responseId = id; }

    public String getResponseId() { return responseId; }

    public String getRequestId() { return requestId; }

    public void setRequestId(String id) { this.requestId = id; }
}
