package com.example.eaimessage.generator.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.model.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ATalkDefaultBodyTemplate implements DefaultBodyTemplate {

    private static final Logger log = LoggerFactory.getLogger(ATalkDefaultBodyTemplate.class);

    private static final int BODY_MESSAGE_TYPE_LEN = 30;
    private static final int BODY_RECEIVER_ID_LEN = 30;
    private static final int BODY_TITLE_LEN = 80;
    private static final int BODY_CONTENT_LEN = 300;

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.A_TALK;
    }

    @Override
    public String generate(BodyData data) {
        log.trace("A_TALK 본문 템플릿 조립 messageType={}, receiverId={}", data.messageType(), data.receiverId());
        String bodyMessageType = FixedLengthFieldFormatter.rightPad(safe(data.messageType()), BODY_MESSAGE_TYPE_LEN);
        String bodyReceiverId = FixedLengthFieldFormatter.rightPad(safe(data.receiverId()), BODY_RECEIVER_ID_LEN);
        String bodyTitle = FixedLengthFieldFormatter.rightPad(safe(data.title()), BODY_TITLE_LEN);
        String bodyContent = FixedLengthFieldFormatter.rightPad(safe(data.content()), BODY_CONTENT_LEN);

        String body = bodyMessageType + bodyReceiverId + bodyTitle + bodyContent;
        log.debug("A_TALK 본문 문자열 생성 완료 totalChars={}", body.length());
        return body;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
