package com.example.eaimessage.generator.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.content.MessageContentDto;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class DefaultEaiBodyGenerator implements EaiBodyGenerator {

    @Override
    public String generate(TalkRequest request, MessageContentDto contentDto) {
        return FixedLengthFieldFormatter.rightPad(request.getMessageType().name(), 30)
            + FixedLengthFieldFormatter.rightPad(request.getReceiverId(), 30)
            + FixedLengthFieldFormatter.rightPad(contentDto.title(), 80)
            + FixedLengthFieldFormatter.rightPad(contentDto.content(), 300);
    }
}
