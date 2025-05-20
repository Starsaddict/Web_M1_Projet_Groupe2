package miage.groupe2.reseausocial.Service;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private GroupeRepository groupeRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublierPostDansGroupe() {
        Utilisateur auteur = new Utilisateur();
        auteur.setPosts(new ArrayList<>());
        Groupe groupe = new Groupe();
        groupe.setPosts(new ArrayList<>());
        Post post = new Post();

        postService.publierPostDansGroupe(post, auteur, groupe);

        assertNotNull(post.getDatePost());
        assertEquals(auteur, post.getCreateur());
        assertEquals(groupe, post.getGroupe());
        assertTrue(auteur.getPosts().contains(post));
        assertTrue(groupe.getPosts().contains(post));
        verify(postRepository).save(post);
        verify(utilisateurRepository).save(auteur);
        verify(groupeRepository).save(groupe);
    }

    @Test
    void testPublierPostSansGroupe() {
        Utilisateur user = new Utilisateur();
        user.setPosts(new ArrayList<>());
        Post post = new Post();

        postService.publierPostSansGroupe(post, user);

        assertNotNull(post.getDatePost());
        assertEquals(user, post.getCreateur());
        assertNull(post.getGroupe());
        assertTrue(user.getPosts().contains(post));
        verify(postRepository).save(post);
        verify(utilisateurRepository).save(user);
    }

    @Test
    void testListPostFriends_UserNull() {
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(null);
        List<Post> result = postService.listPostFriends(session);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testListPostFriends() {
        Utilisateur user = new Utilisateur();
        Utilisateur ami1 = new Utilisateur();
        Utilisateur ami2 = new Utilisateur();
        user.setAmis(Arrays.asList(ami1, ami2));
        Post post1 = new Post();
        post1.setDatePost(1000L);
        post1.setGroupe(null);
        Post post2 = new Post();
        post2.setDatePost(2000L);
        post2.setGroupe(null);
        Post post3 = new Post();
        post3.setDatePost(1500L);
        post3.setGroupe(new Groupe());
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postRepository.findByCreateur(ami1)).thenReturn(List.of(post1, post3));
        when(postRepository.findByCreateur(ami2)).thenReturn(List.of(post2));

        List<Post> result = postService.listPostFriends(session);

        assertEquals(2, result.size());
        assertEquals(post2, result.get(0));
        assertEquals(post1, result.get(1));
    }

    @Test
    void testFindPostById() {
        Post post = new Post();
        when(postRepository.findByIdPost(1)).thenReturn(post);
        Post result = postService.findPostById(1);
        assertEquals(post, result);
    }
}
