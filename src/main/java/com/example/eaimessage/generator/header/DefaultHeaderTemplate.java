package com.example.eaimessage.generator.header;

import com.example.eaimessage.builder.FixedLengthFieldFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultHeaderTemplate {

    private static final Logger log = LoggerFactory.getLogger(DefaultHeaderTemplate.class);

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
        log.trace(
            "헤더 고정 길이 조립 systemCode={}, interfaceId={}, bodyUtf8Bytes={}",
            data.systemCode(),
            data.interfaceId(),
            data.bodyLength()
        );
        String sysCd = FixedLengthFieldFormatter.rightPad(safe(data.systemCode()), SYS_CD_LEN);
        String ifId = FixedLengthFieldFormatter.rightPad(safe(data.interfaceId()), IF_ID_LEN);
        String txId = FixedLengthFieldFormatter.rightPad(safe(data.transactionId()), TX_ID_LEN);
        String channelType = FixedLengthFieldFormatter.rightPad(safe(data.channelType()), CHANNEL_TYPE_LEN);
        String messageType = FixedLengthFieldFormatter.rightPad(safe(data.messageType()), MESSAGE_TYPE_LEN);
        String length = String.format("%0" + BODY_LENGTH_LEN + "d", Math.max(data.bodyLength(), 0));
        String headerTitle = FixedLengthFieldFormatter.rightPad(safe(data.title()), TITLE_LEN);
        String headerContent = FixedLengthFieldFormatter.rightPad(safe(data.content()), CONTENT_LEN);

        String header = TEST_TP_DSCD + sysCd + ifId + txId + channelType + messageType + length + headerTitle + headerContent;
        log.debug("헤더 문자열 생성 완료 totalChars={}, bodyUtf8Bytes={}", header.length(), data.bodyLength());
        return header;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
