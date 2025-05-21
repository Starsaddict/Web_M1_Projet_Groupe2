package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.service.DemandeAmiService;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
        utilisateur.setNomU("TestUser");
        utilisateur.setPosts(new ArrayList<>());
        utilisateur.setPostsRepostes(new ArrayList<>());
        utilisateur.setAmis(new ArrayList<>());
    }

    @Test
    void homepage_redirectsToLogin_whenUserIsNull() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(null);
        String view = mainController.homepage(model, session);
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void homepage_returnsFeedView_withAggregatedPosts() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(utilisateur);

        Post post1 = new Post();
        post1.setDatePost(LocalDateTime.now().minusDays(1));
        post1.setGroupe(null);

        Post post2 = new Post();
        post2.setDatePost(LocalDateTime.now().minusDays(2));
        post2.setGroupe(null);

        Post repost = new Post();
        repost.setDatePost(LocalDateTime.now().minusDays(3));
        repost.setGroupe(null);

        utilisateur.getPosts().add(post1);
        utilisateur.getPostsRepostes().add(repost);

        Utilisateur ami = new Utilisateur();
        ami.setIdUti(2);
        ami.setNomU("Ami");
        ami.setPosts(new ArrayList<>());
        ami.setPostsRepostes(new ArrayList<>());
        ami.getPosts().add(post2);
        ami.getPostsRepostes().add(new Post() {{
            setDatePost(LocalDateTime.now().minusDays(4));
            setGroupe(null);
        }});

        utilisateur.getAmis().add(ami);

        doNothing().when(Hibernate.class);
        Hibernate.initialize(utilisateur.getPostsRepostes());

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
