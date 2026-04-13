package com.example.eaimessage.generator.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.content.MessageContentDto;
import com.example.eaimessage.model.TalkRequest;

/**
 * 채널별 BodyGenerator가 공통으로 사용할 기본 포맷 유틸.
 */
public abstract class DefaultEaiBodyGenerator implements EaiBodyGenerator {

    private static final int BODY_MESSAGE_TYPE_LEN = 30;
    private static final int BODY_RECEIVER_ID_LEN = 30;
    private static final int BODY_TITLE_LEN = 80;
    private static final int BODY_CONTENT_LEN = 300;

    @Override
    public String generate(TalkRequest request, MessageContentDto contentDto) {
        String bodyMessageType = FixedLengthFieldFormatter.rightPad(request.getMessageType().name(), BODY_MESSAGE_TYPE_LEN);
        String bodyReceiverId = FixedLengthFieldFormatter.rightPad(request.getReceiverId(), BODY_RECEIVER_ID_LEN);
        String bodyTitle = FixedLengthFieldFormatter.rightPad(safe(contentDto.title()), BODY_TITLE_LEN);
        String bodyContent = FixedLengthFieldFormatter.rightPad(safe(contentDto.content()), BODY_CONTENT_LEN);
        return bodyMessageType + bodyReceiverId + bodyTitle + bodyContent;
    }

    protected static String safe(String value) {
        return value == null ? "" : value;
    }
}
