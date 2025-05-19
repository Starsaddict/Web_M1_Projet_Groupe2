package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilisateurControllerTest {

    @InjectMocks
    UtilisateurController utilisateurController;

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Setup utilisateur avec email existant
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU("existant@email.com");
        utilisateur.setMdpU("password");

        when(utilisateurRepository.findAllEmailU()).thenReturn(Arrays.asList("existant@email.com"));

        String view = utilisateurController.registerUser(utilisateur, model);

        assertEquals("form-register", view);
        verify(model).addAttribute(eq("error"), eq("email exist"));
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_SuccessfulRegistration() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailU("nouveau@email.com");
        utilisateur.setMdpU("password");

        when(utilisateurRepository.findAllEmailU()).thenReturn(Collections.emptyList());

        String view = utilisateurController.registerUser(utilisateur, model);

        assertEquals("redirect:/auth/login", view);

        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(captor.capture());

        Utilisateur savedUser = captor.getValue();
        assertNotEquals("password", savedUser.getMdpU());
        assertTrue(savedUser.getMdpU().startsWith("$2a$")); // Format BCrypt hash
    }
}
