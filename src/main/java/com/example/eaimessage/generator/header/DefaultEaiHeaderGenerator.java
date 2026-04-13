package com.example.eaimessage.generator.header;

import com.example.eaimessage.model.TalkRequest;

/**
 * 채널별 HeaderGenerator가 공통으로 사용하는 기본 포맷 로직.
 */
public abstract class DefaultEaiHeaderGenerator implements EaiHeaderGenerator {

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

    @Override
    public String generate(TalkRequest request, String title, String content, int bodyLength) {
        String sysCd = String.format("%-" + SYS_CD_LEN + "s", safe(systemCode()));
        String ifId = String.format("%-" + IF_ID_LEN + "s", safe(request.getChannelType().getChannelInterfaceId()));
        String txId = String.format("%-" + TX_ID_LEN + "s", safe(transactionId(request)));
        String channelType = String.format("%-" + CHANNEL_TYPE_LEN + "s", request.getChannelType().name());
        String messageType = String.format("%-" + MESSAGE_TYPE_LEN + "s", request.getMessageType().name());
        String length = String.format("%0" + BODY_LENGTH_LEN + "d", Math.max(bodyLength, 0));
        String headerTitle = String.format("%-" + TITLE_LEN + "s", safe(title));
        String headerContent = String.format("%-" + CONTENT_LEN + "s", safe(content));

        return TEST_TP_DSCD + sysCd + ifId + txId + channelType + messageType + length + headerTitle + headerContent;
    }

    protected abstract String systemCode();

    protected abstract String transactionId(TalkRequest request);

    protected static String safe(String value) {
        return value == null ? "" : value;
    }
}
