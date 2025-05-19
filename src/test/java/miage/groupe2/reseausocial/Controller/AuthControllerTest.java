package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.mindrot.jbcrypt.BCrypt;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @Test
    void testLoginSuccess() throws Exception {
        String email = "test@example.com";
        String rawPassword = "password123";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUti(42);
        utilisateur.setEmailU(email);
        utilisateur.setMdpU(hashedPassword);

        Mockito.when(utilisateurRepository.findByEmailU(email)).thenReturn(utilisateur);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .param("email", email)
                .param("mdp", rawPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/42"));
    }

    @Test
    void testLoginFailWrongPassword() throws Exception {
        String email = "test@example.com";
        String rawPassword = "wrongpass";
        String hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU(email);
        utilisateur.setMdpU(hashedPassword);

        Mockito.when(utilisateurRepository.findByEmailU(email)).thenReturn(utilisateur);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .param("email", email)
                .param("mdp", rawPassword))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "wrong password"))
                .andExpect(view().name("form-login"));
    }

    @Test
    void testLoginFailEmailNotFound() throws Exception {
        String email = "unknown@example.com";

        Mockito.when(utilisateurRepository.findByEmailU(email)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .param("email", email)
                .param("mdp", "anything"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "email n'exist pas"))
                .andExpect(view().name("form-login"));
    }
}
