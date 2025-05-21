package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.*;
import miage.groupe2.reseausocial.service.*;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostControllerTest {

    PostController controller;

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentaireRepository commentaireRepository;

    @Mock
    UtilisateurService utilisateurService;

    @Mock
    GroupeService groupeService;

    @Mock
    PostService postService;

    @Mock
    ReactionRepository reactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PostController();

        try {
            java.lang.reflect.Field f;

            f = PostController.class.getDeclaredField("utilisateurRepository");
            f.setAccessible(true);
            f.set(controller, utilisateurRepository);

            f = PostController.class.getDeclaredField("postRepository");
            f.setAccessible(true);
            f.set(controller, postRepository);

            f = PostController.class.getDeclaredField("commentaireRepository");
            f.setAccessible(true);
            f.set(controller, commentaireRepository);

            f = PostController.class.getDeclaredField("utilisateurService");
            f.setAccessible(true);
            f.set(controller, utilisateurService);

            f = PostController.class.getDeclaredField("groupeService");
            f.setAccessible(true);
            f.set(controller, groupeService);

            f = PostController.class.getDeclaredField("postService");
            f.setAccessible(true);
            f.set(controller, postService);

            f = PostController.class.getDeclaredField("reactionRepository");
            f.setAccessible(true);
            f.set(controller, reactionRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreerPostSansGroupe() throws IOException {
        Post post = new Post();
        MockHttpSession session = new MockHttpSession();
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String redirectUrl = controller.creerPost(post, null, null, session, null);

        verify(postService).publierPostSansGroupe(post, user);
        assertEquals("redirect:/user/1/profil", redirectUrl);
    }

    @Test
    void testCreerPostAvecGroupe() throws IOException {
        Post post = new Post();
        Integer idGrp = 5;
        MockHttpSession session = new MockHttpSession();
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeService.getGroupeByidGrp(idGrp)).thenReturn(groupe);

        String redirectUrl = controller.creerPost(post, idGrp, null, session, null);

        verify(postService).publierPostDansGroupe(post, user, groupe);
        assertEquals("redirect:redirect:/groupe/5", redirectUrl);
    }

    @Test
    void testModifierPost() throws IOException {
        Post post = new Post();
        post.setIdPost(1);
        when(postRepository.findByIdPost(1)).thenReturn(post);

        String redirect = controller.modifierPost("titre modifié", "texte modifié", null, 1, false, null);

        assertEquals("redirect:/home", redirect);
        assertEquals("titre modifié", post.getTitrePost());
        assertEquals("texte modifié", post.getTextePost());
        verify(postRepository).save(post);
    }

    @Test
    void testSupprimerPost() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        Post post = new Post();
        post.setCreateur(user);
        when(postRepository.findByIdPost(10)).thenReturn(post);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        String redirect = controller.supprimerPost(10, session, null);

        verify(postRepository).delete(post);
        assertEquals("redirect:/user/1/profil", redirect);
    }

    @Test
    void testAjouterCommentaire() {
        Utilisateur user = new Utilisateur();
        Post post = new Post();
        when(utilisateurService.getUtilisateurFromSession(any())).thenReturn(user);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        Commentaire commentaire = new Commentaire();
        String redirect = controller.ajouterCommentaire(commentaire, 1, new MockHttpSession(), null);

        assertEquals("redirect:/home", redirect);
        assertEquals(user, commentaire.getUtilisateur());
        assertEquals(post, commentaire.getPost());
        verify(commentaireRepository).save(commentaire);
    }

    @Test
    void testAjouterReaction() {
        Utilisateur user = new Utilisateur();
        Post post = new Post();
        when(utilisateurService.getUtilisateurFromSession(any())).thenReturn(user);
        when(postRepository.findByIdPost(1)).thenReturn(post);

        String redirect = controller.ajouterReaction(1, "like", new MockHttpSession(), null);

        verify(reactionRepository).deleteByPostAndUtilisateur(post, user);
        verify(reactionRepository).save(any());
        assertEquals("redirect:/home", redirect);
    }
}
