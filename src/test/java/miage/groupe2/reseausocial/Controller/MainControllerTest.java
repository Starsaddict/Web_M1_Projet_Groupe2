package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.DemandeAmiService;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MainControllerTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void homepage_userNull_redirectsToLogin() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(null);

        String result = mainController.homepage(model, session);

        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void homepage_userWithPostsAndFriends_feedViewWithPosts() {
    Utilisateur user = mock(Utilisateur.class);
    when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

    List<Post> userPosts = new ArrayList<>();
    List<Post> userPostsRepostes = new ArrayList<>();
    List<Utilisateur> friends = new ArrayList<>();

    Post post1 = mock(Post.class);
    Post postRepost = mock(Post.class);
    Post friendPost = mock(Post.class);
    Post friendRepost = mock(Post.class);

    userPosts.add(post1);
    userPostsRepostes.add(postRepost);

    Utilisateur friend = mock(Utilisateur.class);
    List<Post> friendPosts = new ArrayList<>();
    List<Post> friendPostsRepostes = new ArrayList<>();

    friendPosts.add(friendPost);
    friendPostsRepostes.add(friendRepost);

    friends.add(friend);

    when(user.getPosts()).thenReturn(userPosts);
    when(user.getPostsRepostes()).thenReturn(userPostsRepostes);
    when(user.getAmis()).thenReturn(friends);

    when(friend.getPosts()).thenReturn(friendPosts);
    when(friend.getPostsRepostes()).thenReturn(friendPostsRepostes);

    when(post1.getGroupe()).thenReturn(null);
    when(postRepost.getGroupe()).thenReturn(null);
    when(friendPost.getGroupe()).thenReturn(null);
    when(friendRepost.getGroupe()).thenReturn(null);

    Date now = new Date();
    when(post1.getDatePost()).thenReturn(now.getTime() - 1000);
    when(postRepost.getDatePost()).thenReturn(now.getTime() - 5000);
    when(friendPost.getDatePost()).thenReturn(now.getTime() - 2000);
    when(friendRepost.getDatePost()).thenReturn(now.getTime() - 3000);

    try (MockedStatic<Hibernate> mockedHibernate = mockStatic(Hibernate.class)) {
        mockedHibernate.when(() -> Hibernate.initialize(userPostsRepostes)).thenAnswer(invocation -> null);

        String view = mainController.homepage(model, session);

        ArgumentCaptor<List<Post>> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("posts"), captor.capture());

        List<Post> postsSentToModel = captor.getValue();
        assertEquals(4, postsSentToModel.size());
        assertEquals("feed", view);
    }
    }

    @Test
    void redirectToHome_shouldReturnRedirectHome() {
        String result = mainController.redirectToHome();
        assertEquals("redirect:/home", result);
    }
}
