package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.UtilisateurService;
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
    @Autowired
    private UtilisateurService utilisateurService;


    @PostMapping("/accepter")
    public String accepterDemande(@RequestParam("idDemande") Integer idDemande,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes,
                                  @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);

        DemandeAmi demande = demandeAmiRepository.findByIdDA(idDemande);
        if (demande != null && demande.getRecepteur().getIdUti().equals(userConnecte.getIdUti())) {
            demande.setStatut("acceptée");
            demandeAmiRepository.save(demande);
            demandeAmiRepository.ajouterLienAmitie(demande.getDemandeur().getIdUti(), userConnecte.getIdUti());
            redirectAttributes.addFlashAttribute("succes", "Demande d'ami acceptée.");
        }

        return RedirectUtil.getSafeRedirectUrl(referer, "/home");
    }

    @PostMapping("/refuser")
    public String refuserDemande(@RequestParam("idDemande") Integer idDemande,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);

        DemandeAmi demande = demandeAmiRepository.findById(idDemande).orElse(null);
        if (demande != null && demande.getRecepteur().getIdUti().equals(userConnecte.getIdUti())) {
            demande.setStatut("refusée");
            demandeAmiRepository.save(demande);
            redirectAttributes.addFlashAttribute("succes", "Demande d'ami refusée.");
        }

        return RedirectUtil.getSafeRedirectUrl(referer, "/home");
    }


    @RequestMapping("/ajouterAmi")
    public String envoyerDemandeAmi(@RequestParam("idAmi") Integer idAmi,
                                    @RequestParam(value = "nom", required = false) String nomRecherche,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes,
                                    @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);

        boolean demandeExist = demandeAmiRepository.findByDemandeur(userConnecte).stream()
                .filter(d -> d.getStatut().equals("en attente"))
                .anyMatch(d -> d.getRecepteur().getIdUti().equals(idAmi));

        boolean dejaAmis = userConnecte.getAmis().stream().anyMatch(a -> a.getIdUti().equals(idAmi));

        if (demandeExist || dejaAmis) {
            redirectAttributes.addFlashAttribute("error", "Une demande d'ami existe déjà.");
            return RedirectUtil.getSafeRedirectUrl(referer, "/mes-amis");
        }

        Utilisateur recepteur = utilisateurRepository.findByidUti(idAmi);

        DemandeAmi demande = new DemandeAmi();
        demande.setDemandeur(userConnecte);
        demande.setRecepteur(recepteur);
        demande.setStatut("en attente");
        long timestamp = System.currentTimeMillis();
        demande.setDateDA(timestamp);

        demandeAmiRepository.save(demande);

        redirectAttributes.addFlashAttribute("success", "Demande d'ami envoyée à " + recepteur.getNomU() + ".");

        if (referer != null) {
            return RedirectUtil.getSafeRedirectUrl(referer, "/home");
        }

        String redirectUrl = "/home";
        if (nomRecherche != null && !nomRecherche.isEmpty()) {
            redirectUrl = "/user/rechercher?nom=" + nomRecherche;
        }

        return "redirect:" + redirectUrl;
    }

}