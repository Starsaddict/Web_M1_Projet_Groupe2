package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.EvenementRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import miage.groupe2.reseausocial.Util.DateUtil;
import miage.groupe2.reseausocial.service.UtilisateurService;
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

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private UtilisateurService utilisateurService;


    /**
     * Traite la soumission du formulaire de création d’un événement.
     * Associe l'utilisateur connecté comme créateur de l'événement.
     *
     * @param evenement l’événement rempli depuis le formulaire
     * @param session la session HTTP contenant l’utilisateur connecté
     * @return redirection vers la liste des événements créés par l’utilisateur
     */
    @PostMapping("/creer")
    public String creerEvenement(@ModelAttribute Evenement evenement,
                                 HttpSession session,
                                 @RequestParam(name = "start") LocalDateTime dateStart,
                                 @RequestParam(name="fin") LocalDateTime dateFin,
                                 @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        long dd = DateUtil.toEpochMilli(dateStart);
        long df = DateUtil.toEpochMilli(dateFin);
        evenement.setCreateur(user);
        evenement.setDateDebutE(dd);
        evenement.setDateFinE(df);

        evenementRepository.save(evenement);
        return "redirect:" + (referer != null ? referer : "/evenement/tous");

    }





    /**
     * Supprime un événement si l'utilisateur connecté en est le créateur.
     *
     * @param id l’identifiant de l’événement à supprimer
     * @param session la session HTTP contenant l’utilisateur connecté
     * @return redirection vers la liste des événements de l’utilisateur
     */
    @RequestMapping("/supprimer")
    public String supprimerEvenement(@RequestParam("id") Integer id,
                                     HttpSession session,
                                     @RequestHeader(value = "Referer", required = false) String referer

    ) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        Evenement evenement = evenementRepository.findById(id).orElse(null);
        if (evenement != null && evenement.getCreateur().getIdUti().equals(user.getIdUti())) {
            evenementRepository.delete(evenement);
        }

        return "redirect:" + (referer != null ? referer : "/evenement/tous");
    }


    /**
     * Traite la soumission du formulaire de modification d’un événement.
     *
     * @ modifierEvenement l’objet contenant les modifications de l’événement
     * @param session la session HTTP contenant l’utilisateur connecté
     * @return redirection vers la liste des événements de l’utilisateur
     */
    @PostMapping("/modifier")
    public String modifierEvenement(@RequestParam(name = "id") Integer id,
                                    @RequestParam(name = "nomE",required = false) String nomE,
                                    @RequestParam(name = "description",required = false) String description,
                                    @RequestParam(name = "adressE",required = false) String adressE,
                                    HttpSession session,
                                    @RequestParam(name = "start") LocalDateTime start,
                                    @RequestParam(name = "fin") LocalDateTime fin,
                                    @RequestHeader(value = "Referer", required = false) String referer


    ) {
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

        return "redirect:" + (referer != null ? referer : "/evenement/tous");
    }

    /**
     * Affiche tous les événements existants, qu’ils soient créés par l’utilisateur ou par d’autres.
     * Permet à l’utilisateur de voir ceux auxquels il peut participer ou qu’il a déjà rejoints.
     *
     * @param model   le modèle utilisé pour passer la liste des événements à la vue
     * @param session la session HTTP contenant l’utilisateur connecté
     * @return la vue affichant tous les événements, ou redirection vers la page de login si non connecté
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

        List<Evenement> upcoming = evenementRepository.findAll();

        upcoming = upcoming.stream()
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
     * Permet à un utilisateur connecté de rejoindre un événement donné.
     *
     * @param id      identifiant de l'événement à rejoindre
     * @param session session HTTP pour récupérer et mettre à jour l'utilisateur
     * @return redirection vers la liste des événements
     */
    @RequestMapping("/rejoindre")
    public String rejoindreEvenement(@RequestParam("id") Integer id,
                                     HttpSession session,
                                     @RequestHeader(value = "Referer", required = false) String referer

    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Evenement event = evenementRepository.findByIdEve(id);

        if (user != null && event != null && !user.getEvenementsAssistes().contains(event)) {
            user.getEvenementsAssistes().add(event);
            event.getParticipants().add(user);
            utilisateurRepository.save(user);
            session.setAttribute("user", user);
        }
        return "redirect:" + (referer != null ? referer : "/evenement/tous");
    }



    /**
     * Permet à un utilisateur connecté de quitter un événement donné.
     *
     * @param id      identifiant de l'événement à quitter
     * @param session session HTTP pour récupérer et mettre à jour l'utilisateur
     * @return redirection vers la liste des événements
     */
    @RequestMapping("/quitter")
    public String quitterEvenement(@RequestParam("id") Integer id,
                                   HttpSession session,
                                   @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Evenement event = evenementRepository.findByIdEve(id);

        if (user != null && event != null && user.getEvenementsAssistes().contains(event)) {
            user.getEvenementsAssistes().remove(event);
            event.getParticipants().remove(user);
            utilisateurRepository.save(user);
            session.setAttribute("user", user);
        }
        return "redirect:" + (referer != null ? referer : "/evenement/tous");
    }



    @GetMapping("")
    public String redirectToEvenement(
            @RequestParam(name = "idEve") Integer idEve,
            Model model,
            HttpSession session
    ) {

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
