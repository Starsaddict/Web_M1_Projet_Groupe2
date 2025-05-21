package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Conversation;
import miage.groupe2.reseausocial.Model.Message;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.ConversationRepository;
import miage.groupe2.reseausocial.Repository.MessageRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ConversationController {
    /** Repository pour la gestion des conversations */
    private final ConversationRepository conversationRepository;

    /** Repository pour la gestion des utilisateurs */
    private final UtilisateurRepository utilisateurRepository;

    /** Repository pour la gestion des messages */
    private final MessageRepository messageRepository;

    /** Template pour l'envoi de messages via WebSocket */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Constructeur avec injection des dépendances nécessaires.
     *
     * @param conversationRepository Repository des conversations
     * @param utilisateurRepository Repository des utilisateurs
     * @param messageRepository     Repository des messages
     */
    @Autowired
    public ConversationController(ConversationRepository conversationRepository, UtilisateurRepository utilisateurRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Redirige vers une conversation privée avec un ami existant,
     * ou en crée une si elle n'existe pas encore.
     *
     * @param idAmi ID de l'ami avec qui discuter
     * @param session Session HTTP de l'utilisateur
     * @param redirectAttributes Attributs de redirection
     * @return Redirection vers la conversation
     */

    @PostMapping("/message/vers-conversation")
    public String versConversation(@RequestParam("idAmi") Integer idAmi,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Recherche d'une conversation privée existante entre les deux utilisateurs
        List<Conversation> conversations = conversationRepository
                .findConversationBetweenTwoUsers(user.getIdUti(), idAmi);

        Conversation conv = null;

        for (Conversation c : conversations) {
            if (!c.isEstconversationDeGroupe()) {
                conv = c;
                break;
            }
        }

        // Si aucune conversation privée n'existe, en créer une
        if (conv == null) {
            Utilisateur ami = utilisateurRepository.findByidUti(idAmi);

            conv = new Conversation();
            conv.setNomConv("Conversation entre " + user.getNomU() + " et " + ami.getNomU());
            conv.setCreateur(user);


            List<Utilisateur> participants = new ArrayList<>();
            participants.add(user);
            participants.add(ami);

            conv.setParticipants(participants);

            conversationRepository.save(conv);
        }

        return "redirect:/message/conversation/" + conv.getIdConv();
    }


    /**
     * Affiche la page d'une conversation spécifique avec les messages.
     *
     * @param idConv ID de la conversation
     * @param session Session HTTP
     * @param model Modèle pour la vue
     * @return Vue de la conversation
     */
    @GetMapping("/message/conversation/{id}")
    public String afficherConversation(@PathVariable("id") Integer idConv, HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        Conversation conv = conversationRepository.findById(idConv).orElse(null);
        if (conv == null) return "redirect:/user/mes-amis";

        List<Message> messages = messageRepository.findByConversationOrderByDateMAsc(conv);

        // Récupérer tous les noms des participants sauf l'utilisateur connecté
        List<String> nomsParticipants = conv.getParticipants().stream()
                .filter(u -> !u.getIdUti().equals(user.getIdUti()))
                .map(u -> u.getPrenomU() + " " + u.getNomU())
                .toList();

        model.addAttribute("conversation", conv);
        model.addAttribute("messages", messages);
        model.addAttribute("nomsParticipants", nomsParticipants);
        return "conversation";
    }

    /**
     * Envoie un message dans une conversation existante
     * et le transmet via WebSocket aux autres participants.
     *
     * @param idConv ID de la conversation
     * @param texte Texte du message
     * @param session Session HTTP
     * @param redirectAttributes Attributs de redirection
     * @return Redirection vers la conversation
     */
    @PostMapping("/message/envoyer/{idConv}")
    public String envoyerMessage(@PathVariable("idConv") Integer idConv,
                                 @RequestParam("texte") String texte,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        Conversation conv = conversationRepository.findById(idConv).orElse(null);
        if (conv == null) {
            redirectAttributes.addFlashAttribute("error", "Conversation introuvable");
            return "redirect:/user/mes-amis";
        }

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setExpediteur(user);
        msg.setTextM(texte);
        msg.setDateM(Instant.now().toEpochMilli());

        messageRepository.save(msg);

        // ✅ WebSocket 推送
        messagingTemplate.convertAndSend("/topic/conversation/" + idConv, msg);

        return "redirect:/message/conversation/" + idConv;
    }

    /**
     * Affiche la page permettant de créer une nouvelle conversation de groupe.
     *
     * @param session Session HTTP
     * @param model Modèle pour la vue
     * @return Vue pour sélectionner les amis
     */

    @GetMapping("/conversation/groupe/nouvelle")
    public String voirMesAmis1(HttpSession session, Model model) {
        Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
        if (userConnecte == null) return "redirect:/auth/login";

        Utilisateur utilisateurAvecAmis = utilisateurRepository.findById(userConnecte.getIdUti()).orElse(null);
        if (utilisateurAvecAmis == null) return "redirect:/auth/login";

        model.addAttribute("amis", utilisateurAvecAmis.getAmis());
        return "conversationgroupe"; // ❗affiche la page avec les amis à cocher
    }

    /**
     * Crée une nouvelle conversation de groupe avec les participants sélectionnés.
     *
     * @param participantIds Liste des IDs des participants
     * @param nomdiscussion Nom de la discussion
     * @param session Session HTTP
     * @param redirectAttributes Attributs de redirection
     * @return Redirection vers la nouvelle conversation
     */
    @PostMapping("/conversation/groupe/creer")
    public String creerConversationGroupe(
            @RequestParam("participantIds") List<Integer> participantIds,
            HttpSession session,@RequestParam("nomdiscussion") String nomdiscussion,
            RedirectAttributes redirectAttributes) {

        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("user");
        if (utilisateurConnecte == null) return "redirect:/auth/login";

        // On ajoute l'utilisateur lui-même dans la conversation
        participantIds.add(utilisateurConnecte.getIdUti());

        if (participantIds.size() < 2) {
            redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner au moins 3 participants.");
            return "redirect:/conversation/groupe/nouvelle";
        }

        List<Utilisateur> participants = utilisateurRepository.findAllById(participantIds);

        Conversation conversation = new Conversation();
        conversation.setNomConv(nomdiscussion);
        conversation.setParticipants(participants);
        conversation.setCreateur(utilisateurConnecte);
        conversation.setEstconversationDeGroupe(true);

        conversationRepository.save(conversation);

        return "redirect:/message/conversation/" + conversation.getIdConv();
    }

    /**
     * Affiche toutes les conversations de groupe auxquelles participe l'utilisateur connecté.
     *
     * @param session Session HTTP
     * @param model Modèle pour la vue
     * @return Vue des conversations de groupe
     */

    @GetMapping("/conversation/groupes")
    public String afficherConversationsDeGroupe(HttpSession session, Model model) {
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("user");
        if (utilisateurConnecte == null) {
            return "redirect:/auth/login";
        }

        // On récupère toutes les conversations où l'utilisateur participe
        List<Conversation> toutesConversations = conversationRepository.findByParticipants_IdUti(utilisateurConnecte.getIdUti());

        // On filtre pour ne garder que les conversations avec 3 participants ou plus
        List<Conversation> conversationsDeGroupe = toutesConversations.stream()
                .filter(conv -> conv.getParticipants().size() >= 2)
                .toList();

        model.addAttribute("groupes", conversationsDeGroupe);
        model.addAttribute("userConnecteId", utilisateurConnecte.getIdUti());

        return "afficherconversationgroupe";
    }

    /**
     * Supprime une conversation de groupe (réservé au créateur).
     *
     * @param idConv ID de la conversation
     * @param session Session HTTP
     * @param redirectAttributes Attributs de redirection
     * @return Redirection après suppression
     */
    @PostMapping("/conversation/supprimer/{idConv}")
    public String supprimerConversation(@PathVariable("idConv") Integer idConv, HttpSession session,  RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        Conversation conv = conversationRepository.findById(idConv).orElse(null);

        assert conv != null;
        if (!conv.getCreateur().getIdUti().equals(user.getIdUti())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas le créateur de cette conversation.");
            return "redirect:/conversation/groupes";
        }


        // Supprimer d'abord les messages liés
        List<Message> messages = messageRepository.findByConversationOrderByDateMAsc(conv);
        messageRepository.deleteAll(messages);

        // Ensuite supprimer la conversation
        conversationRepository.delete(conv);


        return "redirect:/conversation/groupes";
    }

    /**
     * Permet à un utilisateur de quitter une conversation de groupe
     * (le créateur ne peut pas quitter).
     *
     * @param idConv ID de la conversation
     * @param session Session HTTP
     * @param redirectAttributes Attributs de redirection
     * @return Redirection après avoir quitté
     */
    @PostMapping("/conversation/quitter/{idConv}")
    public String quitterConversation(@PathVariable("idConv") Integer idConv, HttpSession session, RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        Conversation conv = conversationRepository.findById(idConv).orElse(null);
        if (conv == null) {
            redirectAttributes.addFlashAttribute("error", "Conversation introuvable.");
            return "redirect:/conversation/groupes";
        }

        // Vérifier que ce n’est pas le créateur
        if (conv.getCreateur().getIdUti().equals(user.getIdUti())) {
            redirectAttributes.addFlashAttribute("error", "Le créateur ne peut pas quitter la conversation. Supprime-la si nécessaire.");
            return "redirect:/conversation/groupes";
        }

        // Supprimer l'utilisateur des participants
        conv.getParticipants().removeIf(p -> p.getIdUti().equals(user.getIdUti()));
        conversationRepository.save(conv);

        redirectAttributes.addFlashAttribute("success", "Vous avez quitté la conversation.");
        return "redirect:/conversation/groupes";
    }

}
