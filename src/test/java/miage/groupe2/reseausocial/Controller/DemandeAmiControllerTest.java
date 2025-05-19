package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DemandeAmiControllerTest {

    @InjectMocks
    private DemandeAmiController controller;

    @Mock
    private DemandeAmiRepository demandeAmiRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void afficherDemandesRecues_UserNotLogged_RedirectToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String result = controller.afficherDemandesRecues(session, model);

        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void afficherDemandesRecues_UserLogged_ReturnsListe() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(session.getAttribute("user")).thenReturn(user);

        List<DemandeAmi> demandes = List.of(new DemandeAmi());
        when(demandeAmiRepository.findByRecepteurIdUtiAndStatut(1, "en attente")).thenReturn(demandes);

        String result = controller.afficherDemandesRecues(session, model);

        verify(model).addAttribute("demandesRecues", demandes);
        assertEquals("listeDemandeAmi", result);
    }

    @Test
    void accepterDemande_UserNotLogged_RedirectToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String result = controller.accepterDemande(1, session, redirectAttributes, null);

        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void accepterDemande_ValidRequest_SuccessRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(2);

        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(1);

        DemandeAmi demande = new DemandeAmi();
        demande.setRecepteur(user);
        demande.setDemandeur(demandeur);

        when(session.getAttribute("user")).thenReturn(user);
        when(demandeAmiRepository.findById(10)).thenReturn(Optional.of(demande));

        String result = controller.accepterDemande(10, session, redirectAttributes, "/somepage");

        assertEquals("redirect:/somepage", result);
        assertEquals("acceptée", demande.getStatut());
        verify(demandeAmiRepository).save(demande);
        verify(demandeAmiRepository).ajouterLienAmitie(1, 2);
        verify(redirectAttributes).addFlashAttribute("succes", "Demande d'ami acceptée.");
    }

    @Test
    void refuserDemande_UserNotLogged_RedirectToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String result = controller.refuserDemande(1, session, redirectAttributes, null);

        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void refuserDemande_ValidRequest_SuccessRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(2);

        DemandeAmi demande = new DemandeAmi();
        demande.setRecepteur(user);

        when(session.getAttribute("user")).thenReturn(user);
        when(demandeAmiRepository.findById(20)).thenReturn(Optional.of(demande));

        String result = controller.refuserDemande(20, session, redirectAttributes, "/previous");

        assertEquals("redirect:/previous", result);
        assertEquals("refusée", demande.getStatut());
        verify(demandeAmiRepository).save(demande);
        verify(redirectAttributes).addFlashAttribute("succes", "Demande d'ami refusée.");
    }

    @Test
    void envoyerDemandeAmi_UserNotLogged_RedirectToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String result = controller.envoyerDemandeAmi(1, null, session, redirectAttributes);

        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void envoyerDemandeAmi_AddSelf_ReturnErrorRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(5);
        when(session.getAttribute("user")).thenReturn(user);

        String result = controller.envoyerDemandeAmi(5, "nomTest", session, redirectAttributes);

        assertEquals("redirect:/user/rechercher?nom=nomTest", result);
        verify(redirectAttributes).addFlashAttribute("error", "Vous ne pouvez pas vous ajouter vous-même.");
    }

    @Test
    void envoyerDemandeAmi_UserNotFound_ReturnErrorRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(5);
        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(10)).thenReturn(null);

        String result = controller.envoyerDemandeAmi(10, null, session, redirectAttributes);

        assertEquals("redirect:/user/rechercher?nom=", result);
        verify(redirectAttributes).addFlashAttribute("error", "Utilisateur non trouvé.");
    }

    @Test
    void envoyerDemandeAmi_DemandeExistante_ReturnErrorRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(5);
        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(10);

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(10)).thenReturn(recepteur);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(5, 10, List.of("en attente"))).thenReturn(true);

        String result = controller.envoyerDemandeAmi(10, null, session, redirectAttributes);

        assertEquals("redirect:/user/rechercher?nom=", result);
        verify(redirectAttributes).addFlashAttribute("error", "Une demande d'ami existe déjà.");
    }

    @Test
    void envoyerDemandeAmi_DejaAmis_ReturnErrorRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(5);
        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(10);

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(10)).thenReturn(recepteur);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(5, 10, List.of("en attente"))).thenReturn(false);
        when(demandeAmiRepository.sontDejaAmis(5, 10)).thenReturn(true);

        String result = controller.envoyerDemandeAmi(10, null, session, redirectAttributes);

        assertEquals("redirect:/user/rechercher?nom=", result);
        verify(redirectAttributes).addFlashAttribute("error", "Une demande d'ami existe déjà.");
    }

    @Test
    void envoyerDemandeAmi_Success() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(5);
        user.setNomU("Jean");
        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(10);
        recepteur.setNomU("Paul");

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(10)).thenReturn(recepteur);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(5, 10, List.of("en attente"))).thenReturn(false);
        when(demandeAmiRepository.sontDejaAmis(5, 10)).thenReturn(false);

        String result = controller.envoyerDemandeAmi(10, "recherche", session, redirectAttributes);

        assertEquals("redirect:/user/rechercher?nom=recherche", result);
        verify(demandeAmiRepository).save(any(DemandeAmi.class));
        verify(redirectAttributes).addFlashAttribute("success", "Demande d'ami envoyée à Paul.");
    }
}
