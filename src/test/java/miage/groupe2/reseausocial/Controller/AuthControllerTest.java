package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpSession;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mindrot.jbcrypt.BCrypt.*;

@ExtendWith(SpringExtension.class)
public class AuthControllerTest {

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
    void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-login"));
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        // Préparation
        Utilisateur user = new Utilisateur();
        user.setEmailU("test@example.com");
        user.setMdpU(hashpw("password123", gensalt()));

        when(utilisateurRepository.findByEmailU("test@example.com")).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .param("email", "test@example.com")
                        .param("mdp", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(utilisateurRepository).findByEmailU("test@example.com");
    }

    @Test
    void testAuthenticateFail() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setEmailU("wrong@example.com");
        user.setMdpU(hashpw("correctpassword", gensalt()));

        when(utilisateurRepository.findByEmailU("wrong@example.com")).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .param("email", "wrong@example.com")
                        .param("mdp", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(utilisateurRepository).findByEmailU("wrong@example.com");
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(get("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-login"));
    }
}
