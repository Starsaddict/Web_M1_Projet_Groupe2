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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Contrôleur pour la gestion des demandes d'amis.
 */
@Controller
@RequestMapping("/demande")
public class DemandeAmiController {

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Accepte une demande d'ami.
     *
     * @param idDemande identifiant de la demande
     * @param session session utilisateur
     * @param redirectAttributes attributs de redirection
     * @param referer URL précédente
     * @return redirection
     */
    @PostMapping("/accepter")
    public String accepterDemande(@RequestParam("idDemande") Integer idDemande,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes,
                                  @RequestHeader(value = "Referer", required = false) String referer) {
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

    /**
     * Refuse une demande d'ami.
     *
     * @param idDemande identifiant de la demande
     * @param session session utilisateur
     * @param redirectAttributes attributs de redirection
     * @param referer URL précédente
     * @return redirection
     */
    @PostMapping("/refuser")
    public String refuserDemande(@RequestParam("idDemande") Integer idDemande,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);
        DemandeAmi demande = demandeAmiRepository.findById(idDemande).orElse(null);

        if (demande != null && demande.getRecepteur().getIdUti().equals(userConnecte.getIdUti())) {
            demande.setStatut("refusée");
            demandeAmiRepository.save(demande);
            redirectAttributes.addFlashAttribute("succes", "Demande d'ami refusée.");
        }

        return RedirectUtil.getSafeRedirectUrl(referer, "/home");
    }

    /**
     * Envoie une demande d'ami à un utilisateur.
     *
     * @param idAmi identifiant du destinataire
     * @param nomRecherche nom utilisé pour la recherche
     * @param session session utilisateur
     * @param redirectAttributes attributs de redirection
     * @param referer URL précédente
     * @return redirection
     */
    @PostMapping("/ajouterAmi")
    public String envoyerDemandeAmi(@RequestParam("idAmi") Integer idAmi,
                                    @RequestParam(value = "nom", required = false) String nomRecherche,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes,
                                    @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);

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
        demande.setDateDA(System.currentTimeMillis());

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
