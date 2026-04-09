package com.example.eaimessage.header;

import org.springframework.stereotype.Component;

/**
 * 기존 Header 생성 소스(고정길이 + String.format 패딩)를 재사용하는 역할.
 */
@Component
public class EaiHeaderGenerator {
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

    public String create(
        String transactionId,
        String channelCode,
        String messageTypeCode,
        int bodyLength
    ) {
        String txId = String.format("%-" + TX_ID_LEN + "s", safe(transactionId));
        String channel = String.format("%-" + CHANNEL_LEN + "s", safe(channelCode));
        String messageType = String.format("%-" + MESSAGE_TYPE_LEN + "s", safe(messageTypeCode));
        String length = String.format("%0" + BODY_LENGTH_LEN + "d", Math.max(bodyLength, 0));

        StringBuilder sb = new StringBuilder();
        sb.append(TEST_TP_DSCD);
        sb.append(SYS_CD);
        sb.append(IF_ID);
        sb.append(txId);
        sb.append(channel);
        sb.append(messageType);
        sb.append(length);
        return sb.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
