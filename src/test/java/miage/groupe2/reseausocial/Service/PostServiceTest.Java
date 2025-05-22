package miage.groupe2.reseausocial.Service;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    private PostRepository postRepository;
    private UtilisateurRepository utilisateurRepository;
    private GroupeRepository groupeRepository;
    private UtilisateurService utilisateurService;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        groupeRepository = mock(GroupeRepository.class);
        utilisateurService = mock(UtilisateurService.class);
        postService = new PostService(postRepository, utilisateurRepository, groupeRepository, utilisateurService);
    }

    @Test
    void testListPostFriends() {
        HttpSession session = mock(HttpSession.class);

        Utilisateur user = new Utilisateur();
        Utilisateur ami = new Utilisateur();
        user.setAmis(List.of(ami));

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Post post1 = new Post();
        post1.setCreateur(ami);
        post1.setDatePost(100L);
        post1.setGroupe(null);

        Post post2 = new Post();
        post2.setCreateur(ami);
        post2.setDatePost(200L);
        post2.setGroupe(null);

        Post postAvecGroupe = new Post();
        postAvecGroupe.setCreateur(ami);
        postAvecGroupe.setDatePost(150L);
        postAvecGroupe.setGroupe(new Groupe());

        when(postRepository.findByCreateur(ami)).thenReturn(List.of(post1, post2, postAvecGroupe));

        List<Post> result = postService.listPostFriends(session);

        assertEquals(2, result.size());
        assertEquals(post2, result.get(0));
        assertEquals(post1, result.get(1));
    }

    @Test
    void testPublierPostDansGroupe() {
        Post post = new Post();
        Utilisateur auteur = new Utilisateur();
        auteur.setPosts(new java.util.ArrayList<>());
        Groupe groupe = new Groupe();
        groupe.setPosts(new java.util.ArrayList<>());

        postService.publierPostDansGroupe(post, auteur, groupe);

        assertEquals(auteur, post.getCreateur());
        assertEquals(groupe, post.getGroupe());
        assertTrue(auteur.getPosts().contains(post));
        assertTrue(groupe.getPosts().contains(post));
        assertTrue(post.getDatePost() > 0);

        verify(postRepository).save(post);
        verify(utilisateurRepository).save(auteur);
        verify(groupeRepository).save(groupe);
    }

    @Test
    void testPublierPostSansGroupe() {
        Post post = new Post();
        Utilisateur user = new Utilisateur();
        user.setPosts(new java.util.ArrayList<>());

        postService.publierPostSansGroupe(post, user);

        assertEquals(user, post.getCreateur());
        assertNull(post.getGroupe());
        assertTrue(user.getPosts().contains(post));
        assertTrue(post.getDatePost() > 0);

        verify(postRepository).save(post);
        verify(utilisateurRepository).save(user);
    }

    @Test
    void testFindPostById() {
        Post post = new Post();
        when(postRepository.findByIdPost(42)).thenReturn(post);

        Post result = postService.findPostById(42);
        assertSame(post, result);

        verify(postRepository).findByIdPost(42);
    }
}
