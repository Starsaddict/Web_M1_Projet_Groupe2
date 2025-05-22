package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Conversation;
import miage.groupe2.reseausocial.Model.Message;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.ConversationRepository;
import miage.groupe2.reseausocial.Repository.MessageRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ConversationControllerTest {

    private ConversationRepository conversationRepository;
    private UtilisateurRepository utilisateurRepository;
    private MessageRepository messageRepository;
    private UtilisateurService utilisateurService;
    private SimpMessagingTemplate messagingTemplate;

    private ConversationController conversationController;

    @BeforeEach
    public void setUp() throws Exception {
        conversationRepository = mock(ConversationRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        messageRepository = mock(MessageRepository.class);
        utilisateurService = mock(UtilisateurService.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);

        conversationController = new ConversationController(conversationRepository, utilisateurRepository, messageRepository);

        Field utilisateurServiceField = ConversationController.class.getDeclaredField("utilisateurService");
        utilisateurServiceField.setAccessible(true);
        utilisateurServiceField.set(conversationController, utilisateurService);

        Field messagingTemplateField = ConversationController.class.getDeclaredField("messagingTemplate");
        messagingTemplateField.setAccessible(true);
        messagingTemplateField.set(conversationController, messagingTemplate);
    }

    @Test
    public void testCreerConversationGroupe_simplifie() {
        HttpSession session = mock(HttpSession.class);
        List<Integer> participantIds = Arrays.asList(1, 2);
        String nomdiscussion = "Groupe Test";

        Utilisateur utilisateurConnecte = new Utilisateur();
        utilisateurConnecte.setIdUti(1);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateurConnecte);

        Utilisateur u1 = new Utilisateur();
        u1.setIdUti(1);
        Utilisateur u2 = new Utilisateur();
        u2.setIdUti(2);
        List<Utilisateur> participants = Arrays.asList(u1, u2);
        when(utilisateurRepository.findAllById(participantIds)).thenReturn(participants);

        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        doAnswer(invocation -> {
            Conversation conv = invocation.getArgument(0);
            conv.setIdConv(100);
            return conv;
        }).when(conversationRepository).save(captor.capture());

        List<Utilisateur> foundParticipants = utilisateurRepository.findAllById(participantIds);
        Conversation conversation = new Conversation();
        conversation.setDateConv(Instant.now().toEpochMilli());
        conversation.setNomConv(nomdiscussion);
        conversation.setParticipants(foundParticipants);
        conversation.setCreateur(utilisateurConnecte);
        conversation.setEstconversationDeGroupe(true);
        conversationRepository.save(conversation);
        String redirect = "redirect:/messages?idConv=" + conversation.getIdConv();

        Conversation savedConv = captor.getValue();
        assertEquals(nomdiscussion, savedConv.getNomConv());
        assertTrue(savedConv.getParticipants().containsAll(participants));
        assertEquals(utilisateurConnecte, savedConv.getCreateur());
        assertTrue(savedConv.isEstconversationDeGroupe());
        assertEquals("redirect:/messages?idConv=100", redirect);
    }


    @Test
    public void testSupprimerConversation_Createur() {
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String referer = "/previous";

        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(session.getAttribute("user")).thenReturn(user);

        Conversation conv = new Conversation();
        conv.setIdConv(5);
        conv.setCreateur(user);
        when(conversationRepository.findByIdConv(5)).thenReturn(conv);

        String redirect = conversationController.supprimerConversation(5, session, redirectAttributes, referer);

        verify(conversationRepository).delete(conv);
        assertEquals("redirect:/messages", redirect);
    }

    @Test
    public void testSupprimerConversation_NonCreateur() {
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String referer = "/previous";

        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(session.getAttribute("user")).thenReturn(user);

        Utilisateur autre = new Utilisateur();
        autre.setIdUti(2);

        Conversation conv = new Conversation();
        conv.setIdConv(5);
        conv.setCreateur(autre);
        when(conversationRepository.findByIdConv(5)).thenReturn(conv);

        String redirect = conversationController.supprimerConversation(5, session, redirectAttributes, referer);

        verify(conversationRepository, never()).delete(any());
        assertEquals("redirect:/messages?idConv=5", redirect);
    }

    @Test
    public void testQuitterConversation_NonCreateur() {
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String referer = "/previous";

        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Conversation conv = new Conversation();
        conv.setIdConv(10);
        Utilisateur createur = new Utilisateur();
        createur.setIdUti(2);
        conv.setCreateur(createur);

        List<Utilisateur> participants = new ArrayList<>();
        participants.add(user);
        participants.add(createur);
        conv.setParticipants(participants);

        when(conversationRepository.findById(10)).thenReturn(Optional.of(conv));

        String redirect = conversationController.quitterConversation(10, session, redirectAttributes, referer);

        assertFalse(conv.getParticipants().contains(user));
        verify(conversationRepository).save(conv);
        assertEquals("/messages", redirect);
    }

    @Test
    public void testQuitterConversation_Createur() {
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String referer = "/previous";

        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Conversation conv = new Conversation();
        conv.setIdConv(10);
        conv.setCreateur(user);

        when(conversationRepository.findById(10)).thenReturn(Optional.of(conv));

        String redirect = conversationController.quitterConversation(10, session, redirectAttributes, referer);

        verify(conversationRepository, never()).save(any());
        assertEquals("redirect:/messages", redirect);
    }

    @Test
    public void testAfficherMessages_simplifie() {
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);

        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        Conversation conv1 = new Conversation();
        conv1.setIdConv(1);
        conv1.setDateConv(1000L);
        conv1.setMessages(new ArrayList<>());
        conv1.setParticipants(new ArrayList<>(List.of(user)));

        Conversation conv2 = new Conversation();
        conv2.setIdConv(2);
        conv2.setDateConv(2000L);
        Message msg = new Message();
        msg.setDateM(3000L);
        conv2.setMessages(List.of(msg));
        conv2.setParticipants(new ArrayList<>(List.of(user)));

        user.setConversationsParticipees(List.of(conv1, conv2));
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        when(conversationRepository.findByIdConv(null)).thenReturn(null);

        String view = conversationController.afficherMessages(session, model, null);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("conversations"), captor.capture());

        List<Conversation> convsAdded = captor.getValue();
        assertEquals(2, convsAdded.size());

        verify(model).addAttribute(eq("selectedConv"), eq(conv2));

        assertEquals("messages", view);
    }

    @Test
    public void testSupprimerParticipant() {
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        String referer = "/prev";

        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Utilisateur kicked = new Utilisateur();
        kicked.setIdUti(2);
        Conversation conv = new Conversation();
        conv.setIdConv(10);
        conv.setCreateur(user);
        conv.setParticipants(new ArrayList<>(List.of(user, kicked)));
        kicked.setConversationsParticipees(new ArrayList<>(List.of(conv)));

        when(conversationRepository.findByIdConv(10)).thenReturn(conv);
        when(utilisateurRepository.findByIdUti(2)).thenReturn(kicked);

        String redirect = conversationController.supprimerParticipant(session, model, 10, 2, referer);

        assertFalse(conv.getParticipants().contains(kicked));
        assertFalse(kicked.getConversationsParticipees().contains(conv));
        verify(conversationRepository).save(conv);
        assertEquals("redirect:/messages?idConv=10", redirect);
    }

    @Test
    public void testAjouterParticipant() {
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        String referer = "/prev";

        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Utilisateur ajouter = new Utilisateur();
        ajouter.setIdUti(2);
        Conversation conv = new Conversation();
        conv.setIdConv(10);
        conv.setCreateur(user);
        conv.setParticipants(new ArrayList<>(List.of(user)));
        ajouter.setConversationsParticipees(new ArrayList<>());

        when(conversationRepository.findByIdConv(10)).thenReturn(conv);
        when(utilisateurRepository.findByIdUti(2)).thenReturn(ajouter);

        String redirect = conversationController.ajouterParticipant(session, model, 10, 2, referer);

        assertTrue(conv.getParticipants().contains(ajouter));
        assertTrue(ajouter.getConversationsParticipees().contains(conv));
        verify(conversationRepository).save(conv);
        assertEquals("redirect:/messages?idConv=10", redirect);
    }
}
