package miage.groupe2.reseausocial.config;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
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
}
