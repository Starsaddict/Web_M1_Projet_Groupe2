package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Commentaire;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.CommentaireRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentaireRepository commentaireRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private PostController postController;

    private Utilisateur user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Utilisateur();
        user.setIdUti(1);
    }

    @Test
    void testPostPersonne() {
        when(utilisateurRepository.findByidUti(1L)).thenReturn(user);
        when(postRepository.findByCreateur(user)).thenReturn(new ArrayList<>());

        String view = postController.postPersonne(1L, model);

        assertEquals("listPostsPersonne", view);
        verify(model).addAttribute("Posts", new ArrayList<>());
        verify(model).addAttribute("userId", 1L);
    }

    @Test
    void testCreerPost_Get() {
        String view = postController.CreerPost(model);
        assertEquals("creerPost", view);
        verify(model).addAttribute(eq("Post"), any(Post.class));
    }

    @Test
    void testCreerPost_Post() {
        Post post = new Post();
        when(session.getAttribute("user")).thenReturn(user);

        String view = postController.CreerPost(post, session);

        verify(postRepository).save(post);
        assertEquals("redirect:/user/1", view);
    }

    @Test
    void testAfficherPostParId() {
        Post post = new Post();
        post.setIdPost(123);
        List<Commentaire> commentaires = new ArrayList<>();

        when(postRepository.findById(123)).thenReturn(Optional.of(post));
        when(commentaireRepository.findByPost(post)).thenReturn(commentaires);

        String view = postController.afficherPostParId(123, model);

        assertEquals("detailPost", view);
        verify(model).addAttribute("post", post);
        verify(model).addAttribute("commentaires", commentaires);
        verify(model).addAttribute(eq("nouveauCommentaire"), any(Commentaire.class));
    }

    @Test
    void testSupprimerPost() {
        Post post = new Post();
        post.setIdPost(123);
        post.setCreateur(user);

        when(postRepository.findById(123)).thenReturn(Optional.of(post));
        when(session.getAttribute("user")).thenReturn(user);

        String view = postController.supprimerPost(123, session);

        verify(postRepository).delete(post);
        assertEquals("redirect:/user/1", view);
    }

    @Test
    void testAjouterCommentaire() {
        Commentaire commentaire = new Commentaire();
        Post post = new Post();
        post.setIdPost(10);

        when(session.getAttribute("user")).thenReturn(user);
        when(postRepository.findById(10)).thenReturn(Optional.of(post));

        String view = postController.ajouterCommentaire(commentaire, 10, session);

        verify(commentaireRepository).save(any(Commentaire.class));
        assertEquals("redirect:/post?id=10", view);
    }

    @Test
    void testRepostPost() {
        Post post = new Post();
        post.setIdPost(42);
        List<Post> reposts = new ArrayList<>();

        user.setPostsRepostes(reposts);

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(1L)).thenReturn(user);
        when(postRepository.findById(42)).thenReturn(Optional.of(post));

        String view = postController.repostPost(42, session, redirectAttributes);

        assertEquals("redirect:/user/1", view);
        verify(utilisateurRepository).save(user);
    }
}
