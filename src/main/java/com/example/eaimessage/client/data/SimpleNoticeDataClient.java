package com.example.eaimessage.client.data;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SimpleNoticeDataClient implements MessageDataClient {

    @Override
    public MessageType supportedType() {
        return MessageType.SIMPLE_NOTICE;
    }

    @Override
    public MessageContext fetch(TalkRequest request) {
        return MessageContext.of(Map.of("notice", "simple"));
    }
}
