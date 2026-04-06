package com.example.eaimessage.model;

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
        return padRight(templateCode, 10)
            + padRight(senderKey, 20)
            + padRight(recipient, 15)
            + padRight(subject, 80)
            + padRight(content, 300);
    }

    private String padRight(String value, int length) {
        String normalized = value == null ? "" : value;
        if (normalized.length() >= length) {
            return normalized.substring(0, length);
        }
        return normalized + " ".repeat(length - normalized.length());
    }
}
