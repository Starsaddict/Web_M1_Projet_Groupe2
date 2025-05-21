package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MainControllerTest {

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private MainController mainController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHomepageRedirectsToLoginIfUserNull() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(null);

        String result = mainController.homepage(model, session);

        assertEquals("redirect:/auth/login", result);
        verifyNoInteractions(model);
    }

    @Test
public void testHomepageReturnsFeedWithSortedPosts() {
    Utilisateur user = new Utilisateur();

    Post post1 = new Post();
    post1.setDatePost(System.currentTimeMillis() - 10000L);
    post1.setGroupe(null);

    Post post2 = new Post();
    post2.setDatePost(System.currentTimeMillis() - 5000L);
    post2.setGroupe(null);

    Post repost1 = new Post();
    repost1.setDatePost(System.currentTimeMillis() - 8000L);
    repost1.setGroupe(null);

    List<Post> posts = new ArrayList<>();
    posts.add(post1);
    posts.add(post2);

    List<Post> reposts = new ArrayList<>();
    reposts.add(repost1);

    user.setPosts(posts);
    user.setPostsRepostes(reposts);

    Utilisateur friend = new Utilisateur();

    Post friendPost = new Post();
    friendPost.setDatePost(System.currentTimeMillis() - 7000L);
    friendPost.setGroupe(null);

    List<Post> friendPosts = new ArrayList<>();
    friendPosts.add(friendPost);

    friend.setPosts(friendPosts);
    friend.setPostsRepostes(new ArrayList<>());

    List<Utilisateur> amis = new ArrayList<>();
    amis.add(friend);
    user.setAmis(amis);

    when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

    String view = mainController.homepage(model, session);

    ArgumentCaptor<List<Post>> captor = ArgumentCaptor.forClass(List.class);
    verify(model).addAttribute(eq("posts"), captor.capture());

    List<Post> resultPosts = captor.getValue();

    assertEquals("feed", view);
    for (int i = 0; i < resultPosts.size() - 1; i++) {
        assert(resultPosts.get(i).getDatePost() >= resultPosts.get(i + 1).getDatePost());
    }
    assert(resultPosts.size() <= 20);
}



    @Test
    public void testRedirectToHome() {
        String redirect = mainController.redirectToHome();
        assertEquals("redirect:/home", redirect);
    }
}
