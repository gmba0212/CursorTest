package com.example.eaimessage.generator.header;

import com.example.eaimessage.generator.body.BodyData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.TalkRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailHeaderGenerator implements EaiHeaderGenerator {

    private static final Logger log = LoggerFactory.getLogger(EmailHeaderGenerator.class);

    private static final DateTimeFormatter TX_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public HeaderData generate(TalkRequest request, BodyData bodyData, int bodyLength) {
        log.debug(
            "EMAIL 헤더 데이터 생성 messageType={}, bodyUtf8Bytes={}, receiverId={}",
            request.getMessageType(),
            bodyLength,
            request.getReceiverId()
        );
        return new HeaderData(
            "EMAILSYS",
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
