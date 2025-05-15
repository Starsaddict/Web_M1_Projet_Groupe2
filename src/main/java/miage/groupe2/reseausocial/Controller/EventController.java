package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.EvenementRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion des événements
 */
@Controller
@RequestMapping("/evenement")
public class EventController {

    @Autowired
    private EvenementRepository evenementRepository;

    /**
     * Affiche le formulaire de création d’un nouvel événement.
     *
     * @param model le modèle utilisé pour passer l’objet Evenement au formulaire
     * @return la vue du formulaire de création
     */
    @GetMapping("/creer")
    public String afficherFormulaireCreation(Model model) {
        model.addAttribute("evenement", new Evenement());
        return "creerEvenement";
    }

    /**
     * Traite la soumission du formulaire de création d’un événement.
     * Associe l'utilisateur connecté comme créateur de l'événement.
     *
     * @param evenement l’événement rempli depuis le formulaire
     * @param session la session HTTP contenant l’utilisateur connecté
     * @return redirection vers la liste des événements créés par l’utilisateur
     */
    @PostMapping("/creer")
    public String creerEvenement(@ModelAttribute Evenement evenement, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        if (evenement.getDateDebutEString() != null && !evenement.getDateDebutEString().isEmpty()) {
            LocalDateTime debut = LocalDateTime.parse(evenement.getDateDebutEString(), formatter);
            long timestampDebut = debut.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            evenement.setDateDebutE(timestampDebut);
        }

        if (evenement.getDateFinEString() != null && !evenement.getDateFinEString().isEmpty()) {
            LocalDateTime fin = LocalDateTime.parse(evenement.getDateFinEString(), formatter);
            long timestampFin = fin.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            evenement.setDateFinE(timestampFin);
        }

        evenement.setCreateur(user);
        evenementRepository.save(evenement);
        return "redirect:/evenement/maListEvenement";
    }



/**
 * Affiche la liste des événements créés par l'utilisateur connecté.
 *
 * @param model le modèle utilisé pour passer la liste des événements à la vue
 * @param session la session HTTP contenant l'utilisateur connecté
 * @return la vue affichant les événements de l'utilisateur, ou redirection vers la page de login si non connecté
 */
@GetMapping("/maListEvenement")
public String afficherMesEvenements(Model model, HttpSession session) {
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    if (user == null) return "redirect:/auth/login";

    model.addAttribute("evenements", evenementRepository.findByCreateur(user));
    return "maListEvenement";
}


/**
 * Supprime un événement si l'utilisateur connecté en est le créateur.
 *
 * @param id l’identifiant de l’événement à supprimer
 * @param session la session HTTP contenant l’utilisateur connecté
 * @return redirection vers la liste des événements de l’utilisateur
 */
@PostMapping("/supprimer")
public String supprimerEvenement(@RequestParam("id") Integer id, HttpSession session) {
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    if (user == null) return "redirect:/auth/login";

    Evenement evenement = evenementRepository.findById(id).orElse(null);
    if (evenement != null && evenement.getCreateur().getIdUti().equals(user.getIdUti())) {
        evenementRepository.delete(evenement);
    }

    return "redirect:/evenement/maListEvenement";
}

/**
 * Affiche le formulaire de modification d’un événement existant.
 * Vérifie que l'utilisateur est le créateur de l'événement.
 *
 * @param id identifiant de l’événement à modifier
 * @param session la session HTTP contenant l’utilisateur connecté
 * @param model le modèle pour passer l’événement à modifier à la vue
 * @return la vue du formulaire de modification, ou redirection si accès non autorisé
 */
@GetMapping("/modifier")
public String afficherFormulaireModification(@RequestParam("id") Integer id, HttpSession session, Model model) {
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    if (user == null) return "redirect:/auth/login";

    Evenement evenement = evenementRepository.findById(id).orElse(null);
    if (evenement == null || !evenement.getCreateur().getIdUti().equals(user.getIdUti())) {
        return "redirect:/evenement/maListEvenement";
    }

    model.addAttribute("evenement", evenement);
    return "modifierEvenement";
}

/**
 * Traite la soumission du formulaire de modification d’un événement.
 *
 * @param evenementModifie l’objet contenant les modifications de l’événement
 * @param session la session HTTP contenant l’utilisateur connecté
 * @return redirection vers la liste des événements de l’utilisateur
 */
@PostMapping("/modifier")
public String modifierEvenement(@ModelAttribute Evenement evenementModifie, HttpSession session) {
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    if (user == null) return "redirect:/auth/login";

    Evenement existant = evenementRepository.findById(evenementModifie.getIdEve()).orElse(null);
    if (existant != null && existant.getCreateur().getIdUti().equals(user.getIdUti())) {
        existant.setNomE(evenementModifie.getNomE());
        existant.setDateDebutE(evenementModifie.getDateDebutE());
        existant.setDateFinE(evenementModifie.getDateFinE());
        existant.setAdresseE(evenementModifie.getAdresseE());
        evenementRepository.save(existant);
    }

    return "redirect:/evenement/maListEvenement";
}




}
