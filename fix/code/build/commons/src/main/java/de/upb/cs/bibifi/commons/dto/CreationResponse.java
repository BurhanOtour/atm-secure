package de.upb.cs.bibifi.commons.dto;

public class CreationResponse extends Response {

    private final String pin;

    public CreationResponse(String message, int code, String packetId, String pin) {
        super(message, code, packetId);
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }
}
