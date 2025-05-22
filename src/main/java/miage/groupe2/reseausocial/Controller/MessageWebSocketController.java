package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Conversation;
import miage.groupe2.reseausocial.Model.Message;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.ConversationRepository;
import miage.groupe2.reseausocial.Repository.MessageRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageWebSocketController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Traite l'envoi d'un message via WebSocket.
     */
    @MessageMapping("/chat.sendMessage/{convId}")
    @SendTo("/topic/conversation/{convId}")
    public Message envoyerViaSocket(@DestinationVariable int convId, Message message) {
        Conversation conv = conversationRepository.findById(convId).orElseThrow();
        message.setConversation(conv);

        Utilisateur expediteur = utilisateurRepository.findById(message.getExpediteur().getIdUti()).orElseThrow();
        message.setExpediteur(expediteur);
        message.setDateM(System.currentTimeMillis());

        messageRepository.save(message);
        return message;
    }
}
