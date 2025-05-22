package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.ImageUtil;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UtilisateurControllerTest {

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    GroupeRepository groupeRepository;

    @Mock
    GroupeService groupeService;

    @Mock
    UtilisateurService utilisateurService;

    @Mock
    Model model;

    @Mock
    HttpSession session;

    @Mock
    MultipartFile multipartFile;

    @InjectMocks
    UtilisateurController controller;

    Utilisateur user;
    Utilisateur friend;
    Groupe groupe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Utilisateur();
        user.setIdUti(1);
        user.setMdpU(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setAmis(new ArrayList<>());
        user.setPosts(new ArrayList<>());
        user.setPostsRepostes(new ArrayList<>());
        user.setDemandesEnvoyees(new ArrayList<>());
        user.setGroupesAppartenance(new ArrayList<>());

        friend = new Utilisateur();
        friend.setIdUti(2);
        friend.setAmis(new ArrayList<>());

        groupe = new Groupe();
        groupe.setIdGrp(10);
        groupe.setCreateur(user);
    }

    @Test
    void showRegisterForm_shouldAddNewUtilisateurAndReturnForm() {
        String view = controller.showRegisterForm(model);
        verify(model).addAttribute(eq("utilisateur"), any(Utilisateur.class));
        assertEquals("form-register", view);
    }

    @Test
    void registerUser_whenEmailExists_shouldReturnFormWithError() {
        when(utilisateurRepository.findAllEmailU()).thenReturn(List.of("test@example.com"));
        Utilisateur u = new Utilisateur();
        u.setEmailU("test@example.com");
        String view = controller.registerUser(u, model);
        verify(model).addAttribute("error", "email exist");
        assertEquals("form-register", view);
    }

    @Test
    void registerUser_whenEmailNotExists_shouldSaveAndRedirect() {
        when(utilisateurRepository.findAllEmailU()).thenReturn(Collections.emptyList());
        Utilisateur u = new Utilisateur();
        u.setEmailU("new@example.com");
        u.setMdpU("pwd");
        String view = controller.registerUser(u, model);
        verify(utilisateurRepository).save(any(Utilisateur.class));
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void rechercherUtilisateurs_shouldExcludeCurrentUser() {
        Utilisateur currentUser = new Utilisateur();
        currentUser.setIdUti(1);
        currentUser.setAmis(new ArrayList<>());
        currentUser.setDemandesEnvoyees(new ArrayList<>());

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(currentUser);

        Utilisateur foundUser1 = new Utilisateur();
        foundUser1.setIdUti(1);
        foundUser1.setAmis(new ArrayList<>());
        foundUser1.setDemandesEnvoyees(new ArrayList<>());

        Utilisateur foundUser2 = new Utilisateur();
        foundUser2.setIdUti(2);
        foundUser2.setAmis(new ArrayList<>());
        foundUser2.setDemandesEnvoyees(new ArrayList<>());

        when(utilisateurRepository.findAll()).thenReturn(new ArrayList<>(List.of(foundUser1, foundUser2)));

        String view = controller.rechercherUtilisateurs("nom", model, session);

        verify(model).addAttribute(eq("recommande"), argThat(list -> ((List<?>)list).size() == 1));
        assertEquals("results", view);
    }

    @Test
    void voirMesAmis_shouldAddAmisToModel() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        user.getAmis().add(friend);
        String view = controller.voirMesAmis(session, model);
        assertEquals("friends", view);
    }

    @Test
    void supprimerAmi_shouldRemoveFriendAndSave() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(utilisateurRepository.findByIdUti(2)).thenReturn(friend);
        user.getAmis().add(friend);
        friend.getAmis().add(user);

        String referer = "someurl";
        

        String view = controller.supprimerAmi(2, session, mock(RedirectAttributes.class), referer);
        assertNotNull(view);
        verify(utilisateurRepository).save(user);
        verify(utilisateurRepository).save(friend);
    }

    @Test
    void userProfil_shouldAddAttributesAndReturnProfil() {
        when(utilisateurRepository.findByIdUti(1)).thenReturn(user);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        user.getAmis().add(friend);
        Post post = new Post();
        post.setDatePost(System.currentTimeMillis());
        post.setGroupe(null);
        user.getPosts().add(post);
        user.getPostsRepostes().add(post);

        String view = controller.userProfil("post", 1, model, session);
        verify(model).addAttribute(eq("user"), eq(user));
        verify(model).addAttribute(eq("Friends"), any());
        verify(model).addAttribute(eq("posts"), any());
        assertEquals("profil_user", view);
    }

    @Test
    void modifierProfil_get_shouldReturnSetting() {
        assertEquals("setting", controller.modifierProfil());
    }

    @Test
    void modifierProfil_post_shouldUpdateUserAndRedirect() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        String view = controller.modifierProfil("pseudo", "email@test.com", "intro", session);
        verify(utilisateurRepository).save(user);
        verify(session).setAttribute("user", user);
        assertEquals("redirect:/user/" + user.getIdUti() + "/profil", view);
    }

    @Test
    void modifierPassword_whenCurrentPasswordCorrect_shouldUpdatePassword() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        String newPassword = "newPwd";
        String currentPassword = "password";

        String view = controller.modifierPassword(currentPassword, newPassword, session, null);
        verify(utilisateurRepository).save(user);
        verify(session).setAttribute("user", user);
        assertTrue(BCrypt.checkpw(newPassword, user.getMdpU()));
        assertEquals("redirect:/user/modifierProfil", view);
    }

    @Test
    void modifierPassword_whenCurrentPasswordIncorrect_shouldNotUpdate() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        String newPassword = "newPwd";
        String currentPassword = "wrongPwd";

        String view = controller.modifierPassword(currentPassword, newPassword, session, null);
        verify(utilisateurRepository, never()).save(user);
        assertEquals("redirect:/user/modifierProfil", view);
    }

    @Test
    void joinGroupe_shouldCallServiceAndRedirect() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findGroupeByidGrp(10)).thenReturn(groupe);
        String referer = "refurl";
        String view = controller.joinGroupe(session, 10, referer);
        verify(groupeService).joinGroupe(user, groupe);
        verify(session).setAttribute("user", user);
        assertEquals("redirect:/user/mes-groupes", view);
    }

    @Test
    void quitterGroupe_whenUserIsCreator_shouldDeleteGroup() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findGroupeByidGrp(10)).thenReturn(groupe);
        String referer = "refurl";
        String view = controller.quitterGroupe(session, 10, referer);
        verify(groupeService).quitterGroupe(user, 10);
        verify(groupeService).supprimerGroupe(user, groupe);
        verify(session).setAttribute("user", user);
        assertEquals("redirect:/user/mes-groupes", view);
    }

    @Test
    void quitterGroupe_whenUserNotCreator_shouldNotDeleteGroup() {
        Utilisateur anotherUser = new Utilisateur();
        anotherUser.setIdUti(99);
        groupe.setCreateur(anotherUser);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findGroupeByidGrp(10)).thenReturn(groupe);
        String referer = "refurl";
        String view = controller.quitterGroupe(session, 10, referer);
        verify(groupeService).quitterGroupe(user, 10);
        verify(groupeService, never()).supprimerGroupe(any(), any());
        verify(session).setAttribute("user", user);
        assertEquals("redirect:/user/mes-groupes", view);
    }

    @Test
    void supprimerGroupe_shouldCallServiceAndRedirect() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        String view = controller.supprimerGroupe(session, 10);
        verify(groupeService).supprimerGroupe(user, 10);
        assertEquals("redirect:/user/mes-groupes", view);
    }

    @Test
    void uploadAvatar_whenFileEmpty_shouldNotSave() throws IOException {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(multipartFile.isEmpty()).thenReturn(true);

        String referer = "refurl";
        String view = controller.uploadAvatar(multipartFile, session, referer);
        verify(utilisateurRepository, never()).save(user);
        verify(session).setAttribute("user", user);
        assertEquals("redirect:/user/1", view);
    }

    private void mockStatic(Class<?> clazz) {
    }

    private void clearAllCaches() {
    }
}
