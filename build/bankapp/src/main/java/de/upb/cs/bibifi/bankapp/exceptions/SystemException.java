package de.upb.cs.bibifi.bankapp.exceptions;

public class SystemException extends Exception{
    private String message;

    public SystemException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
