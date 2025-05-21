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

/**
 * Contrôleur WebSocket pour la gestion des messages en temps réel dans les conversations.
 * Gère la réception et la diffusion des messages via STOMP/WebSocket.
 */
@Controller
public class MessageWebSocketController {

    /**
     * Repository pour l'accès aux messages persistés.
     */
    @Autowired
    private MessageRepository messageRepository;

    /**
     * Repository pour l'accès aux conversations.
     */
    @Autowired
    private ConversationRepository conversationRepository;

    /**
     * Repository pour l'accès aux utilisateurs.
     */
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Méthode appelée lorsqu'un message est envoyé via WebSocket.
     * Elle enregistre le message en base, l’associe à la conversation et à l’expéditeur,
     * puis le renvoie pour être diffusé à tous les abonnés du topic correspondant.
     *
     * @param convId  L'identifiant de la conversation à laquelle le message appartient.
     * @param message Le message envoyé par l'utilisateur.
     * @return Le message complet (avec date, conversation et expéditeur), qui sera envoyé aux abonnés.
     */
    @MessageMapping("/chat.sendMessage/{convId}")
    @SendTo("/topic/conversation/{convId}")
    public Message envoyerViaSocket(@DestinationVariable int convId, Message message) {
        // Récupère la conversation concernée
        Conversation conv = conversationRepository.findById(convId).orElseThrow();
        message.setConversation(conv);

        // Récupère l’expéditeur complet à partir de son ID
        Utilisateur expediteur = utilisateurRepository.findById(message.getExpediteur().getIdUti()).orElseThrow();
        message.setExpediteur(expediteur);

        // Définit la date actuelle pour le message
        message.setDateM(System.currentTimeMillis());

        // Sauvegarde du message en base
        messageRepository.save(message);

        // Diffusion du message via WebSocket
        return message;
    }
}
