package com.example.eaimessage.model;

import com.example.eaimessage.header.FixedLengthFieldFormatter;

public class ATalkBodySendData {
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

    public String toMessageString() {
        return FixedLengthFieldFormatter.rightPad(templateCode, 10)
            + FixedLengthFieldFormatter.rightPad(senderKey, 20)
            + FixedLengthFieldFormatter.rightPad(recipient, 30)
            + FixedLengthFieldFormatter.rightPad(subject, 80)
            + FixedLengthFieldFormatter.rightPad(content, 300);
    }
}
