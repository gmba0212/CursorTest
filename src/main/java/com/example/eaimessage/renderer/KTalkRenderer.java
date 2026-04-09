package com.example.eaimessage.renderer;

import com.example.eaimessage.model.ATalkBodySendData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class KTalkRenderer extends AbstractChannelRendererSupport implements ChannelMessageRenderer {

    @Override
    public ChannelType channelType() {
        return ChannelType.KTALK;
    }

    @Override
    public String renderBody(TalkRequest request, ServiceData serviceData, MessageContent content) {
        ATalkBodySendData body = new ATalkBodySendData();
        body.setSenderKey(param(request, "senderKey", "DEFAULT_KTALK_KEY"));
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
