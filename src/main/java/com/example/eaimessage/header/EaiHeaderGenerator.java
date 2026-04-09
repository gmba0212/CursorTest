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

    private static final String TEST_TP_DSCD = FixedLengthFieldFormatter.rightPad("0", TEST_TP_DSCD_LEN);
    private static final String SYS_CD = FixedLengthFieldFormatter.rightPad("SYSTEM", SYS_CD_LEN);
    private static final String IF_ID = FixedLengthFieldFormatter.rightPad("IF0001", IF_ID_LEN);

    public String create(
        String transactionId,
        String channelCode,
        String messageTypeCode,
        int bodyLength
    ) {
        String txId = FixedLengthFieldFormatter.rightPad(transactionId, TX_ID_LEN);
        String channel = FixedLengthFieldFormatter.rightPad(channelCode, CHANNEL_LEN);
        String messageType = FixedLengthFieldFormatter.rightPad(messageTypeCode, MESSAGE_TYPE_LEN);
        String length = FixedLengthFieldFormatter.zeroPadNumber(bodyLength, BODY_LENGTH_LEN);

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
}
