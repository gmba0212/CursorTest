package com.example.eaimessage.renderer;

import com.example.eaimessage.config.KTalkProperties;
import com.example.eaimessage.model.ATalkBodySendData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class KTalkRenderer extends AbstractChannelRendererSupport implements ChannelMessageRenderer {

    private final KTalkProperties kTalkProperties;

    public KTalkRenderer(KTalkProperties kTalkProperties) {
        this.kTalkProperties = kTalkProperties;
    }

    @Override
    public ChannelType channelType() {
        return ChannelType.KTALK;
    }

    @Override
    public String renderBody(TalkRequest request, ServiceData serviceData, MessageContent content) {
        ATalkBodySendData body = new ATalkBodySendData();
        body.setSenderKey(defaultString(kTalkProperties.getSenderKey()));
        body.setRecipient(defaultString(request.getReceiverAddress()));
        body.setTemplateCode(content.getTemplateCode());
        String override = content.getExtras().get("templateCodeOverride");
        if (override != null && !override.isBlank()) {
            body.setTemplateCode(override);
        }
        body.setSubject(content.getSubject());
        body.setContent(content.getBodyText());
        return body.buildMessage();
    }
}
