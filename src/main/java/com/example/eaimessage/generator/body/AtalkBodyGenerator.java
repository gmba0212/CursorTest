package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.ChannelType;
import org.springframework.stereotype.Component;

@Component
public class AtalkBodyGenerator extends DefaultEaiBodyGenerator {

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.A_TALK;
    }
}
