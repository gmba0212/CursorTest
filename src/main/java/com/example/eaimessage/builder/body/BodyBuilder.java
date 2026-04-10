package com.example.eaimessage.builder.body;

import com.example.eaimessage.model.MessageContext;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;

public interface BodyBuilder {

    MessageType supportedType();

    String build(TalkRequest request, MessageContext context);
}
