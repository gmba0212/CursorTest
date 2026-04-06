package com.example.eaimessage.model;

public class HttpSendRequest {
    private final String url;
    private final String payload;

    public HttpSendRequest(String url, String payload) {
        this.url = url;
        this.payload = payload;
    }

    public String getUrl() {
        return url;
    }

    public String getPayload() {
        return payload;
    }
}
