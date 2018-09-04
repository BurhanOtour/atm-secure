package de.upb.cs.bibifi.commons.dto;

public class Response {
    private final String message;
    private final int code;
    private String requestId;

    public Response(String message, int code, String packetId) {
        this.message = message;
        this.code = code;
        this.requestId = packetId;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public String getRequestId() {
        return requestId;
    }

}
