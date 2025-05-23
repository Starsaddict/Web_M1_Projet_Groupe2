package miage.groupe2.reseausocial.config;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.service.DemandeAmiService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private DemandeAmiService demandeAmiService;

    @ModelAttribute
    public void ajouterDemandesRecues(Model model, HttpSession session) {


        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        if (user == null) {
            return;
        }

        List<DemandeAmi> demandeAmis = demandeAmiService.getDemandeMessages(user);
        model.addAttribute("demandeAmis", demandeAmis);
    }

    @ModelAttribute
    public void ajouterAmis(Model model, HttpSession session) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        if (user == null) {
            return;
        }

        List<Utilisateur> Amis = user.getAmis();
        model.addAttribute("Amis", Amis);
    }

    @ModelAttribute
    public void ajouterPost(Model model, HttpSession session) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        if (user == null) {
            return;
        }

        model.addAttribute("newpost", new Post());
    }

    @ModelAttribute
    public void ajouterEvent(Model model, HttpSession session) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        if (user == null) {
            return;
        }
        model.addAttribute("newevent", new Evenement());
    }

    @ModelAttribute
    public void ajouterGroupe(Model model, HttpSession session) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        if (user == null) {
            return;
        }
        model.addAttribute("newgroupe", new Groupe());
    }

//    @ModelAttribute
//    public void ajouterConversation(Model model, HttpSession session) {
//        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
//        if (user == null) {
//            return;
//        }
//        model.addAttribute("newconversation", new Conversation());
//    }
}
