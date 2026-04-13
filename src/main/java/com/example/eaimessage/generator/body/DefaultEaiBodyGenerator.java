package com.example.eaimessage.generator.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.model.TalkRequest;

/**
 * 메시지 타입별 BodyGenerator가 공통으로 사용할 기본 포맷 유틸.
 */
public abstract class DefaultEaiBodyGenerator implements EaiBodyGenerator {

    private static final int BODY_MESSAGE_TYPE_LEN = 30;
    private static final int BODY_RECEIVER_ID_LEN = 30;
    private static final int BODY_TITLE_LEN = 80;
    private static final int BODY_CONTENT_LEN = 300;

    protected BodyGenerationResult generateWithDefaults(TalkRequest request, String title, String content) {
        String bodyMessageType = FixedLengthFieldFormatter.rightPad(request.getMessageType().name(), BODY_MESSAGE_TYPE_LEN);
        String bodyReceiverId = FixedLengthFieldFormatter.rightPad(request.getReceiverId(), BODY_RECEIVER_ID_LEN);
        String bodyTitle = FixedLengthFieldFormatter.rightPad(safe(title), BODY_TITLE_LEN);
        String bodyContent = FixedLengthFieldFormatter.rightPad(safe(content), BODY_CONTENT_LEN);
        return new BodyGenerationResult(bodyMessageType + bodyReceiverId + bodyTitle + bodyContent, safe(title), safe(content));
    }

    protected static String safe(String value) {
        return value == null ? "" : value;
    }
}
