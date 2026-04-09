package com.example.eaimessage.renderer;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageContent;
import com.example.eaimessage.model.ServiceData;
import com.example.eaimessage.model.TalkRequest;

public interface ChannelMessageRenderer {

    ChannelType channelType();

    String renderBody(TalkRequest request, ServiceData serviceData, MessageContent content);
}
