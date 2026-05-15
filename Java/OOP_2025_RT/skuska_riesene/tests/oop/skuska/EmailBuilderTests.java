package oop.skuska;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnotherEmailBuilder implements EmailBuilderInterface {
    @Override
    public void setSender(String sender) {
    }
    @Override
    public void setReceiver(String receiver) {
    }
    @Override
    public void setSubject(String subject) {
    }
    @Override
    public void setContent(String content) {
    }
    @Override
    public void setAttachment(byte[] attachment) {
    }
    @Override
    public void reset() {
    }
    @Override
    public Email build() throws EmailNotBuildableException {
        throw new UnsupportedOperationException();
    }
}

public class EmailBuilderTests {

    // ---------- email ---------------------------------------------------------------------------

    @Test
    void emailMethodsCompilation_1() {
        Email email = new Email(null, null, null, null, null);
        String sender = email.getSender();
        String receiver = email.getReceiver();
        String subject = email.getSubject();
        String content = email.getContent();
        byte[] attachment = email.getAttachment();
    }

    @Test
    void emailAttributes_1() {
        Field[] fields = Email.class.getDeclaredFields();
        for (Field f: fields) {
            assertTrue(Modifier.isPrivate(f.getModifiers()), "Email musi obsahovat len private atributy");
        }
    }

    @Test
    void emailConstructor_1() {
        Constructor<?>[] constructors = Email.class.getDeclaredConstructors();
        assertEquals(1, constructors.length, "Email musi mat prave jeden konstruktor");
        int modifier = constructors[0].getModifiers();
        assertFalse(Modifier.isPrivate(modifier) || Modifier.isProtected(modifier) || Modifier.isPublic(modifier), "Email.constructor musi byt mozene volat len v jednom baliku (v ktorom je trieda Email definovana)");
    }

    // ---------- email builder -------------------------------------------------------------------

    @Test
    void builderMethodsCompilation_1() throws EmailNotBuildableException{
        EmailBuilderInterface builder = new EmailBuilder();
        builder.setSender("sender");
        builder.setReceiver("receiver");
        builder.setSubject("subject");
        builder.setContent("conent");
        builder.setAttachment(new byte[] {0x10, 0x20});
        Email e = builder.build();
        builder.reset();
    }

    @Test
    void builderAttributes_1() {
        Field[] fields = EmailBuilder.class.getDeclaredFields();
        for (Field f: fields) {
            assertTrue(Modifier.isPrivate(f.getModifiers()), "EmailBuilder musi obsahovat len private atributy");
        }
    }

    @Test
    void buildWithParam_8() throws EmailNotBuildableException {
        EmailBuilderInterface builder = new EmailBuilder();
        builder.setSender("sender@stuba.sk");
        builder.setReceiver("receiver@stuba.sk");
        builder.setSubject("subject of the message");
        builder.setContent("content of this message");
        builder.setAttachment(new byte[]{0x10, 0x20, 0x30, 0x40, 0x50});

        try {
            Email email = builder.build();
            assertEquals("sender@stuba.sk", email.getSender());
            assertEquals("receiver@stuba.sk", email.getReceiver());
            assertEquals("subject of the message", email.getSubject());
            assertEquals("sender@stuba.sk", email.getSender());
            assertEquals("content of this message", email.getContent());
            assertTrue(Arrays.equals(new byte[]{0x10, 0x20, 0x30, 0x40, 0x50}, email.getAttachment()));
        }
        catch (EmailNotBuildableException e) {
            assertTrue(false, "Email build by nemal vyhodid vynimku, ak su vsetky polozky emailu nastavene");
        }
    }

    @Test
    void buildWithoutParam_8() {
        EmailBuilderInterface builder1 = new EmailBuilder();
        assertThrows(EmailNotBuildableException.class, () -> {builder1.build();}, "build() by mal vyhodit vynimku ked nie su zadane ziadne polozky emailu");

        EmailBuilderInterface builder2 = new EmailBuilder();
        builder2.setSender("sender@stuba.sk");
        builder2.setReceiver("receiver@stuba.sk");
        builder2.setSubject("subject of the message");
        builder2.setContent("content of this message");
        builder2.reset();
        assertThrows(EmailNotBuildableException.class, () -> {builder2.build();}, "build() by mal po reset() vyhodit vynimku");
    }

    @Test
    void buildWithSomeParam_8() {
        EmailBuilderInterface builder1 = new EmailBuilder();
        builder1.setSender("writer@gmail.com");
        builder1.setReceiver("reader@gmail.com");
        assertThrows(EmailNotBuildableException.class, () -> { builder1.build(); }, "build() musi vyhodit vynimku, ak obsah/sprava nie je nenastavena");

        EmailBuilderInterface builder2 = new EmailBuilder();
        builder2.setSender("writer@gmail.com");
        builder2.setContent("obsah");
        assertThrows(EmailNotBuildableException.class, () -> { builder2.build(); }, "build() musi vyhodit vynimku, ak prijimatel nie je nenastaveny");

        EmailBuilderInterface builder3 = new EmailBuilder();
        builder3.setReceiver("reader@gmail.com");
        builder3.setContent("obsah");
        assertThrows(EmailNotBuildableException.class, () -> { builder3.build(); }, "build() musi vyhodit vynimku, ak odosielatel nie je nenastaveny");
    }

    @Test
    void buildHasToReset_8() throws EmailNotBuildableException {
        EmailBuilderInterface builder = new EmailBuilder();
        builder.setSender("writer@gmail.com");
        builder.setReceiver("reader@gmail.com");
        builder.setContent("obsah");
        Email email1 = builder.build();
        assertThrows(EmailNotBuildableException.class, () -> { builder.build(); }, "build() musi resetnut vsetky udaje v buildery");
    }

    // ---------- director ------------------------------------------------------------------------

    @Test
    void directorMethodCompilation_1() {
        EmailBuilderInterface builder = new EmailBuilder();
        ConferenceDirector director = new ConferenceDirector(builder, null, null);
        director.invite(null, null, null, null, null, null);
        director.confirm(null, null, null);
    }

    @Test
    void directorAttributes_1() {
        Field[] fields = ConferenceDirector.class.getDeclaredFields();
        for (Field f: fields) {
            assertTrue(Modifier.isPrivate(f.getModifiers()), "Director musi obsahovat len private atributy");
        }
    }

    @Test
    void directorAttributeBuilder_1() {
        Field[] fields = ConferenceDirector.class.getDeclaredFields();
        for (Field f: fields) {
            assertNotEquals(EmailBuilder.class, f.getType(), "V Directore musi byt referencia na builder typu EmailBuilderInterface, aby sme v direcote mohli pouzit aj iny typ buildera, ak by sme ho vytvorili ");
        }
    }

    @Test
    void directorAnotherBuilderType_4() {
        AnotherEmailBuilder builder = new AnotherEmailBuilder();
        ConferenceDirector director = new ConferenceDirector(builder, null, null);
        director.invite(null, null, null, null, null, null);
        director.confirm(null, null, null);
    }

    @Test
    void directorInvitation_8() throws EmailNotBuildableException {
        EmailBuilderInterface builder = new EmailBuilder();
        ConferenceDirector directorA = new ConferenceDirector(builder, "jan@stuba.sk", "Jan Prskac");
        directorA.invite("jozef@uniba.sk", "Jozef", "Rocket Engines", "Bratislava", "April 1, 2025", "10:00 AM");
        Email invitation1 = builder.build();

        assertEquals("jan@stuba.sk", invitation1.getSender());
        assertEquals("jozef@uniba.sk", invitation1.getReceiver());
        assertEquals("Rocket Engines (invitation)", invitation1.getSubject());
        assertEquals("""
            Dear Jozef,
            We are pleased to invite you to our upcoming conference, Rocket Engines.
            The conference is scheduled to take place on April 1, 2025, at 10:00 AM, in Bratislava.
            Sincerely,
            Jan Prskac
            """, invitation1.getContent());
        assertNull(invitation1.getAttachment());

        directorA.invite("ondrej@gmail.com", "Ondrej", "Java", "Trnava", "April 2, 2025", "08:00 AM");
        Email invitation2 = builder.build();

        assertEquals("jan@stuba.sk", invitation2.getSender());
        assertEquals("ondrej@gmail.com", invitation2.getReceiver());
        assertEquals("Java (invitation)", invitation2.getSubject());
        assertEquals("""
            Dear Ondrej,
            We are pleased to invite you to our upcoming conference, Java.
            The conference is scheduled to take place on April 2, 2025, at 08:00 AM, in Trnava.
            Sincerely,
            Jan Prskac
            """, invitation2.getContent());
        assertNull(invitation2.getAttachment());

        ConferenceDirector directorB = new ConferenceDirector(builder, "andrej@stuba.sk", "Andrej Sietovy");
        directorB.invite("lubos@gmai.com", "Lubos", "Computer Network", "Nitra", "April 3, 2025", "13:00 AM");
        Email invitation3 = builder.build();

        assertEquals("andrej@stuba.sk", invitation3.getSender());
        assertEquals("lubos@gmai.com", invitation3.getReceiver());
        assertEquals("Computer Network (invitation)", invitation3.getSubject());
        assertEquals("""
            Dear Lubos,
            We are pleased to invite you to our upcoming conference, Computer Network.
            The conference is scheduled to take place on April 3, 2025, at 13:00 AM, in Nitra.
            Sincerely,
            Andrej Sietovy
            """, invitation3.getContent());
        assertNull(invitation3.getAttachment());

    }

    @Test
    void directorConfirmation_8() throws EmailNotBuildableException {
        EmailBuilderInterface builder = new EmailBuilder();
        ConferenceDirector directorA = new ConferenceDirector(builder, "jan@stuba.sk", "Jan Prskac");
        directorA.confirm("jozef@uniba.sk", "Jozef", "Rocket Engines");
        Email confirmation1 = builder.build();

        assertEquals("jan@stuba.sk", confirmation1.getSender());
        assertEquals("jozef@uniba.sk", confirmation1.getReceiver());
        assertEquals("Rocket Engines (registration confirmation)", confirmation1.getSubject());
        assertEquals("""
            Dear Jozef,
            We are delighted to confirm your registration for the Rocket Engines conference.
            Singecerly,
            Jan Prskac
            """, confirmation1.getContent());
        assertNull(confirmation1.getAttachment());

        directorA.confirm("emil@uniba.sk", "Emil", "Java");
        Email confirmation2 = builder.build();

        assertEquals("jan@stuba.sk", confirmation2.getSender());
        assertEquals("emil@uniba.sk", confirmation2.getReceiver());
        assertEquals("Java (registration confirmation)", confirmation2.getSubject());
        assertEquals("""
            Dear Emil,
            We are delighted to confirm your registration for the Java conference.
            Singecerly,
            Jan Prskac
            """, confirmation2.getContent());
        assertNull(confirmation2.getAttachment());
    }
}
