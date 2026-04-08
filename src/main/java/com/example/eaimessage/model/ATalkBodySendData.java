package com.example.eaimessage.model;

import com.example.eaimessage.header.FixedLengthFieldFormatter;

public class ATalkBodySendData {

    private static final int TEMPLATE_CODE_LEN = 10;
    private static final int SENDER_KEY_LEN = 20;
    private static final int RECIPIENT_LEN = 30;
    private static final int SUBJECT_LEN = 80;
    private static final int CONTENT_LEN = 300;

    private String templateCode = "";
    private String senderKey = "";
    private String recipient = "";
    private String subject = "";
    private String content = "";

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(String senderKey) {
        this.senderKey = senderKey;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String buildMessage() {
        return FixedLengthFieldFormatter.rightPad(templateCode, TEMPLATE_CODE_LEN)
            + FixedLengthFieldFormatter.rightPad(senderKey, SENDER_KEY_LEN)
            + FixedLengthFieldFormatter.rightPad(recipient, RECIPIENT_LEN)
            + FixedLengthFieldFormatter.rightPad(subject, SUBJECT_LEN)
            + FixedLengthFieldFormatter.rightPad(content, CONTENT_LEN);
    }

    public String toMessageString() {
        return buildMessage();
    }
}
