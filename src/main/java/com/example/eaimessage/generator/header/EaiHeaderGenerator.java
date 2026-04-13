package com.example.eaimessage.generator.header;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.TalkRequest;

public interface EaiHeaderGenerator {

    ChannelType supportChannelType();

    String generate(TalkRequest request, String title, String content, int bodyLength);
}
