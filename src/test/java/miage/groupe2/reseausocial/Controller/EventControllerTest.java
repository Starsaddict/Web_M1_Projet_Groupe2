package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.EvenementRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class EventControllerTest {

    private EventController controller;
    private EvenementRepository evenementRepository;
    private UtilisateurRepository utilisateurRepository;
    private UtilisateurService utilisateurService;
    private HttpSession session;
    private Model model;

    private Utilisateur utilisateur;
    private Evenement evenement;

    @BeforeEach
    public void setup() {
        evenementRepository = mock(EvenementRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        utilisateurService = mock(UtilisateurService.class);
        session = mock(HttpSession.class);
        model = mock(Model.class);

        controller = new EventController();
        controller.setEvenementRepository(evenementRepository);
        controller.setUtilisateurRepository(utilisateurRepository);
        controller.setUtilisateurService(utilisateurService);


        utilisateur = new Utilisateur();
        utilisateur.setIdUti(1);
        utilisateur.setEvenements(new ArrayList<>());
        utilisateur.setEvenementsAssistes(new ArrayList<>());

        evenement = new Evenement();
        evenement.setIdEve(10);
        evenement.setParticipants(new ArrayList<>());
        evenement.setCreateur(utilisateur);
    }

    @Test
    public void testCreerEvenement() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateur);
        String view = controller.creerEvenement(evenement, session, LocalDateTime.now(), LocalDateTime.now().plusHours(2), "/evenement/tous");
        verify(evenementRepository, times(1)).save(evenement);
        assertEquals("/evenement/tous", view);
    }

    @Test
    public void testSupprimerEvenement_Creator() {
        evenement.setCreateur(utilisateur);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateur);
        when(evenementRepository.findById(10)).thenReturn(Optional.of(evenement));
        String view = controller.supprimerEvenement(10, session, "/evenement/tous");
        verify(evenementRepository, times(1)).delete(evenement);
        assertEquals("/evenement/tous", view);
    }

    @Test
    public void testModifierEvenement() {
        when(evenementRepository.findByIdEve(10)).thenReturn(evenement);
        String view = controller.modifierEvenement(
                10, "Nom", "Desc", "Adresse", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "/evenement/tous"
        );
        verify(evenementRepository, times(1)).save(evenement);
        assertEquals("/evenement/tous", view);
    }

    @Test
    public void testAfficherTousLesEvenements() {
        utilisateur.setEvenementsAssistes(List.of(evenement));
        utilisateur.setEvenements(List.of(evenement));
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateur);
        when(evenementRepository.findAll()).thenReturn(List.of(evenement));
        evenement.setDateFinE(System.currentTimeMillis() + 100000);
        evenement.setDateDebutE(System.currentTimeMillis() + 1000);

        String view = controller.afficherTousLesEvenements(model, session);
        verify(model).addAttribute(eq("monEvenements"), any());
        verify(model).addAttribute(eq("evenementCree"), any());
        verify(model).addAttribute(eq("upcoming"), any());
        verify(model).addAttribute("user", utilisateur);
        assertEquals("events", view);
    }

    @Test
    public void testRejoindreEvenement() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateur);
        when(evenementRepository.findByIdEve(10)).thenReturn(evenement);

        String view = controller.rejoindreEvenement(10, session, "/evenement/tous");
        verify(utilisateurRepository, times(1)).save(utilisateur);
        verify(session).setAttribute("user", utilisateur);
        assertEquals("/evenement/tous", view);
    }

    @Test
    public void testQuitterEvenement() {
        utilisateur.getEvenementsAssistes().add(evenement);
        evenement.getParticipants().add(utilisateur);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateur);
        when(evenementRepository.findByIdEve(10)).thenReturn(evenement);

        String view = controller.quitterEvenement(10, session, "/evenement/tous");
        verify(utilisateurRepository, times(1)).save(utilisateur);
        verify(session).setAttribute("user", utilisateur);
        assertEquals("/evenement/tous", view);
    }

    @Test
    public void testRedirectToEvenement() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateur);
        when(evenementRepository.findByIdEve(10)).thenReturn(evenement);
        evenement.setDateDebutE(System.currentTimeMillis() + 3600000);
        evenement.getParticipants().add(utilisateur);

        String view = controller.redirectToEvenement(10, model, session);
        verify(model).addAttribute("event", evenement);
        verify(model).addAttribute(eq("eventDate"), any());
        verify(model).addAttribute("estParticipants", true);
        assertEquals("event", view);
    }
}
