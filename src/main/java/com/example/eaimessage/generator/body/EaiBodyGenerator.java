package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;

public interface EaiBodyGenerator {

    ChannelType supportChannelType();

    MessageType supportMessageType();

    BodyData generate(TalkRequest request);
}
