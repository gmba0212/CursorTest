package com.example.eaimessage.builder.content;

import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;

public interface MessageContentBuilder {

    MessageType supportedType();

    MessageContent build(TalkRequest request, ServiceData serviceData);
}
