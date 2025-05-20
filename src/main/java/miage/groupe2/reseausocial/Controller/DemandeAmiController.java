package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/demande")
public class DemandeAmiController {

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;


    @GetMapping("/demandes-recues")
    public String afficherDemandesRecues(HttpSession session, Model model) {
        Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
        if (userConnecte == null) {
            return "redirect:/auth/login";
        }

        List<DemandeAmi> demandesRecues = demandeAmiRepository
                .findByRecepteurIdUtiAndStatut(userConnecte.getIdUti(), "en attente");

        model.addAttribute("demandesRecues", demandesRecues);
        return "listeDemandeAmi";
    }

    @PostMapping("/accepter")
    public String accepterDemande(@RequestParam("idDemande") Integer idDemande,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes,
                                  @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
        if (userConnecte == null) {
            return "redirect:/auth/login";
        }

        DemandeAmi demande = demandeAmiRepository.findById(idDemande).orElse(null);
        if (demande != null && demande.getRecepteur().getIdUti().equals(userConnecte.getIdUti())) {
            demande.setStatut("acceptée");
            demandeAmiRepository.save(demande);
            demandeAmiRepository.ajouterLienAmitie(demande.getDemandeur().getIdUti(), userConnecte.getIdUti());
            redirectAttributes.addFlashAttribute("succes", "Demande d'ami acceptée.");
        }

        return "redirect:" + (referer != null ? referer : "/home");
    }

    @PostMapping("/refuser")
    public String refuserDemande(@RequestParam("idDemande") Integer idDemande,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
        if (userConnecte == null) {
            return "redirect:/auth/login";
        }

        DemandeAmi demande = demandeAmiRepository.findById(idDemande).orElse(null);
        if (demande != null && demande.getRecepteur().getIdUti().equals(userConnecte.getIdUti())) {
            demande.setStatut("refusée");
            demandeAmiRepository.save(demande);
            redirectAttributes.addFlashAttribute("succes", "Demande d'ami refusée.");
        }

        return "redirect:" + (referer != null ? referer : "/home");
    }


    @PostMapping("/ajouterAmi")
    public String envoyerDemandeAmi(@RequestParam("idAmi") Integer idAmi,
                                    @RequestParam(value = "nom", required = false) String nomRecherche,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
        if (userConnecte == null) {
            return "redirect:/auth/login";
        }
        if (idAmi.equals(userConnecte.getIdUti())) {
            redirectAttributes.addFlashAttribute("error", "Vous ne pouvez pas vous ajouter vous-même.");
            return "redirect:/user/rechercher?nom=" + (nomRecherche != null ? nomRecherche : "");
        }

        Utilisateur recepteur = utilisateurRepository.findByidUti(idAmi);
        if (recepteur == null) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé.");
            return "redirect:/user/rechercher?nom=" + (nomRecherche != null ? nomRecherche : "");
        }

        boolean demandeExistante = demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(
                userConnecte.getIdUti(), idAmi, List.of("en attente"));

        boolean dejaAmis = demandeAmiRepository.sontDejaAmis(userConnecte.getIdUti(), idAmi);
        if (demandeExistante || dejaAmis) {
            redirectAttributes.addFlashAttribute("error", "Une demande d'ami existe déjà.");
            return "redirect:/user/rechercher?nom=" + (nomRecherche != null ? nomRecherche : "");
        }

        DemandeAmi demande = new DemandeAmi();
        demande.setDemandeur(userConnecte);
        demande.setRecepteur(recepteur);
        demande.setStatut("en attente");
        long timestamp = System.currentTimeMillis();
        demande.setDateDA(timestamp);

        demandeAmiRepository.save(demande);

        redirectAttributes.addFlashAttribute("success", "Demande d'ami envoyée à " + recepteur.getNomU() + ".");
        return "redirect:/user/rechercher?nom=" + (nomRecherche != null ? nomRecherche : "");
    }

}