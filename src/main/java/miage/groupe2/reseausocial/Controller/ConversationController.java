package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Conversation;
import miage.groupe2.reseausocial.Model.Message;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.ConversationRepository;
import miage.groupe2.reseausocial.Repository.MessageRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère les conversations privées et de groupe.
 */
@Controller
public class ConversationController {

    public static final String MESSAGES = "/messages";
    private final ConversationRepository conversationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    public ConversationController(ConversationRepository conversationRepository, UtilisateurRepository utilisateurRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.messageRepository = messageRepository;
    }

    @RequestMapping("/message/vers-conversation")
    public String versConversation(@RequestParam("idAmi") Integer idAmi,
                                   HttpSession session
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        List<Conversation> monConv = user.getConversationsParticipees();

        monConv = monConv.stream()
                .filter(c -> !c.isEstconversationDeGroupe())
                .filter(c -> c.getParticipants().stream()
                        .map(Utilisateur::getIdUti)
                        .anyMatch(id -> id == idAmi)
                )
                .toList();

        Conversation conv = null;

        boolean ifHaveConversations = !monConv.isEmpty();
        if (ifHaveConversations) {
            conv = monConv.get(0);
        }

        // Si aucune conversation privée n'existe, en créer une
        if (!ifHaveConversations) {

            Utilisateur ami = utilisateurRepository.findByidUti(idAmi);
            conv = new Conversation();
            conv.setCreateur(user);
            long timestamp = Instant.now().toEpochMilli();
            conv.setDateConv(timestamp);

            List<Utilisateur> participants = new ArrayList<>();
            participants.add(user);
            participants.add(ami);
            conv.setParticipants(participants);
            conversationRepository.save(conv);
        }

        return "redirect:/messages?idConv=" + conv.getIdConv();
    }


    /**
     * Envoie un message dans une conversation.
     */
    @PostMapping("/message/envoyer/{idConv}")
    public String envoyerMessage(@PathVariable("idConv") Integer idConv,
                                 @RequestParam("texte") String texte,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 @RequestHeader(value = "Referer", required = false) String referer
    ) {

        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);


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

        messagingTemplate.convertAndSend("/topic/conversation/" + idConv, msg);

        return RedirectUtil.getSafeRedirectUrl(referer,"/message/conversation/" + idConv);
    }

    @PostMapping("/conversation/groupe/creer")
    public String creerConversationGroupe(
            @RequestParam("participantIds") List<Integer> participantIds,
            HttpSession session,@RequestParam("nomdiscussion") String nomdiscussion,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {

        Utilisateur utilisateurConnecte = utilisateurService.getUtilisateurFromSession(session);


        participantIds.add(utilisateurConnecte.getIdUti());

        List<Utilisateur> participants = utilisateurRepository.findAllById(participantIds);
        Conversation conversation = new Conversation();
        long timestamp = Instant.now().toEpochMilli();
        conversation.setDateConv(timestamp);
        conversation.setNomConv(nomdiscussion);
        conversation.setParticipants(participants);
        conversation.setCreateur(utilisateurConnecte);
        conversation.setEstconversationDeGroupe(true);
        conversationRepository.save(conversation);

        return "redirect:/messages?idConv=" + conversation.getIdConv();
    }



    @PostMapping("/conversation/supprimer/{idConv}")
    public String supprimerConversation(
            @PathVariable("idConv") Integer idConv,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        Conversation conv = conversationRepository.findByIdConv(idConv);


        assert conv != null;
        if (conv.getCreateur().getIdUti().equals(user.getIdUti())) {
            conversationRepository.delete(conv);
            return "redirect:/messages";
        }else{
            return "redirect:/messages?idConv=" + idConv;
        }

    }

    /**
     * Permet à un utilisateur de quitter une conversation.
     */
    @PostMapping("/conversation/quitter/{idConv}")
    public String quitterConversation(@PathVariable("idConv") Integer idConv,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes,
                                      @RequestHeader(value = "Referer", required = false) String referer

    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);


        Conversation conv = conversationRepository.findById(idConv).orElse(null);
        if (conv == null) {
            return MESSAGES;
        }

        if (conv.getCreateur().getIdUti().equals(user.getIdUti())) {
            return RedirectUtil.getSafeRedirectUrl(referer,MESSAGES);
        }

        // Supprimer l'utilisateur des participants
        conv.getParticipants().remove(user);
        conversationRepository.save(conv);

        return MESSAGES;
    }


    @GetMapping(MESSAGES)
    public String afficherMessages(HttpSession session,
                                   Model model,
                                   @RequestParam(value = "idConv", required = false) Integer idConv
                                   ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        List<Conversation> convs = user.getConversationsParticipees().stream()
                .sorted((c1, c2) -> {
                    boolean empty1 = c1.getMessages().isEmpty();
                    boolean empty2 = c2.getMessages().isEmpty();
                    if (empty1 && empty2) {
                        return Long.compare(c2.getDateConv(), c1.getDateConv());
                    } else if (empty1) {
                        return 1;
                    } else if (empty2) {
                        return -1;
                    } else {
                        long t1 = c1.getMessages().get(c1.getMessages().size() - 1).getDateM();
                        long t2 = c2.getMessages().get(c2.getMessages().size() - 1).getDateM();
                        return Long.compare(t2, t1);
                    }
                })
                .toList();
        convs.forEach(c -> c.getParticipants().remove(user));
        model.addAttribute("conversations", convs);
        if (idConv != null) {
            Conversation selected = conversationRepository.findByIdConv(idConv);
            model.addAttribute("selectedConv", selected);
            List<Utilisateur> amisDisponibles = user.getAmis().stream()
                    .filter(a -> !selected.getParticipants().contains(a))
                    .toList();
            model.addAttribute("amisDisponibles", amisDisponibles);
        }



        return "messages";
    }

    @RequestMapping("/conversation/supprimerParticipant")
    public String supprimerParticipant(HttpSession session,
                                       Model model,
                                       @RequestParam(value = "idConv") Integer idConv,
                                       @RequestParam(value = "idUti") Integer idUti,
                                       @RequestHeader(value = "Referer", required = false) String referer
                                       ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Conversation conv = conversationRepository.findByIdConv(idConv);
        Utilisateur kicked = utilisateurRepository.findByIdUti(idUti);

        if(conv.getCreateur().getIdUti().equals(user.getIdUti())) {
            if(!user.getIdUti().equals(idUti)) {
                conv.getParticipants().remove(kicked);
                kicked.getConversationsParticipees().remove(conv);
                conversationRepository.save(conv);
            }
        }
        return RedirectUtil.getSafeRedirectUrl(referer,"/messages?idConv="+idConv);
    }

    @PostMapping("/conversation/ajouterParticipant")
    public String ajouterParticipant(
            HttpSession session,
            Model model,
            @RequestParam(value = "idConv") Integer idConv,
            @RequestParam(value = "idUti") Integer idUti,
            @RequestHeader(value = "Referer", required = false) String referer
    ){
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Conversation conv = conversationRepository.findByIdConv(idConv);
        Utilisateur ajouter = utilisateurRepository.findByIdUti(idUti);

        if(conv.getCreateur().getIdUti().equals(user.getIdUti())) {
            if(!user.getIdUti().equals(idUti)) {
                conv.getParticipants().add(ajouter);
                ajouter.getConversationsParticipees().add(conv);
                conversationRepository.save(conv);
            }
        }
        return RedirectUtil.getSafeRedirectUrl(referer,"/messages?idConv="+idConv);
    }
}
