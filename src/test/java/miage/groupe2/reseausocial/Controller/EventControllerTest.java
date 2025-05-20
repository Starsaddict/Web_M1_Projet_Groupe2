package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.EvenementRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventControllerTest {

    @Mock
    private EvenementRepository evenementRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private EventController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAfficherFormulaireCreation() {
        String view = controller.afficherFormulaireCreation(model);
        assertEquals("creerEvenement", view);
        verify(model).addAttribute(eq("evenement"), any(Evenement.class));
    }

    @Test
    void testCreerEvenement_NotLoggedIn() {
        when(session.getAttribute("user")).thenReturn(null);
        String result = controller.creerEvenement(new Evenement(), session);
        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void testAfficherMesEvenements_UserNotLoggedIn() {
        when(session.getAttribute("user")).thenReturn(null);
        String result = controller.afficherMesEvenements(model, session);
        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void testAfficherMesEvenements_Success() {
        Utilisateur user = new Utilisateur();
        List<Evenement> events = List.of(new Evenement());

        when(session.getAttribute("user")).thenReturn(user);
        when(evenementRepository.findByCreateur(user)).thenReturn(events);

        String result = controller.afficherMesEvenements(model, session);
        assertEquals("maListEvenement", result);
        verify(model).addAttribute("evenements", events);
    }

    @Test
    void testSupprimerEvenement_UserNotLoggedIn() {
        when(session.getAttribute("user")).thenReturn(null);
        String result = controller.supprimerEvenement(1, session);
        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void testSupprimerEvenement_Success() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        Evenement e = new Evenement();
        e.setIdEve(1);
        e.setCreateur(user);

        when(session.getAttribute("user")).thenReturn(user);
        when(evenementRepository.findById(1)).thenReturn(Optional.of(e));

        String result = controller.supprimerEvenement(1, session);
        assertEquals("redirect:/evenement/maListEvenement", result);
        verify(evenementRepository).delete(e);
    }

    @Test
    void testRejoindreEvenement_Success() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        user.setEvenementsAssistes(new ArrayList<>());

        Evenement e = new Evenement();
        e.setIdEve(1);
        e.setParticipants(new ArrayList<>());

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(user));
        when(evenementRepository.findById(1)).thenReturn(Optional.of(e));

        String result = controller.rejoindreEvenement(1, session);
        assertEquals("redirect:/evenement/tous", result);
        verify(utilisateurRepository).save(user);
        assertTrue(user.getEvenementsAssistes().contains(e));
    }

    @Test
    void testQuitterEvenement_Success() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        Evenement e = new Evenement();
        e.setIdEve(1);
        e.setParticipants(new ArrayList<>());

        user.setEvenementsAssistes(new ArrayList<>(List.of(e)));
        e.setParticipants(new ArrayList<>(List.of(user)));

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(user));
        when(evenementRepository.findById(1)).thenReturn(Optional.of(e));

        String result = controller.quitterEvenement(1, session);
        assertEquals("redirect:/evenement/tous", result);
        verify(utilisateurRepository).save(user);
        assertFalse(user.getEvenementsAssistes().contains(e));
    }

    @Test
    void testRedirectToTous() {
        String view = controller.redirectToTous();
        assertEquals("redirect:/evenement/tous", view);
    }

}
