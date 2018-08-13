package models;

import com.squareup.moshi.Json;

public class Request {
    @Json(name = "REQUEST")
    private final String request;

    public Request(String request) {
        this.request = request;
    }
}
