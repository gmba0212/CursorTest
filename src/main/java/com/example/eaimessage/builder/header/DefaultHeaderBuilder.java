package com.example.eaimessage.builder.header;

import com.example.eaimessage.model.ChannelType;
import com.example.eaimessage.model.MessageType;
import org.springframework.stereotype.Component;

@Component
public class DefaultHeaderBuilder implements HeaderBuilder {

    private static final int TEST_TP_DSCD_LEN = 1;
    private static final int SYS_CD_LEN = 10;
    private static final int IF_ID_LEN = 10;
    private static final int TX_ID_LEN = 20;
    private static final int CHANNEL_LEN = 10;
    private static final int MESSAGE_TYPE_LEN = 30;
    private static final int BODY_LENGTH_LEN = 8;

    private static final String TEST_TP_DSCD = String.format("%-" + TEST_TP_DSCD_LEN + "s", "0");
    private static final String SYS_CD = String.format("%-" + SYS_CD_LEN + "s", "SYSTEM");
    private static final String IF_ID = String.format("%-" + IF_ID_LEN + "s", "IF0001");

    @Override
    public String build(String transactionId, ChannelType channelType, MessageType messageType, int bodyLength) {
        String txId = String.format("%-" + TX_ID_LEN + "s", safe(transactionId));
        String channel = String.format("%-" + CHANNEL_LEN + "s", channelType == null ? "" : channelType.name());
        String type = String.format("%-" + MESSAGE_TYPE_LEN + "s", messageType == null ? "" : messageType.name());
        String length = String.format("%0" + BODY_LENGTH_LEN + "d", Math.max(bodyLength, 0));

        return TEST_TP_DSCD + SYS_CD + IF_ID + txId + channel + type + length;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
