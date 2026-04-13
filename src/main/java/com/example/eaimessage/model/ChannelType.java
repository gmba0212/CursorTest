package com.example.eaimessage.model;

public enum ChannelType {
    A_TALK("ATK0001"),
    EMAIL("EML0001");

    private final String channelInterfaceId;

    ChannelType(String channelInterfaceId) {
        this.channelInterfaceId = channelInterfaceId;
    }

    public String getChannelInterfaceId() {
        return channelInterfaceId;
    }
}
