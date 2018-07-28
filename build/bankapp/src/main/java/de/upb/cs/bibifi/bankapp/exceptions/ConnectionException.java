package de.upb.cs.bibifi.bankapp.exceptions;

public class ConnectionException extends Exception {
    private String message;

    public ConnectionException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
