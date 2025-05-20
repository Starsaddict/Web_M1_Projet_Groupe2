package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UtilisateurControllerTest {

    @InjectMocks
    private UtilisateurController utilisateurController;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private GroupeRepository groupeRepository;

    @Mock
    private GroupeService groupeService;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowRegisterForm() {
        String viewName = utilisateurController.showRegisterForm(model);
        verify(model).addAttribute(eq("utilisateur"), any(Utilisateur.class));
        assertEquals("form-register", viewName);
    }

    @Test
    void testRegisterUser_NewEmail_ShouldRedirectToLogin() {
        Utilisateur u = new Utilisateur();
        u.setEmailU("new@ex.com");
        u.setMdpU("password");

        when(utilisateurRepository.findAllEmailU()).thenReturn(List.of());

        String viewName = utilisateurController.registerUser(u, model);

        verify(utilisateurRepository).save(any(Utilisateur.class));
        assertEquals("redirect:/auth/login", viewName);
    }

    @Test
    void testRegisterUser_ExistingEmail_ShouldReturnRegisterForm() {
        Utilisateur u = new Utilisateur();
        u.setEmailU("exist@ex.com");

        when(utilisateurRepository.findAllEmailU()).thenReturn(List.of("exist@ex.com"));

        String viewName = utilisateurController.registerUser(u, model);

        verify(model).addAttribute(eq("error"), eq("email exist"));
        assertEquals("form-register", viewName);
    }

    @Test
    void testRechercherUtilisateurs_LoggedOut_ShouldRedirectToLogin() {
        when(session.getAttribute("user")).thenReturn(null);
        String viewName = utilisateurController.rechercherUtilisateurs("nom", model, session);
        assertEquals("redirect:/auth/login", viewName);
    }

    @Test
    void testRechercherUtilisateurs_ValidSearch_ShouldReturnSearchResults() {
        Utilisateur currentUser = new Utilisateur();
        currentUser.setIdUti(1);

        Utilisateur other = new Utilisateur();
        other.setIdUti(2);

        when(session.getAttribute("user")).thenReturn(currentUser);
        when(utilisateurRepository.findByNomUContainingIgnoreCase("nom")).thenReturn(new ArrayList<>(List.of(currentUser, other)));

        String viewName = utilisateurController.rechercherUtilisateurs("nom", model, session);

        verify(model).addAttribute(eq("utilisateurs"), argThat(list -> ((List<?>) list).size() == 1));
        assertEquals("search_results", viewName);
    }

    @Test
    void testSupprimerAmi_ValidIds_ShouldRedirectToMesAmis() {
        Utilisateur user = new Utilisateur();
        Utilisateur ami = new Utilisateur();

        user.setIdUti(1);
        ami.setIdUti(2);

        user.setAmis(new ArrayList<>());
        ami.setAmis(new ArrayList<>());

        user.getAmis().add(ami);
        ami.getAmis().add(user);

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(user));
        when(utilisateurRepository.findById(2)).thenReturn(Optional.of(ami));

        String view = utilisateurController.supprimerAmi(2, session, redirectAttributes);

        verify(utilisateurRepository, times(2)).save(any(Utilisateur.class));
        verify(redirectAttributes).addFlashAttribute("succes", "Ami supprimé avec succès.");
        assertEquals("redirect:/user/mes-amis", view);
    }


    @Test
    void testSupprimerAmi_InvalidIds_ShouldRedirectToMesAmisWithError() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findById(1)).thenReturn(Optional.empty());

        String view = utilisateurController.supprimerAmi(2, session, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Impossible"));
        assertEquals("redirect:/user/mes-amis", view);
    }

    @Test
    void testVoirMesGroupes_ShouldAddAttributesAndReturnView() {
        Utilisateur user = new Utilisateur();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String view = utilisateurController.voirMesGroupes(session, model);

        verify(model).addAttribute(eq("groupesCrees"), any());
        verify(model).addAttribute(eq("groupesMembre"), any());
        assertEquals("mes_groupes", view);
    }

    @Test
    void testJoinGroupe_ShouldJoinAndRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        Groupe groupe = new Groupe();

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(1L)).thenReturn(user);
        when(groupeRepository.findGroupeByidGrp(10)).thenReturn(groupe);

        String result = utilisateurController.joinGroupe(session, 10);

        verify(groupeService).joinGroupe(user, groupe);
        verify(session).setAttribute("user", user);
        assertEquals("redirect:/user/mes-groupes", result);
    }

    @Test
    void testQuitterGroupe_ShouldRemoveAndRedirect() {
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setCreateur(new Utilisateur());

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findGroupeByidGrp(5)).thenReturn(groupe);

        String result = utilisateurController.quitterGroupe(session, 5);

        verify(groupeService).quitterGroupe(user, groupe);
        verify(session).setAttribute("user", user);
        assertEquals("redirect:/user/mes-groupes", result);
    }

    @Test
    void testSupprimerGroupe_ShouldCallServiceAndRedirect() {
        Utilisateur user = new Utilisateur();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String view = utilisateurController.supprimerGroupe(session, 3);

        verify(groupeService).supprimerGroupe(user, 3);
        assertEquals("redirect:/user/mes-groupes", view);
    }
}
