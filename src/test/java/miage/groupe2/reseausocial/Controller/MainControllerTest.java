package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.service.DemandeAmiService;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class MainControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private DemandeAmiService demandeAmiService;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private MainController mainController;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        openMocks(this);
        utilisateur = new Utilisateur();
        utilisateur.setIdUti(1);
        utilisateur.setNomU("Test");
    }

    @Test
    void homepage_redirectsToLogin_whenUserIsNull() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(null);

        String view = mainController.homepage(model, session);

        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void homepage_returnsFeedView_whenUserExists() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateur);
        List<Post> mockPosts = List.of(new Post(), new Post(), new Post());
        when(postService.listPostFriends(session)).thenReturn(mockPosts);
        List<DemandeAmi> mockDemandes = List.of(new DemandeAmi());
        when(demandeAmiService.getDemandeMessages(utilisateur)).thenReturn(mockDemandes);

        String view = mainController.homepage(model, session);

        verify(model).addAttribute(eq("posts"), anyList());
        assertEquals("feed", view);
    }

    @Test
    void redirectToHome_returnsRedirect() {
        String result = mainController.redirectToHome();
        assertEquals("redirect:/home", result);
    }
}
