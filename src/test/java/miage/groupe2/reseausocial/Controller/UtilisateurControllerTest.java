package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UtilisateurControllerTest {

    @InjectMocks
    UtilisateurController controller;

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    GroupeRepository groupeRepository;

    @Mock
    GroupeService groupeService;

    @Mock
    UtilisateurService utilisateurService;

    @Mock
    HttpSession session;

    @Mock
    Model model;

    @Mock
    RedirectAttributes redirectAttributes;

    @Captor
    ArgumentCaptor<Utilisateur> utilisateurCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowRegisterForm() {
        String view = controller.showRegisterForm(model);
        assertEquals("form-register", view);
        verify(model).addAttribute(eq("utilisateur"), any(Utilisateur.class));
    }

    @Test
    void testRegisterUser_NewEmail() {
        Utilisateur u = new Utilisateur();
        u.setMdpU("pwd");
        u.setEmailU("new@mail.com");
        when(utilisateurRepository.findAllEmailU()).thenReturn(List.of("old@mail.com"));

        String view = controller.registerUser(u, model);
        assertEquals("redirect:/auth/login", view);
        verify(utilisateurRepository).save(any(Utilisateur.class));
    }

    @Test
    void testRegisterUser_ExistingEmail() {
        Utilisateur u = new Utilisateur();
        u.setEmailU("existing@mail.com");
        when(utilisateurRepository.findAllEmailU()).thenReturn(List.of("existing@mail.com"));

        String view = controller.registerUser(u, model);
        assertEquals("form-register", view);
        verify(model).addAttribute("error", "email exist");
    }

    @Test
    void testRechercherUtilisateurs() {
        Utilisateur current = new Utilisateur();
        current.setIdUti(1);
        Utilisateur other = new Utilisateur();
        other.setIdUti(2);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(current);
        when(utilisateurRepository.findByNomUContainingIgnoreCase("test")).thenReturn(new ArrayList<>(List.of(current, other)));

        String view = controller.rechercherUtilisateurs("test", model, session);
        assertEquals("search_results", view);
        verify(model).addAttribute(eq("utilisateurs"), argThat(list -> ((List<?>) list).size() == 1));
    }

    @Test
    void testVoirMesAmis() {
        Utilisateur u = new Utilisateur();
        u.setAmis(List.of(new Utilisateur()));
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);

        String view = controller.voirMesAmis(session, model);
        assertEquals("listeamis", view);
        verify(model).addAttribute("amis", u.getAmis());
    }

    @Test
    void testSupprimerAmi_Success() {
        Utilisateur u1 = new Utilisateur();
        u1.setIdUti(1);
        Utilisateur u2 = new Utilisateur();
        u2.setIdUti(2);
        u1.setAmis(new ArrayList<>(List.of(u2)));
        u2.setAmis(new ArrayList<>(List.of(u1)));

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u1);
        when(utilisateurRepository.findByIdUti(2)).thenReturn(u2);

        String view = controller.supprimerAmi(2, session, redirectAttributes, null);
        assertTrue(view.contains("redirect:/user/mes-amis"));
        verify(utilisateurRepository, times(2)).save(any(Utilisateur.class));
        verify(redirectAttributes).addFlashAttribute("succes", "Ami supprimé avec succès.");
    }

    @Test
    void testUserProfil() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(2);
        Utilisateur sessionUser = new Utilisateur();
        sessionUser.setIdUti(1);
        sessionUser.setAmis(List.of(user));
        DemandeAmi demande = new DemandeAmi();
        demande.setRecepteur(user);
        demande.setStatut("en attente");
        sessionUser.setDemandesEnvoyees(List.of(demande));
        Post post = new Post();
        post.setDatePost(123L);
        user.setPosts(List.of(post));
        user.setPostsRepostes(List.of(post));
        user.setAmis(List.of(sessionUser));

        when(utilisateurRepository.findByIdUti(2)).thenReturn(user);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(sessionUser);

        String view = controller.userProfil("post", 2, model, session);
        assertEquals("profil_user", view);
        verify(model).addAttribute(eq("etreAmi"), eq(true));
        verify(model).addAttribute(eq("demandeEnvoyee"), eq(true));
    }

    @Test
    void testModifierProfil() {
        Utilisateur u = new Utilisateur();
        u.setIdUti(5);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);

        String view = controller.modifierProfil("pseudo", "email", "intro", session);
        assertEquals("redirect:/user/5/profil", view);
        verify(utilisateurRepository).save(u);
        verify(session).setAttribute("user", u);
    }

    @Test
    void testModifierPassword_CorrectCurrent() {
        Utilisateur u = new Utilisateur();
        u.setMdpU(org.mindrot.jbcrypt.BCrypt.hashpw("old", org.mindrot.jbcrypt.BCrypt.gensalt()));
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);

        String view = controller.modifierPassword("old", "new", session, null);
        assertTrue(view.contains("/user/modifierProfil"));
        verify(utilisateurRepository).save(u);
        verify(session).setAttribute("user", u);
    }

    @Test
    void testJoinGroupe() {
        Utilisateur u = new Utilisateur();
        u.setGroupesAppartenance(new ArrayList<>());
        Groupe g = new Groupe();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(g);

        String view = controller.joinGroupe(session, 1, null);
        assertTrue(view.contains("/user/mes-groupes"));
        verify(groupeService).joinGroupe(u, g);
        verify(session).setAttribute("user", u);
    }

    @Test
    void testQuitterGroupe_Createur() {
        Utilisateur u = new Utilisateur();
        u.setGroupesAppartenance(new ArrayList<>());
        Groupe g = new Groupe();
        g.setCreateur(u);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(g);

        String view = controller.quitterGroupe(session, 1, null);
        assertTrue(view.contains("/user/mes-groupes"));
        verify(groupeService).quitterGroupe(u, 1);
        verify(groupeService).supprimerGroupe(u, g);
        verify(session).setAttribute("user", u);
    }

    @Test
    void testSupprimerGroupe() {
        Utilisateur u = new Utilisateur();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);

        String view = controller.supprimerGroupe(session, 1);
        assertEquals("redirect:/user/mes-groupes", view);
        verify(groupeService).supprimerGroupe(u, 1);
    }
}
