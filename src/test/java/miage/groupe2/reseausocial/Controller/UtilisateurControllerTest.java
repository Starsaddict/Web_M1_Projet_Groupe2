package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UtilisateurController.class)
@ContextConfiguration(classes = UtilisateurController.class)
public class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UtilisateurRepository utilisateurRepository;

    @Test
    public void testShowRegisterForm() throws Exception {
        mockMvc.perform(get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-register"))
                .andExpect(model().attributeExists("utilisateur"));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        Mockito.when(utilisateurRepository.findAllEmailU())
                .thenReturn(List.of("test@example.com"));

        mockMvc.perform(post("/user/register")
                        .param("emailU", "test@example.com")
                        .param("mdpU", "password123")
                        .param("nomU", "Test")
                        .param("prenomU", "User"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testRegisterUser_NewUser() throws Exception {
        Mockito.when(utilisateurRepository.findAllEmailU())
                .thenReturn(List.of());

        mockMvc.perform(post("/user/register")
                        .param("emailU", "newuser@example.com")
                        .param("mdpU", "password123")
                        .param("nomU", "New")
                        .param("prenomU", "User")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}
