package com.example.eaimessage.header;

/**
 * 기존 Header 생성 소스(고정길이 + String.format 패딩)를 재사용하는 역할.
 */
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
        return TEST_TP_DSCD
            + String.format("%-" + SYS_CD_LEN + "s", SYS_CD)
            + String.format("%-" + IF_ID_LEN + "s", IF_ID)
            + String.format("%-" + TX_ID_LEN + "s", safe(transactionId))
            + String.format("%-" + CHANNEL_LEN + "s", safe(channelCode))
            + String.format("%-" + MESSAGE_TYPE_LEN + "s", safe(messageTypeCode))
            + String.format("%0" + BODY_LENGTH_LEN + "d", Math.max(bodyLength, 0));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
