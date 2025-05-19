package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    @Test
    void testConstructeurVide() {
        Conversation conversation = new Conversation();
        assertNotNull(conversation);
        assertNull(conversation.getIdConv());
        assertNull(conversation.getNomConv());
        assertNull(conversation.getCreateur());
        assertNull(conversation.getMessages());
        assertNull(conversation.getParticipants());
    }

    @Test
    void testGettersAndSetters() {
        Utilisateur createur = new Utilisateur();
        createur.setIdUti(1);
        createur.setNomU("Doe");

        Utilisateur participant1 = new Utilisateur();
        participant1.setIdUti(2);
        Utilisateur participant2 = new Utilisateur();
        participant2.setIdUti(3);

        List<Utilisateur> participants = new ArrayList<>();
        participants.add(participant1);
        participants.add(participant2);

        Message message1 = new Message();
        message1.setIdMsg(1);
        Message message2 = new Message();
        message2.setIdMsg(2);

        List<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);

        Conversation conversation = new Conversation();
        conversation.setIdConv(10);
        conversation.setNomConv("Groupe MIAGE");
        conversation.setCreateur(createur);
        conversation.setParticipants(participants);
        conversation.setMessages(messages);

        assertEquals(10, conversation.getIdConv());
        assertEquals("Groupe MIAGE", conversation.getNomConv());
        assertEquals(createur, conversation.getCreateur());

        assertEquals(2, conversation.getParticipants().size());
        assertTrue(conversation.getParticipants().contains(participant1));
        assertTrue(conversation.getParticipants().contains(participant2));

        assertEquals(2, conversation.getMessages().size());
        assertEquals(1, conversation.getMessages().get(0).getIdMsg());
        assertEquals(2, conversation.getMessages().get(1).getIdMsg());
    }

    @Test
    void testConversationVideSansParticipantsNiMessages() {
        Conversation conversation = new Conversation();
        conversation.setNomConv("Test Vide");

        assertEquals("Test Vide", conversation.getNomConv());
        assertNull(conversation.getParticipants());
        assertNull(conversation.getMessages());
    }

    @Test
    void testParticipantsVidesExplicitement() {
        Conversation conversation = new Conversation();
        conversation.setParticipants(new ArrayList<>());
        conversation.setMessages(new ArrayList<>());

        assertNotNull(conversation.getParticipants());
        assertNotNull(conversation.getMessages());
        assertTrue(conversation.getParticipants().isEmpty());
        assertTrue(conversation.getMessages().isEmpty());
    }
}
