package oop.skuska;

public interface EmailBuilderInterface {
    void setSender(String sender);
    void setReceiver(String receiver);
    void setSubject(String subject);
    void setContent(String content);
    void setAttachment(byte[] attachment);

    void reset();

    Email build() throws EmailNotBuildableException;
}
