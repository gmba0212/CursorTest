package com.example.eaimessage.service;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.TalkRequest;
import com.example.eaimessage.service.bodyfield.BodyDisplayFields;
import com.example.eaimessage.service.bodyfield.BodyFieldResolveSupport;
import com.example.eaimessage.service.bodyfield.MessageBodyFieldResolverRegistry;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageContentComposer {

    private final MessageBodyFieldResolverRegistry bodyFieldResolverRegistry;

    public MessageContentComposer(MessageBodyFieldResolverRegistry bodyFieldResolverRegistry) {
        this.bodyFieldResolverRegistry = bodyFieldResolverRegistry;
    }

    public MessageContext compose(TalkRequest request, MessageContext rawContext) {
        Map<String, Object> map = new HashMap<>(rawContext.data());

        BodyDisplayFields fields = bodyFieldResolverRegistry.require(request.getMessageType()).resolve(request, rawContext);
        map.put("templateCode", fields.templateCode());
        map.put("subject", fields.subject());
        map.put("content", fields.content());

        applyChannelDefaults(request, map);
        return MessageContext.of(map);
    }

    private void applyChannelDefaults(TalkRequest request, Map<String, Object> map) {
        if (request.getChannelType() == null) {
            return;
        }
        switch (request.getChannelType()) {
            case EMAIL -> {
                map.put(
                    "subject",
                    BodyFieldResolveSupport.firstNonBlank((String) map.get("subject"), request.getTitle(), "[메일]")
                );
                map.put("receiverType", BodyFieldResolveSupport.firstNonBlank(request.getReceiverType(), "USER"));
            }
            case SMS -> {
                map.put(
                    "subject",
                    BodyFieldResolveSupport.firstNonBlank((String) map.get("subject"), request.getTitle(), "알림")
                );
                map.put(
                    "content",
                    BodyFieldResolveSupport.firstNonBlank((String) map.get("content"), request.getContent(), "SMS 안내 메시지입니다.")
                );
            }
            default -> {
            }
        }
    }
}
