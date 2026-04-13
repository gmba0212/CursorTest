package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;

public interface EaiBodyGenerator {

    MessageType supportMessageType();

    BodyGenerationResult generate(TalkRequest request);
}
