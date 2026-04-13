package com.example.eaimessage.generator.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.content.MessageContentDto;
import com.example.eaimessage.model.TalkRequest;

/**
 * 채널별 BodyGenerator가 공통으로 사용할 기본 포맷 유틸.
 */
public abstract class DefaultEaiBodyGenerator implements EaiBodyGenerator {

    protected String commonBody(TalkRequest request, MessageContentDto contentDto) {
        return FixedLengthFieldFormatter.rightPad(request.getMessageType().name(), 30)
            + FixedLengthFieldFormatter.rightPad(request.getReceiverId(), 30)
            + FixedLengthFieldFormatter.rightPad(contentDto.title(), 80)
            + FixedLengthFieldFormatter.rightPad(contentDto.content(), 300);
    }
}
