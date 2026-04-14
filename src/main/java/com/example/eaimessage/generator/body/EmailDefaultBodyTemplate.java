package com.example.eaimessage.generator.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.model.ChannelType;
import org.springframework.stereotype.Component;

@Component
public class EmailDefaultBodyTemplate implements DefaultBodyTemplate {

    private static final int BODY_MESSAGE_TYPE_LEN = 30;
    private static final int BODY_RECEIVER_ID_LEN = 30;
    private static final int BODY_TITLE_LEN = 80;
    private static final int BODY_CONTENT_LEN = 300;

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public String generate(BodyData data) {
        String bodyMessageType = FixedLengthFieldFormatter.rightPad(safe(data.messageType()), BODY_MESSAGE_TYPE_LEN);
        String bodyReceiverId = FixedLengthFieldFormatter.rightPad(safe(data.receiverId()), BODY_RECEIVER_ID_LEN);
        String bodyTitle = FixedLengthFieldFormatter.rightPad(safe(data.title()), BODY_TITLE_LEN);
        String bodyContent = FixedLengthFieldFormatter.rightPad(safe(data.content()), BODY_CONTENT_LEN);

        return bodyMessageType + bodyReceiverId + bodyTitle + bodyContent;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
