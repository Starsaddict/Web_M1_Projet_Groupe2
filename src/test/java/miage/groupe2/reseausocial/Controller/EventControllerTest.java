package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.EvenementRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventControllerTest {

    @InjectMocks
    private EventController controller;

    @Mock
    private EvenementRepository evenementRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void creerEvenement_nonConnecte_redirigeLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String view = controller.creerEvenement(new Evenement(), session);

        assertEquals("redirect:/auth/login", view);
        verifyNoInteractions(evenementRepository);
    }

    @Test
    void creerEvenement_connecte_sauvegardeEtRedirige() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(session.getAttribute("user")).thenReturn(user);

        Evenement evt = new Evenement();
        evt.setDateDebutEString("2025-05-20T10:00");
        evt.setDateFinEString("2025-05-20T12:00");

        String view = controller.creerEvenement(evt, session);

        assertEquals("redirect:/evenement/maListEvenement", view);
        assertEquals(user, evt.getCreateur());
        verify(evenementRepository).save(evt);
    }

    @Test
    void afficherMesEvenements_nonConnecte_redirigeLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String view = controller.afficherMesEvenements(model, session);

        assertEquals("redirect:/auth/login", view);
        verifyNoInteractions(evenementRepository);
    }

    @Test
    void afficherMesEvenements_connecte_afficheListe() {
        Utilisateur user = new Utilisateur();
        when(session.getAttribute("user")).thenReturn(user);
        List<Evenement> liste = new ArrayList<>();
        when(evenementRepository.findByCreateur(user)).thenReturn(liste);

        String view = controller.afficherMesEvenements(model, session);

        assertEquals("maListEvenement", view);
        verify(model).addAttribute("evenements", liste);
    }

    @Test
    void supprimerEvenement_nonConnecte_redirigeLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String view = controller.supprimerEvenement(1, session);

        assertEquals("redirect:/auth/login", view);
        verifyNoInteractions(evenementRepository);
    }

    @Test
    void supprimerEvenement_connecteEtCreateur_supprimeEtRedirige() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(session.getAttribute("user")).thenReturn(user);

        Evenement evt = new Evenement();
        evt.setCreateur(user);
        evt.setIdEve(10);

        when(evenementRepository.findById(10)).thenReturn(Optional.of(evt));

        String view = controller.supprimerEvenement(10, session);

        assertEquals("redirect:/evenement/maListEvenement", view);
        verify(evenementRepository).delete(evt);
    }

    @Test
    void supprimerEvenement_connectePasCreateur_neFaitRien() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        Utilisateur autre = new Utilisateur();
        autre.setIdUti(2);

        when(session.getAttribute("user")).thenReturn(user);

        Evenement evt = new Evenement();
        evt.setCreateur(autre);
        evt.setIdEve(20);

        when(evenementRepository.findById(20)).thenReturn(Optional.of(evt));

        String view = controller.supprimerEvenement(20, session);

        assertEquals("redirect:/evenement/maListEvenement", view);
        verify(evenementRepository, never()).delete(any());
    }
}
