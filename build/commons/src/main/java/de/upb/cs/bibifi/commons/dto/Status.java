package de.upb.cs.bibifi.commons.dto;

import de.upb.cs.bibifi.commons.enums.StatusCode;

public class Status {
    private Integer statusCode;

    private String msg;

    public Status(){

    }

    public Status(Integer statusCode, String msg){
    this.statusCode = statusCode;
    this.msg = msg;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg
        ;
    }
}
