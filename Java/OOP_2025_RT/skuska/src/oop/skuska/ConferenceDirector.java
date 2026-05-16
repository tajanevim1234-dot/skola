package oop.skuska;

public class ConferenceDirector {
    private    EmailBuilderInterface builder;
    private    String senderAddress;
    private    String senderName;

    public ConferenceDirector(EmailBuilderInterface builder, String senderAddress, String senderName) {
        this.builder = builder;
        this.senderAddress = senderAddress;
        this.senderName = senderName;
    }

    public void invite(String receiverAddress, String receiverName,String conferenceName, String location,String date, String time){
        builder.setSender(senderAddress);
        builder.setReceiver(receiverAddress);
        builder.setSubject(String.format("%s (invitation)", conferenceName));

        builder.setContent(String.format("""
            Dear %s,
            We are pleased to invite you to our upcoming conference, %s.
            The conference is scheduled to take place on %s, at %s, in %s.
            Sincerely,
            %s
            """, receiverName, conferenceName, date, time, location, senderName));

    }

    public void confirm(String receiverAddress, String receiverName,String conferenceName){
        builder.setSender(senderAddress);
        builder.setReceiver(receiverAddress);
        builder.setSubject(String.format("%s (registration confirmation)", conferenceName));

        builder.setContent(String.format("""
            Dear %s,
            We are delighted to confirm your registration for the %s conference.
            Singecerly,
            %s
            """, receiverName, conferenceName, senderName));
    }
}
