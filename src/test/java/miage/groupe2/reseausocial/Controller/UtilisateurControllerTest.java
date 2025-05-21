package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UtilisateurControllerTest {

    UtilisateurController controller;
    UtilisateurRepository utilisateurRepository;
    GroupeRepository groupeRepository;
    GroupeService groupeService;
    UtilisateurService utilisateurService;
    Model model;
    HttpSession session;

    @BeforeEach
    public void setUp() {
        utilisateurRepository = mock(UtilisateurRepository.class);
        groupeRepository = mock(GroupeRepository.class);
        groupeService = mock(GroupeService.class);
        utilisateurService = mock(UtilisateurService.class);
        model = mock(Model.class);
        session = mock(HttpSession.class);

        controller = new UtilisateurController();
        controller.utilisateurRepository = utilisateurRepository;
        controller.groupeRepository = groupeRepository;
        controller.setGroupeService(groupeService);
        controller.setUtilisateurService(utilisateurService);
    }

    @Test
    public void testShowRegisterForm() {
        String view = controller.showRegisterForm(model);
        verify(model).addAttribute(eq("utilisateur"), any(Utilisateur.class));
        assertEquals("form-register", view);
    }

    @Test
    public void testRegisterUser_EmailExists_ReturnFormRegister() {
        Utilisateur user = new Utilisateur();
        user.setEmailU("test@example.com");
        List<String> emails = new ArrayList<>();
        emails.add("test@example.com");
        when(utilisateurRepository.findAllEmailU()).thenReturn(emails);

        String view = controller.registerUser(user, model);

        verify(model).addAttribute("error", "email exist");
        assertEquals("form-register", view);
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    public void testRegisterUser_Success() {
        Utilisateur user = new Utilisateur();
        user.setEmailU("unique@example.com");
        user.setMdpU("password123");
        when(utilisateurRepository.findAllEmailU()).thenReturn(new ArrayList<>());

        String view = controller.registerUser(user, model);

        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(captor.capture());
        Utilisateur savedUser = captor.getValue();
        assertTrue(BCrypt.checkpw("password123", savedUser.getMdpU()));
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    public void testRechercherUtilisateurs() {
        Utilisateur sessionUser = new Utilisateur();
        sessionUser.setIdUti(1);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(sessionUser);

        Utilisateur foundUser = new Utilisateur();
        foundUser.setIdUti(2);
        List<Utilisateur> results = new ArrayList<>();
        results.add(foundUser);
        results.add(sessionUser); // Should be removed
        when(utilisateurRepository.findByNomUContainingIgnoreCase("nom")).thenReturn(results);

        String view = controller.rechercherUtilisateurs("nom", model, session);

        verify(model).addAttribute(eq("utilisateurs"), argThat(list -> ((List<?>)list).size() == 1));
        assertEquals("search_results", view);
    }

    @Test
    public void testVoirMesAmis() {
        Utilisateur user = new Utilisateur();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String view = controller.voirMesAmis(session, model);

        verify(model).addAttribute("amis", user.getAmis());
        assertEquals("listeamis", view);
    }

    @Test
    public void testSupprimerAmi_AmiExists() {
        Utilisateur user = new Utilisateur();
        user.setAmis(new ArrayList<>());
        Utilisateur ami = new Utilisateur();
        ami.setAmis(new ArrayList<>()); 

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(utilisateurRepository.findByIdUti(5)).thenReturn(ami);

        user.getAmis().add(ami);
        ami.getAmis().add(user);

        RedirectAttributes redirectAttributes = mock(org.springframework.web.servlet.mvc.support.RedirectAttributes.class);

        String referer = "someReferer";

        String view = controller.supprimerAmi(5, session, redirectAttributes, referer);

        assertTrue(view.contains("redirect:"));
    }


    @Test
    public void testModifierProfil() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String view = controller.modifierProfil("pseudo", "email@example.com", "intro", session);

        assertEquals("redirect:/user/1/profil", view);
        assertEquals("pseudo", user.getPseudoU());
        assertEquals("email@example.com", user.getEmailU());
        assertEquals("intro", user.getIntroductionU());
        verify(utilisateurRepository).save(user);
        verify(session).setAttribute("user", user);
    }

    @Test
    public void testModifierPassword_Success() {
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        String hashedOldPass = BCrypt.hashpw(oldPassword, BCrypt.gensalt());

        Utilisateur user = new Utilisateur();
        user.setMdpU(hashedOldPass);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String view = controller.modifierPassword(oldPassword, newPassword, session, "/referer");

        assertTrue(BCrypt.checkpw(newPassword, user.getMdpU()));
        assertEquals("redirect:/user/modifierProfil", view);
        verify(utilisateurRepository).save(user);
        verify(session).setAttribute("user", user);
    }

    @Test
    public void testModifierPassword_WrongCurrentPassword() {
        String oldPassword = "wrongOld";
        String newPassword = "newPass";

        String hashedOldPass = BCrypt.hashpw("correctOld", BCrypt.gensalt());

        Utilisateur user = new Utilisateur();
        user.setMdpU(hashedOldPass);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String view = controller.modifierPassword(oldPassword, newPassword, session, "/referer");

        assertFalse(BCrypt.checkpw(newPassword, user.getMdpU())); // password should not have changed
        assertEquals("redirect:/user/modifierProfil", view);
        verify(utilisateurRepository, never()).save(user);
    }


}
