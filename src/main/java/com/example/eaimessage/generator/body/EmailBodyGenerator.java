package com.example.eaimessage.generator.body;

import com.example.eaimessage.model.ChannelType;
import org.springframework.stereotype.Component;

@Component
public class EmailBodyGenerator extends DefaultEaiBodyGenerator {

    @Override
    public ChannelType supportChannelType() {
        return ChannelType.EMAIL;
    }
}
