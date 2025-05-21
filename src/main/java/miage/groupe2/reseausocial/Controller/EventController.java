package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.EvenementRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.DateUtil;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur Spring MVC pour la gestion des événements dans le réseau social.
 */
@Controller
@RequestMapping("/evenement")
public class EventController {

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    public static final String EVENEMENT_TOUS = "/evenement/tous";

    /**
     * Crée un nouvel événement pour l'utilisateur connecté.
     *
     * @param evenement l’événement provenant du formulaire
     * @param session   session HTTP contenant l'utilisateur connecté
     * @param dateStart date et heure de début de l’événement
     * @param dateFin   date et heure de fin de l’événement
     * @param referer   l’URL de provenance pour la redirection
     * @return redirection vers la liste des événements
     */
    @PostMapping("/creer")
    public String creerEvenement(@ModelAttribute Evenement evenement,
                                 HttpSession session,
                                 @RequestParam(name = "start") LocalDateTime dateStart,
                                 @RequestParam(name = "fin") LocalDateTime dateFin,
                                 @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        long dd = DateUtil.toEpochMilli(dateStart);
        long df = DateUtil.toEpochMilli(dateFin);
        evenement.setCreateur(user);
        evenement.setDateDebutE(dd);
        evenement.setDateFinE(df);

        evenementRepository.save(evenement);
        return RedirectUtil.getSafeRedirectUrl(referer, EVENEMENT_TOUS);
    }

    /**
     * Supprime un événement si l'utilisateur connecté en est le créateur.
     *
     * @param id      identifiant de l’événement à supprimer
     * @param session session HTTP contenant l’utilisateur connecté
     * @param referer l’URL de provenance pour la redirection
     * @return redirection vers la liste des événements
     */
    @RequestMapping("/supprimer")
    public String supprimerEvenement(@RequestParam("id") Integer id,
                                     HttpSession session,
                                     @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Evenement evenement = evenementRepository.findById(id).orElse(null);

        if (evenement != null && evenement.getCreateur().getIdUti().equals(user.getIdUti())) {
            evenementRepository.delete(evenement);
        }

        return RedirectUtil.getSafeRedirectUrl(referer, EVENEMENT_TOUS);
    }

    /**
     * Modifie les informations d’un événement existant.
     *
     * @param id          identifiant de l’événement
     * @param nomE        nouveau nom (optionnel)
     * @param description nouvelle description (optionnelle)
     * @param adressE     nouvelle adresse (optionnelle)
     * @param start       nouvelle date de début
     * @param fin         nouvelle date de fin
     * @param referer     l’URL de provenance pour la redirection
     * @return redirection vers la liste des événements
     */
    @PostMapping("/modifier")
    public String modifierEvenement(@RequestParam(name = "id") Integer id,
                                    @RequestParam(name = "nomE", required = false) String nomE,
                                    @RequestParam(name = "description", required = false) String description,
                                    @RequestParam(name = "adressE", required = false) String adressE,
                                    @RequestParam(name = "start") LocalDateTime start,
                                    @RequestParam(name = "fin") LocalDateTime fin,
                                    @RequestHeader(value = "Referer", required = false) String referer) {

        Evenement evenement = evenementRepository.findByIdEve(id);

        if (nomE != null && !nomE.trim().isEmpty()) {
            evenement.setNomE(nomE);
        }

        if (description != null && !description.trim().isEmpty()) {
            evenement.setDescription(description);
        }

        if (adressE != null && !adressE.trim().isEmpty()) {
            evenement.setAdresseE(adressE);
        }

        long dd = DateUtil.toEpochMilli(start);
        long df = DateUtil.toEpochMilli(fin);
        evenement.setDateDebutE(dd);
        evenement.setDateFinE(df);

        evenementRepository.save(evenement);
        return RedirectUtil.getSafeRedirectUrl(referer, EVENEMENT_TOUS);
    }

    /**
     * Affiche tous les événements disponibles : ceux créés ou rejoints par l'utilisateur, ainsi que d'autres événements publics.
     *
     * @param model   le modèle passé à la vue
     * @param session session HTTP contenant l’utilisateur connecté
     * @return la vue des événements
     */
    @GetMapping("/tous")
    public String afficherTousLesEvenements(Model model, HttpSession session) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        long time = System.currentTimeMillis();

        List<Evenement> monEvenements = user.getEvenementsAssistes().stream()
                .filter(e -> e.getDateFinE() > time)
                .sorted(Comparator.comparing(Evenement::getDateDebutE))
                .collect(Collectors.toList());

        List<Evenement> evenementCree = user.getEvenements().stream()
                .filter(e -> e.getDateFinE() > time)
                .sorted(Comparator.comparing(Evenement::getDateDebutE))
                .collect(Collectors.toList());

        List<Evenement> upcoming = evenementRepository.findAll().stream()
                .filter(e -> e.getDateFinE() > time)
                .filter(e -> !e.getParticipants().contains(user))
                .limit(4)
                .collect(Collectors.toList());

        model.addAttribute("monEvenements", monEvenements);
        model.addAttribute("evenementCree", evenementCree);
        model.addAttribute("upcoming", upcoming);
        model.addAttribute("user", user);
        return "events";
    }

    /**
     * Permet à l'utilisateur connecté de rejoindre un événement.
     *
     * @param id      identifiant de l'événement
     * @param session session HTTP
     * @param referer l’URL de provenance pour la redirection
     * @return redirection vers la vue des événements
     */
    @RequestMapping("/rejoindre")
    public String rejoindreEvenement(@RequestParam("id") Integer id,
                                     HttpSession session,
                                     @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Evenement event = evenementRepository.findByIdEve(id);

        if (user != null && event != null && !user.getEvenementsAssistes().contains(event)) {
            user.getEvenementsAssistes().add(event);
            event.getParticipants().add(user);
            utilisateurRepository.save(user);
            session.setAttribute("user", user);
        }

        return RedirectUtil.getSafeRedirectUrl(referer, EVENEMENT_TOUS);
    }

    /**
     * Permet à l'utilisateur connecté de quitter un événement.
     *
     * @param id      identifiant de l'événement
     * @param session session HTTP
     * @param referer l’URL de provenance pour la redirection
     * @return redirection vers la vue des événements
     */
    @RequestMapping("/quitter")
    public String quitterEvenement(@RequestParam("id") Integer id,
                                   HttpSession session,
                                   @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Evenement event = evenementRepository.findByIdEve(id);

        if (user != null && event != null && user.getEvenementsAssistes().contains(event)) {
            user.getEvenementsAssistes().remove(event);
            event.getParticipants().remove(user);
            utilisateurRepository.save(user);
            session.setAttribute("user", user);
        }

        return RedirectUtil.getSafeRedirectUrl(referer, EVENEMENT_TOUS);
    }

    /**
     * Redirige vers la page d'un événement spécifique.
     *
     * @param idEve   identifiant de l'événement
     * @param model   modèle pour la vue
     * @param session session HTTP
     * @return la vue d’un événement spécifique
     */
    @GetMapping("")
    public String redirectToEvenement(@RequestParam(name = "idEve") Integer idEve,
                                      Model model,
                                      HttpSession session) {

        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Evenement eve = evenementRepository.findByIdEve(idEve);
        Instant instant = Instant.ofEpochMilli(eve.getDateDebutE());
        ZonedDateTime utcTime = instant.atZone(ZoneOffset.UTC);
        String isoUtc = utcTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        model.addAttribute("estParticipants", eve.getParticipants().contains(user));
        model.addAttribute("eventDate", isoUtc);
        model.addAttribute("event", eve);
        return "event";
    }
}
