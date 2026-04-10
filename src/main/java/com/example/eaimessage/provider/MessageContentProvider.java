package com.example.eaimessage.provider;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.PreparedMessageContent;
import com.example.eaimessage.model.TalkRequest;

public interface MessageContentProvider {

    MessageType supportedType();

    PreparedMessageContent provide(TalkRequest request);
}
