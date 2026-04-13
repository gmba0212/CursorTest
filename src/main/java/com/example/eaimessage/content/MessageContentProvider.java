package com.example.eaimessage.content;

import com.example.eaimessage.model.MessageType;
import com.example.eaimessage.model.TalkRequest;

public interface MessageContentProvider {

    MessageType supportType();

    MessageContentDto getContent(TalkRequest request);
}
