package oop.skuska;

public class Email {
    private final String sender;
    private final String receiver;
    private final String subject;
    private final String content;
    private final byte[] attachment;

    Email(String sender, String receiver, String subject, String content, byte[] attachment) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.attachment = attachment;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public byte[] getAttachment() {
        return attachment;
    }



}
