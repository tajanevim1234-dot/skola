package oop.skuska;

public class EmailBuilder implements EmailBuilderInterface {
    private String sender;
    private String receiver;
    private String subject;
    private String content;
    private byte[] attachment;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public void reset() {
        this.sender = null;
        this.receiver = null;
        this.subject = null;
        this.content = null;
        this.attachment = null;
    }

    public Email build() throws EmailNotBuildableException {
        if (sender == null || receiver == null || content == null) {
            throw new EmailNotBuildableException("exception");
        }
        Email email = new Email(sender, receiver, subject, content, attachment);
        reset();
        return email;
    }
}
