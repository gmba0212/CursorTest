package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.ChannelType;

public interface DefaultBodyTemplate {

    ChannelType supportChannelType();

    String generate(BodyData data);
}
