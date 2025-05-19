package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Conversation;
import miage.groupe2.reseausocial.Model.Message;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.ConversationRepository;
import miage.groupe2.reseausocial.Repository.MessageRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ConversationController {

    private final ConversationRepository conversationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MessageRepository messageRepository;

    // ✅ Constructeur avec injection de dépendances
    @Autowired
    public ConversationController(ConversationRepository conversationRepository, UtilisateurRepository utilisateurRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/message/vers-conversation")
    public String versConversation(@RequestParam("idAmi") Integer idAmi,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Recherche d'une conversation existante entre les deux utilisateurs
        List<Conversation> conversations = conversationRepository
                .findConversationBetweenTwoUsers(user.getIdUti(), idAmi);

        Conversation conv;
        if (!conversations.isEmpty()) {
            conv = conversations.get(0); // Conversation existante
        } else {
            Utilisateur ami = utilisateurRepository.findByidUti(idAmi);

            conv = new Conversation();
            conv.setNomConv("Conversation entre " + user.getNomU() + " et " + ami.getNomU());
            conv.setCreateur(user);

            List<Utilisateur> participants = new ArrayList<>();
            participants.add(user);
            participants.add(ami);

            conv.setParticipants(participants);

            // ✅ Save and persist the conversation and relationship table
            conversationRepository.save(conv);
        }

        return "redirect:/message/conversation/" + conv.getIdConv();
    }


    @GetMapping("/message/conversation/{id}")
    public String afficherConversation(@PathVariable("id") Integer idConv, HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        Conversation conv = conversationRepository.findById(idConv).orElse(null);
        if (conv == null) return "redirect:/user/mes-amis";

        List<Message> messages = messageRepository.findByConversationOrderByDateMAsc(conv);

        // Trouver l'ami avec qui on discute (l'autre participant)
        String nomAmi = conv.getParticipants().stream()
                .filter(u -> !u.getIdUti().equals(user.getIdUti()))
                .map(u -> u.getNomU() + " " + u.getPrenomU())
                .findFirst().orElse("Inconnu");

        model.addAttribute("conversation", conv);
        model.addAttribute("messages", messages);
        model.addAttribute("nomAmi", nomAmi);
        return "conversation";
    }


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
        // Stocke la date en timestamp Unix millis
        msg.setDateM(Instant.now().toEpochMilli());

        messageRepository.save(msg);

        return "redirect:/message/conversation/" + idConv;
    }





}
