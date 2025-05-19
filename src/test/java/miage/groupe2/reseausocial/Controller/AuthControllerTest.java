package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpSession;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void authenticate_withValidEmailAndPassword_shouldRedirectToHome() throws Exception {
        String rawPassword = "password123";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU("test@example.com");
        utilisateur.setMdpU(hashedPassword);

        when(utilisateurRepository.findByEmailU("test@example.com")).thenReturn(utilisateur);

        mockMvc.perform(post("/auth/login")
                .param("email", "test@example.com")
                .param("mdp", rawPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void authenticate_withInvalidEmail_shouldReturnLoginFormWithError() throws Exception {
        when(utilisateurRepository.findByEmailU("unknown@example.com")).thenReturn(null);

        mockMvc.perform(post("/auth/login")
                .param("email", "unknown@example.com")
                .param("mdp", "anyPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "email n'exist pas"));
    }

    @Test
    void authenticate_withWrongPassword_shouldRedirectToHomeWithError() throws Exception {
        String rawPassword = "password123";
        String hashedPassword = BCrypt.hashpw("correctPassword", BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU("test@example.com");
        utilisateur.setMdpU(hashedPassword);

        when(utilisateurRepository.findByEmailU("test@example.com")).thenReturn(utilisateur);

        mockMvc.perform(post("/auth/login")
                .param("email", "test@example.com")
                .param("mdp", rawPassword))  // mauvais mdp
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }
}
