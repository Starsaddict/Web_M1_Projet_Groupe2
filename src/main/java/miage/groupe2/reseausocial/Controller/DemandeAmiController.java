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

import java.util.List;

/**
 * Contrôleur Spring MVC pour gérer les demandes d'amitié entre utilisateurs.
 * Il permet d'accepter, refuser ou envoyer des demandes d'ami.
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
     * Accepte une demande d'amitié si elle est destinée à l'utilisateur connecté.
     *
     * @param idDemande L'identifiant de la demande à accepter.
     * @param session La session HTTP contenant l'utilisateur connecté.
     * @param redirectAttributes Les attributs de redirection pour afficher un message.
     * @param referer L'URL de provenance pour retourner à la page précédente.
     * @return Une redirection vers la page précédente ou "/home".
     */
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

    /**
     * Refuse une demande d'amitié si elle est destinée à l'utilisateur connecté.
     *
     * @param idDemande L'identifiant de la demande à refuser.
     * @param session La session HTTP contenant l'utilisateur connecté.
     * @param redirectAttributes Les attributs de redirection pour afficher un message.
     * @param referer L'URL de provenance pour retourner à la page précédente.
     * @return Une redirection vers la page précédente ou "/home".
     */
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

    /**
     * Envoie une demande d'amitié à un autre utilisateur.
     * Vérifie si l'utilisateur ciblé existe, s’il ne s'agit pas de soi-même,
     * et qu’aucune demande en attente ou relation d’amitié n’existe déjà.
     *
     * @param idAmi L'identifiant de l'utilisateur à qui envoyer la demande.
     * @param nomRecherche Le nom saisi pour la recherche, pour conserver le contexte en cas d'erreur.
     * @param session La session contenant l'utilisateur connecté.
     * @param redirectAttributes Les attributs de redirection pour afficher les messages.
     * @param referer L'URL précédente pour y retourner si possible.
     * @return Une redirection vers la page précédente ou la page de recherche/utilisateur.
     */
    @PostMapping("/ajouterAmi")
    public String envoyerDemandeAmi(@RequestParam("idAmi") Integer idAmi,
                                    @RequestParam(value = "nom", required = false) String nomRecherche,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes,
                                    @RequestHeader(value = "Referer", required = false) String referer
    ) {
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
