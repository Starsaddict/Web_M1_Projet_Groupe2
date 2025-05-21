package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.*;
import miage.groupe2.reseausocial.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @InjectMocks
    PostController postController;

    @Mock
    UtilisateurService utilisateurService;

    @Mock
    GroupeService groupeService;

    @Mock
    PostService postService;

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentaireRepository commentaireRepository;

    @Mock
    ReactionRepository reactionRepository;

    @Mock
    HttpSession session;

    @Mock
    MultipartFile imageFile;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreerPostAvecGroupe() throws IOException {
        Post post = new Post();
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeService.getGroupeByidGrp(1)).thenReturn(groupe);
        when(imageFile.isEmpty()).thenReturn(false);
        when(imageFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        String result = postController.creerPost(post, 1, imageFile, session, null);

        verify(postService).publierPostDansGroupe(post, user, groupe);
        assertTrue(result.contains("redirect:/groupe/1"));
    }

    @Test
    void testCreerPostSansGroupe() throws IOException {
        Post post = new Post();
        Utilisateur user = new Utilisateur();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        // Pas besoin de when(imageFile)...

        String result = postController.creerPost(post, null, null, session, null);

        verify(postService).publierPostSansGroupe(post, user);
        assertTrue(result.contains("/user/" + user.getIdUti() + "/profil"));
    }


    @Test
    void testModifierPost() throws IOException {
        Post post = new Post();
        post.setTitrePost("old");
        post.setTextePost("old text");

        when(postRepository.findByIdPost(1)).thenReturn(post);
        when(imageFile.isEmpty()).thenReturn(true);

        String result = postController.modifierPost("new titre", "new texte", imageFile, 1, false, null);

        assertEquals("new titre", post.getTitrePost());
        assertEquals("new texte", post.getTextePost());
        verify(postRepository).save(post);
        assertTrue(result.contains(PostController.HOME_PAGE));
    }

    @Test
    void testSupprimerPost() {
        Post post = new Post();
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        post.setCreateur(user);

        when(postRepository.findByIdPost(1)).thenReturn(post);
        when(session.getAttribute("user")).thenReturn(user);

        String result = postController.supprimerPost(1, session, null);

        verify(postRepository).delete(post);
        assertTrue(result.contains("/user/" + user.getIdUti() + "/profil"));
    }

    @Test
    void testRepostPostAjout() {
        Utilisateur user = new Utilisateur();
        user.setPostsRepostes(new ArrayList<>());

        Post post = new Post();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postService.findPostById(1)).thenReturn(post);

        String result = postController.repostPost(1, session, null, null);

        assertTrue(user.getPostsRepostes().contains(post));
        verify(utilisateurRepository).save(user);
        assertTrue(result.contains(PostController.HOME_PAGE));
    }

    @Test
    void testRepostPostAnnuler() {
        Post post = new Post();
        Utilisateur user = new Utilisateur();
        List<Post> repostes = new ArrayList<>();
        repostes.add(post);
        user.setPostsRepostes(repostes);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postService.findPostById(1)).thenReturn(post);

        String result = postController.repostAnnuler(1, session, null, null);

        assertFalse(user.getPostsRepostes().contains(post));
        verify(utilisateurRepository).save(user);
        assertTrue(result.contains(PostController.HOME_PAGE));
    }

    @Test
    void testAjouterCommentaire() {
        Utilisateur user = new Utilisateur();
        Post post = new Post();
        Commentaire commentaire = new Commentaire();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        String result = postController.ajouterCommentaire(commentaire, 1, session, null);

        assertEquals(user, commentaire.getUtilisateur());
        assertEquals(post, commentaire.getPost());
        verify(commentaireRepository).save(commentaire);
        assertTrue(result.contains(PostController.HOME_PAGE));
    }

    @Test
    void testAjouterReaction() {
        Utilisateur user = new Utilisateur();
        Post post = new Post();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postRepository.findByIdPost(1)).thenReturn(post);

        String result = postController.ajouterReaction(1, "like", session, null);

        verify(reactionRepository).deleteByPostAndUtilisateur(post, user);
        verify(reactionRepository).save(any());
        assertTrue(result.contains(PostController.HOME_PAGE));
    }
}
