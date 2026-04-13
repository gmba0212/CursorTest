package com.example.eaimessage.generator.header;

import com.example.eaimessage.generator.body.BodyData;
import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.TalkRequest;

public interface EaiHeaderGenerator {

    ChannelType supportChannelType();

    HeaderData generate(TalkRequest request, BodyData bodyData, int bodyLength);
}
