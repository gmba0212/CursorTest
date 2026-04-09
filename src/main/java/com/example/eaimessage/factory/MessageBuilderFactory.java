package com.example.eaimessage.factory;

import com.example.eaimessage.builder.MessageBuilder;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageBuilderFactory {

    private final List<MessageBuilder> builders;

    public MessageBuilderFactory(List<MessageBuilder> builders) {
        this.builders = builders;
    }

    public MessageBuilder getBuilder(MessageType messageType, ChannelType channelType) {
        if (messageType == null || channelType == null) {
            throw new IllegalArgumentException("messageType/channelType must not be null");
        }
        return builders.stream()
            .filter(builder -> builder.supports(channelType, messageType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "지원하지 않는 조합입니다. messageType=" + messageType + ", channelType=" + channelType));
    }
}
