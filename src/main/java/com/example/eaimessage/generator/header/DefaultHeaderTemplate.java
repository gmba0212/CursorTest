package com.example.eaimessage.generator.header;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import org.springframework.stereotype.Component;

@Component
public class DefaultHeaderTemplate {

    private static final int TEST_TP_DSCD_LEN = 1;
    private static final int SYS_CD_LEN = 10;
    private static final int IF_ID_LEN = 10;
    private static final int TX_ID_LEN = 20;
    private static final int CHANNEL_TYPE_LEN = 20;
    private static final int MESSAGE_TYPE_LEN = 30;
    private static final int BODY_LENGTH_LEN = 8;
    private static final int TITLE_LEN = 80;
    private static final int CONTENT_LEN = 200;

    private static final String TEST_TP_DSCD = String.format("%-" + TEST_TP_DSCD_LEN + "s", "0");

    public String generate(HeaderData data) {
        String sysCd = FixedLengthFieldFormatter.rightPad(safe(data.systemCode()), SYS_CD_LEN);
        String ifId = FixedLengthFieldFormatter.rightPad(safe(data.interfaceId()), IF_ID_LEN);
        String txId = FixedLengthFieldFormatter.rightPad(safe(data.transactionId()), TX_ID_LEN);
        String channelType = FixedLengthFieldFormatter.rightPad(safe(data.channelType()), CHANNEL_TYPE_LEN);
        String messageType = FixedLengthFieldFormatter.rightPad(safe(data.messageType()), MESSAGE_TYPE_LEN);
        String length = String.format("%0" + BODY_LENGTH_LEN + "d", Math.max(data.bodyLength(), 0));
        String headerTitle = FixedLengthFieldFormatter.rightPad(safe(data.title()), TITLE_LEN);
        String headerContent = FixedLengthFieldFormatter.rightPad(safe(data.content()), CONTENT_LEN);

        return TEST_TP_DSCD + sysCd + ifId + txId + channelType + messageType + length + headerTitle + headerContent;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
