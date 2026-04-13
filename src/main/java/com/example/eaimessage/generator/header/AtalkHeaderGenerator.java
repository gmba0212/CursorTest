package com.example.eaimessage.generator.header;

import com.example.eaimessage.generator.body.BodyData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.TalkRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class AtalkHeaderGenerator implements EaiHeaderGenerator {

    private static final DateTimeFormatter TX_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.A_TALK;
    }

    @Override
    public HeaderData generate(TalkRequest request, BodyData bodyData, int bodyLength) {
        return new HeaderData(
            "ATALKSYS",
            request.getChannelType().getChannelInterfaceId(),
            LocalDateTime.now().format(TX_ID_FORMAT),
            request.getChannelType().name(),
            request.getMessageType().name(),
            bodyLength,
            bodyData.title(),
            bodyData.content()
        );
    }
}
