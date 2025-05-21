package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.EvenementRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.UtilisateurService;
import miage.groupe2.reseausocial.Util.RedirectUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EventControllerTest {

    @InjectMocks
    EventController eventController;

    @Mock
    EvenementRepository evenementRepository;

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    UtilisateurService utilisateurService;

    @Mock
    HttpSession session;

    @Mock
    Model model;

    Utilisateur user;
    Evenement evenement;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new Utilisateur();
        user.setIdUti(1);
        user.setEvenementsAssistes(new ArrayList<>());
        user.setEvenementsAssistes(new ArrayList<>());

        evenement = new Evenement();
        evenement.setCreateur(user);
        evenement.setParticipants(new ArrayList<>());
        evenement.setIdEve(10);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
    }

    @Test
    void creerEvenement_shouldSaveEventAndRedirect() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime fin = start.plusHours(1);

        String referer = "/previousPage";

        String result = eventController.creerEvenement(evenement, session, start, fin, referer);

        verify(evenementRepository).save(evenement);
        assertEquals(RedirectUtil.getSafeRedirectUrl(referer, EventController.EVENEMENT_TOUS), result);
    }

    @Test
    void supprimerEvenement_shouldDeleteIfCreator() {
        when(evenementRepository.findById(10)).thenReturn(Optional.of(evenement));

        String result = eventController.supprimerEvenement(10, session, "/previousPage");

        verify(evenementRepository).delete(evenement);
        assertEquals(RedirectUtil.getSafeRedirectUrl("/previousPage", "EVENEMENT_TOUS"), result);
    }

    @Test
    void supprimerEvenement_shouldNotDeleteIfNotCreator() {
        Utilisateur autreUser = new Utilisateur();
        autreUser.setIdUti(2);
        evenement.setCreateur(autreUser);

        when(evenementRepository.findById(10)).thenReturn(Optional.of(evenement));

        String result = eventController.supprimerEvenement(10, session, "/previousPage");

        verify(evenementRepository, never()).delete(any());
        assertEquals(RedirectUtil.getSafeRedirectUrl("/previousPage", "EVENEMENT_TOUS"), result);
    }

    @Test
    void modifierEvenement_shouldUpdateAndSave() {
        when(evenementRepository.findByIdEve(10)).thenReturn(evenement);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime fin = start.plusHours(2);

        String result = eventController.modifierEvenement(10, "Nouveau nom", "Nouvelle desc", "Nouvelle adresse", start, fin, "/referer");

        verify(evenementRepository).save(evenement);
        assertEquals(RedirectUtil.getSafeRedirectUrl("/referer", EventController.EVENEMENT_TOUS), result);
        assertEquals("Nouveau nom", evenement.getNomE());
        assertEquals("Nouvelle desc", evenement.getDescription());
        assertEquals("Nouvelle adresse", evenement.getAdresseE());
    }

    @Test
    void afficherTousLesEvenements_shouldPopulateModel() {
    Evenement futureEvent = new Evenement();
    futureEvent.setDateDebutE(System.currentTimeMillis() + 5000);
    futureEvent.setDateFinE(System.currentTimeMillis() + 10000);
    if (futureEvent.getParticipants() == null) {
        futureEvent.setParticipants(new ArrayList<>());
    }
    if (user.getEvenementsAssistes() == null) {
        user.setEvenementsAssistes(new ArrayList<>());
    }
    if (user.getEvenements() == null) {
        user.setEvenements(new ArrayList<>());
    }
    user.getEvenementsAssistes().add(futureEvent);
    user.getEvenements().add(futureEvent);

    if (evenement.getDateFinE() == null) {
        evenement.setDateFinE(System.currentTimeMillis() + 15000);
    }
    if (evenement.getDateDebutE() == null) {
        evenement.setDateDebutE(System.currentTimeMillis() + 10000);
    }
    if (evenement.getParticipants() == null) {
        evenement.setParticipants(new ArrayList<>());
    }

    when(evenementRepository.findAll()).thenReturn(List.of(futureEvent, evenement));

    String view = eventController.afficherTousLesEvenements(model, session);

    verify(model).addAttribute(eq("monEvenements"), any());
    verify(model).addAttribute(eq("evenementCree"), any());
    verify(model).addAttribute(eq("upcoming"), any());
    verify(model).addAttribute("user", user);

    assertEquals("events", view);
}





    @Test
    void rejoindreEvenement_shouldAddParticipant() {
        when(evenementRepository.findByIdEve(10)).thenReturn(evenement);
        String result = eventController.rejoindreEvenement(10, session, "/referer");

        verify(utilisateurRepository).save(user);
        verify(session).setAttribute("user", user);
        assertEquals(RedirectUtil.getSafeRedirectUrl("/referer", EventController.EVENEMENT_TOUS), result);
        assert(user.getEvenementsAssistes().contains(evenement));
        assert(evenement.getParticipants().contains(user));
    }

    @Test
    void quitterEvenement_shouldRemoveParticipant() {
        user.getEvenementsAssistes().add(evenement);
        evenement.getParticipants().add(user);

        when(evenementRepository.findByIdEve(10)).thenReturn(evenement);

        String result = eventController.quitterEvenement(10, session, "/referer");

        verify(utilisateurRepository).save(user);
        verify(session).setAttribute("user", user);
        assertEquals(RedirectUtil.getSafeRedirectUrl("/referer", EventController.EVENEMENT_TOUS), result);
        assert(!user.getEvenementsAssistes().contains(evenement));
        assert(!evenement.getParticipants().contains(user));
    }

    @Test
    void redirectToEvenement_shouldAddAttributesAndReturnView() {
        evenement.setDateDebutE(System.currentTimeMillis() + 5000);
        evenement.setDateFinE(System.currentTimeMillis() + 10000);
        if (evenement.getParticipants() == null) {
            evenement.setParticipants(new ArrayList<>());
        }

        when(evenementRepository.findByIdEve(10)).thenReturn(evenement);

        String view = eventController.redirectToEvenement(10, model, session);

        verify(model).addAttribute(eq("estParticipants"), eq(false));
        verify(model).addAttribute(eq("eventDate"), anyString());
        verify(model).addAttribute("event", evenement);

        assertEquals("event", view);
    }

}
