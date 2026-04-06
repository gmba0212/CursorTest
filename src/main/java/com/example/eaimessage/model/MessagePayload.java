package com.example.eaimessage.model;

public class MessagePayload {
    private final String payload;

    public MessagePayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
