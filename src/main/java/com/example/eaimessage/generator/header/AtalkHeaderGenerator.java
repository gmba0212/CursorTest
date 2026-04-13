package com.example.eaimessage.generator.header;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.TalkRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class AtalkHeaderGenerator extends DefaultEaiHeaderGenerator {

    private static final DateTimeFormatter TX_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.A_TALK;
    }

    @Override
    protected String systemCode() {
        return "ATALKSYS";
    }

    @Override
    protected String interfaceId() {
        return "ATK0001";
    }

    @Override
    protected String transactionId(TalkRequest request) {
        return LocalDateTime.now().format(TX_ID_FORMAT);
    }
}
