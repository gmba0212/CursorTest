package com.example.eaimessage.generator.body;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import com.example.eaimessage.model.BodyGenerationInput;
import com.example.eaimessage.model.ChannelType;
import org.springframework.stereotype.Component;

@Component
public class DefaultMessageBodyGenerator implements MessageBodyGenerator {

    @Override
    public String generate(BodyGenerationInput input) {
        if (input.channelType() == ChannelType.KTALK) {
            return formatKtalk(input);
        }
        if (input.channelType() == ChannelType.EMAIL) {
            return formatEmail(input);
        }
        if (input.channelType() == ChannelType.SMS) {
            return formatSms(input);
        }
        throw new IllegalArgumentException("Unsupported channelType: " + input.channelType());
    }

    private String formatKtalk(BodyGenerationInput input) {
        return FixedLengthFieldFormatter.rightPad(input.templateCode(), 10)
            + FixedLengthFieldFormatter.rightPad(input.senderKey(), 20)
            + FixedLengthFieldFormatter.rightPad(input.receiverAddress(), 30)
            + FixedLengthFieldFormatter.rightPad(input.subject(), 80)
            + FixedLengthFieldFormatter.rightPad(input.content(), 300);
    }

    private String formatEmail(BodyGenerationInput input) {
        return FixedLengthFieldFormatter.rightPad(input.subject(), 80)
            + FixedLengthFieldFormatter.rightPad(input.receiverType(), 10)
            + FixedLengthFieldFormatter.rightPad(input.receiverAddress(), 80)
            + FixedLengthFieldFormatter.rightPad(input.receiverId(), 20)
            + FixedLengthFieldFormatter.rightPad(input.content(), 400);
    }

    private String formatSms(BodyGenerationInput input) {
        return FixedLengthFieldFormatter.rightPad(ChannelType.SMS.name(), 10)
            + FixedLengthFieldFormatter.rightPad(input.receiverType(), 10)
            + FixedLengthFieldFormatter.rightPad(input.receiverAddress(), 30)
            + FixedLengthFieldFormatter.rightPad(input.receiverId(), 20)
            + FixedLengthFieldFormatter.rightPad(input.subject(), 80)
            + FixedLengthFieldFormatter.rightPad(input.content(), 300);
    }
}
