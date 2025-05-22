package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.ui.Model;
import org.springframework.ui.ConcurrentModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;


public class AuthControllerTest {

    private AuthController authController;
    private UtilisateurRepository utilisateurRepository;
    private HttpSession session;
    private Model model;


@BeforeEach
public void setUp() throws Exception {
    utilisateurRepository = mock(UtilisateurRepository.class);
    session = mock(HttpSession.class);
    model = new ConcurrentModel();
    authController = new AuthController();

    Field field = AuthController.class.getDeclaredField("utilisateurRepository");
    field.setAccessible(true);
    field.set(authController, utilisateurRepository);
}

    @Test
    public void testShowLoginForm() {
        String result = authController.showLoginForm();
        assertEquals("form-login", result);
    }

    @Test
    public void testAuthenticateSuccess() {
        String email = "test@example.com";
        String password = "password";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU(email);
        utilisateur.setMdpU(hashedPassword);

        when(utilisateurRepository.findByEmailU(email)).thenReturn(utilisateur);

        String result = authController.authenticate(email, password, model, session);
        assertEquals("redirect:/home", result);
        verify(session).setAttribute("user", utilisateur);
    }

    @Test
    public void testAuthenticateFailure() {
        String email = "test@example.com";
        String password = "wrongpassword";

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU(email);
        utilisateur.setMdpU(BCrypt.hashpw("password", BCrypt.gensalt()));

        when(utilisateurRepository.findByEmailU(email)).thenReturn(utilisateur);

        String result = authController.authenticate(email, password, model, session);
        assertEquals("form-login", result);
    }

    @Test
    public void testAuthenticateUnknownUser() {
        when(utilisateurRepository.findByEmailU("unknown@example.com")).thenReturn(null);
        String result = authController.authenticate("unknown@example.com", "password", model, session);
        assertEquals("form-login", result);
    }

    @Test
    public void testLogout() {
        String result = authController.logout(session);
        assertEquals("form-login", result);
        verify(session).invalidate();
    }
}
