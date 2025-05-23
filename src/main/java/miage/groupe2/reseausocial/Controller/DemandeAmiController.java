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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



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
    @RequestMapping("/ajouterAmi")
    public String envoyerDemandeAmi(@RequestParam("idAmi") Integer idAmi,
                                    HttpSession session,
                                    @RequestHeader(value = "Referer", required = false) String referer
    ) {

        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);

        boolean demandeExist = demandeAmiRepository.findByDemandeur(userConnecte).stream()
                .filter(d -> d.getStatut().equals("en attente"))
                .anyMatch(d -> d.getRecepteur().getIdUti().equals(idAmi));

        boolean dejaAmis = userConnecte.getAmis().stream().anyMatch(a -> a.getIdUti().equals(idAmi));

        if (demandeExist || dejaAmis) {
            return RedirectUtil.getSafeRedirectUrl(referer, "/mes-amis");
        }

        Utilisateur recepteur = utilisateurRepository.findByidUti(idAmi);

        DemandeAmi demande = new DemandeAmi();
        demande.setDemandeur(userConnecte);
        demande.setRecepteur(recepteur);
        demande.setStatut("en attente");
        demande.setDateDA(System.currentTimeMillis());

        demandeAmiRepository.save(demande);

        return RedirectUtil.getSafeRedirectUrl(referer, "/user/rechercher" );
    }

}
