package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Conversation;
import miage.groupe2.reseausocial.Model.Message;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.ConversationRepository;
import miage.groupe2.reseausocial.Repository.MessageRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageWebSocketControllerTest {

    @InjectMocks
    private MessageWebSocketController controller;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEnvoyerViaSocket() {
        int convId = 1;

        Conversation conv = new Conversation();
        conv.setIdConv(convId);

        Utilisateur expediteur = new Utilisateur();
        expediteur.setIdUti(42);

        Message message = new Message();
        message.setExpediteur(expediteur);

        when(conversationRepository.findById(convId)).thenReturn(Optional.of(conv));
        when(utilisateurRepository.findById(expediteur.getIdUti())).thenReturn(Optional.of(expediteur));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Message result = controller.envoyerViaSocket(convId, message);

        assertNotNull(result);
        assertEquals(conv, result.getConversation());
        assertEquals(expediteur, result.getExpediteur());
        assertTrue(result.getDateM() > 0);

        verify(messageRepository, times(1)).save(result);
    }
}
