package com.example.eaimessage.model;

/**
 * channelType + messageType 조합 키.
 */
public record MessageRouteKey(ChannelType channelType, MessageType messageType) {
}
