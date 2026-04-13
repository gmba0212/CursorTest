package com.example.eaimessage.generator.body;

import com.example.eaimessage.content.MessageContentDto;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.TalkRequest;
import org.springframework.stereotype.Component;

@Component
public class AtalkBodyGenerator extends DefaultEaiBodyGenerator {

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.A_TALK;
    }

    @Override
    public String generate(TalkRequest request, MessageContentDto contentDto) {
        return commonBody(request, contentDto);
    }
}
