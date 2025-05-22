package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.CommentaireRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.ReactionRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentaireRepository commentaireRepository;
    @Mock private ReactionRepository reactionRepository;
    @Mock private UtilisateurService utilisateurService;
    @Mock private GroupeService groupeService;
    @Mock private PostService postService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreerPostSansGroupe() throws IOException {
        Post post = new Post();
        Utilisateur user = new Utilisateur(); user.setIdUti(1);
        MockMultipartFile image = new MockMultipartFile("image", "img.png", "image/png", "test".getBytes());
        MockHttpSession session = new MockHttpSession();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String result = postController.creerPost(post, null, image, session, null);

        verify(postService).publierPostSansGroupe(post, user);
        assertEquals("redirect:/user/1/profil", result);
    }

    @Test
    public void testCreerPostAvecGroupe() throws IOException {
        Post post = new Post();
        Utilisateur user = new Utilisateur(); user.setIdUti(1);
        Groupe groupe = new Groupe(); groupe.setIdGrp(2);
        MockHttpSession session = new MockHttpSession();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeService.getGroupeByidGrp(2)).thenReturn(groupe);

        String result = postController.creerPost(post, 2, null, session, null);

        verify(postService).publierPostDansGroupe(post, user, groupe);
        assertEquals("redirect:redirect:/groupe/2", result);
    }

    @Test
    public void testModifierPost() throws IOException {
        Post post = new Post(); post.setIdPost(10);
        when(postRepository.findByIdPost(10)).thenReturn(post);

        MockMultipartFile image = new MockMultipartFile("img", "file.png", "image/png", "123".getBytes());

        String result = postController.modifierPost("Titre", "Texte", image, 10, null, null);

        assertEquals("redirect:/home", result);
        verify(postRepository).save(post);
    }

    @Test
    public void testSupprimerPost() {
        Utilisateur user = new Utilisateur(); user.setIdUti(5);
        Post post = new Post(); post.setIdPost(12);
        Utilisateur createur = new Utilisateur(); createur.setIdUti(5);
        post.setCreateur(createur);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        when(postRepository.findByIdPost(12)).thenReturn(post);

        String result = postController.supprimerPost(12, session, null);

        verify(postRepository).delete(post);
        assertEquals("redirect:/user/5/profil", result);
    }

    @Test
    public void testRepost() {
        Utilisateur user = new Utilisateur(); user.setIdUti(1);
        Post post = new Post(); post.setIdPost(5);
        user.setPostsRepostes(new ArrayList<>());

        MockHttpSession session = new MockHttpSession();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postService.findPostById(5)).thenReturn(post);

        String result = postController.repostPost(5, session, null, null);

        verify(utilisateurRepository).save(user);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testAnnulerRepost() {
        Utilisateur user = new Utilisateur(); user.setIdUti(1);
        Post post = new Post(); post.setIdPost(5);
        user.setPostsRepostes(new ArrayList<>(List.of(post)));

        MockHttpSession session = new MockHttpSession();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postService.findPostById(5)).thenReturn(post);

        String result = postController.repostAnnuler(5, session, null, null);

        verify(utilisateurRepository).save(user);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testAjouterCommentaire() {
        Utilisateur user = new Utilisateur(); user.setIdUti(1);
        Post post = new Post(); post.setIdPost(2);
        Commentaire commentaire = new Commentaire();

        MockHttpSession session = new MockHttpSession();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postRepository.findById(2)).thenReturn(Optional.of(post));

        String result = postController.ajouterCommentaire(commentaire, 2, session, null);

        verify(commentaireRepository).save(commentaire);
        assertEquals("redirect:/home", result);
    }

    @Test
    public void testAjouterReaction() {
        Utilisateur user = new Utilisateur(); user.setIdUti(1);
        Post post = new Post(); post.setIdPost(2);

        MockHttpSession session = new MockHttpSession();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(postRepository.findByIdPost(2)).thenReturn(post);

        String result = postController.ajouterReaction(2, "😊", session, null);

        verify(reactionRepository).deleteByPostAndUtilisateur(post, user);
        verify(reactionRepository).save(any(Reaction.class));
        assertEquals("redirect:/home", result);
    }
}
