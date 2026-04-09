package com.example.eaimessage.resolver;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SimpleNoticeDataResolver implements MessageDataResolver {

    @Override
    public MessageType supportedType() {
        return MessageType.SIMPLE_NOTICE;
    }

    @Override
    public ServiceData resolve(TalkRequest request) {
        return new ServiceData(Map.of("notice", "simple"));
    }
}
