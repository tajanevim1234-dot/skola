package oop.skuska;

public class EmailBuilder implements EmailBuilderInterface {

    private String sender;
    private String receiver;
    private String subject;
    private String content;
    private byte[] attachment;

    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    @Override
    public void reset() {
        sender = null;
        receiver = null;
        subject = null;
        content = null;
        attachment = null;
    }

    @Override
    public Email build() throws EmailNotBuildableException {
        
        if (sender == null || receiver == null || content == null) {
            reset();
            throw new EmailNotBuildableException("Email must have sender, receiver and content");
        }
        
        Email email = new Email(sender, receiver, subject, content, attachment);
        reset();
        return email;
    }
}
