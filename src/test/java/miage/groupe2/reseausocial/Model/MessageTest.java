package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class MessageTest {

    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message();
    }

    @Test
    void testSetAndGetIdMsg() {
        message.setIdMsg(10);
        assertEquals(10, message.getIdMsg());
    }

    @Test
    void testSetAndGetTextM() {
        message.setTextM("Bonjour tout le monde");
        assertEquals("Bonjour tout le monde", message.getTextM());
    }

    @Test
    void testSetAndGetDateM() {
        long timestamp = System.currentTimeMillis();
        message.setDateM(timestamp);
        assertEquals(timestamp, message.getDateM());
    }

    @Test
    void testSetAndGetExpediteur() {
        Utilisateur expediteur = new Utilisateur();
        message.setExpediteur(expediteur);
        assertEquals(expediteur, message.getExpediteur());
    }

    @Test
    void testSetAndGetConversation() {
        Conversation conv = new Conversation();
        message.setConversation(conv);
        assertEquals(conv, message.getConversation());
    }

    @Test
    void testConstructorWithParameters() {
        Utilisateur expediteur = new Utilisateur();
        Conversation conv = new Conversation();
        long date = 123456789L;
        String text = "Hello";

        Message msg = new Message(1, expediteur, conv, date, text);

        assertEquals(1, msg.getIdMsg());
        assertEquals(expediteur, msg.getExpediteur());
        assertEquals(conv, msg.getConversation());
        assertEquals(date, msg.getDateM());
        assertEquals(text, msg.getTextM());
    }
}
