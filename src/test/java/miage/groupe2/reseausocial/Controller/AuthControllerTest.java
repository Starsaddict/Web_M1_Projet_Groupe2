package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showLoginForm_shouldReturnFormLogin() {
        String result = authController.showLoginForm();
        assertEquals("form-login", result);
    }

    @Test
    void authenticate_withValidUserAndPassword_shouldRedirectHome() {
        String email = "test@example.com";
        String rawPassword = "password";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU(email);
        utilisateur.setMdpU(hashedPassword);

        when(utilisateurRepository.findByEmailU(email)).thenReturn(utilisateur);

        String result = authController.authenticate(email, rawPassword, model, session);

        verify(session).setAttribute("user", utilisateur);
        assertEquals("redirect:/home", result);
    }

    @Test
    void authenticate_withInvalidPassword_shouldReturnFormLoginWithError() {
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String hashedPassword = BCrypt.hashpw("correctPassword", BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU(email);
        utilisateur.setMdpU(hashedPassword);

        when(utilisateurRepository.findByEmailU(email)).thenReturn(utilisateur);

        String result = authController.authenticate(email, rawPassword, model, session);

        verify(model).addAttribute("error", "Mot de passe incorrect");
        assertEquals("form-login", result);
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void authenticate_withNonExistingUser_shouldReturnFormLoginWithError() {
        String email = "nonexistent@example.com";
        String rawPassword = "password";

        when(utilisateurRepository.findByEmailU(email)).thenReturn(null);

        String result = authController.authenticate(email, rawPassword, model, session);

        verify(model).addAttribute("error", "Mot de passe incorrect");
        assertEquals("form-login", result);
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void logout_shouldInvalidateSessionAndReturnFormLogin() {
        String result = authController.logout(session);

        verify(session).invalidate();
        assertEquals("form-login", result);
    }
}
