package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.*;
import miage.groupe2.reseausocial.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @InjectMocks
    PostController postController;

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentaireRepository commentaireRepository;

    @Mock
    ReactionRepository reactionRepository;

    @Mock
    UtilisateurService utilisateurService;

    @Mock
    GroupeService groupeService;

    @Mock
    PostService postService;

    @Mock
    Model model;

    @Mock
    HttpSession session;

    @Mock
    RedirectAttributes redirectAttributes;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void postPersonne_shouldReturnListPostsPersonne() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        List<Post> posts = List.of(new Post(), new Post());
        when(utilisateurRepository.findByidUti(1L)).thenReturn(user);
        when(postRepository.findByCreateur(user)).thenReturn(posts);

        String view = postController.postPersonne(1, model);

        verify(model).addAttribute("Posts", posts);
        verify(model).addAttribute("userId", 1);
        assertEquals("listPostsPersonne", view);
    }

    @Test
    void creerPost_withGroup_shouldRedirectToGroup() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(2);
        Groupe groupe = new Groupe();
        groupe.setIdGrp(10);

        Post post = new Post();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeService.getGroupeByidGrp(10)).thenReturn(groupe);

        String redirect = postController.creerPost(post, 10, session);

        verify(postService).publierPostDansGroupe(post, user, groupe);
        assertEquals("redirect:/groupe/10", redirect);
    }

    @Test
    void creerPost_withoutGroup_shouldRedirectToUser() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(3);
        Post post = new Post();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String redirect = postController.creerPost(post, null, session);

        verify(postService).publierPostSansGroupe(post, user);
        assertEquals("redirect:/user/3", redirect);
    }

    @Test
    void listPosts_shouldReturnListPosts() {
        Post post1 = new Post();
        post1.setDatePost(2L);
        Post post2 = new Post();
        post2.setDatePost(1L);
        post2.setGroupe(new Groupe());
        Post post3 = new Post();
        post3.setDatePost(3L);
        List<Post> postsAll = List.of(post1, post2, post3);

        when(postRepository.findAll()).thenReturn(postsAll);

        String view = postController.listPosts(model);

        ArgumentCaptor<List<Post>> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("posts"), captor.capture());

        List<Post> filteredPosts = captor.getValue();

        assertTrue(filteredPosts.contains(post1));
        assertFalse(filteredPosts.contains(post2));
        assertEquals(2, filteredPosts.size());
        assertEquals("listPosts", view);
    }

    @Test
    void afficherPostParId_found_shouldReturnDetailPost() {
        Post post = new Post();
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        List<Commentaire> commentaires = List.of(new Commentaire());
        when(commentaireRepository.findByPost(post)).thenReturn(commentaires);

        String view = postController.afficherPostParId(1, model);

        verify(model).addAttribute("post", post);
        verify(model).addAttribute("commentaires", commentaires);
        verify(model).addAttribute(eq("nouveauCommentaire"), any(Commentaire.class));
        assertEquals("detailPost", view);
    }

    @Test
    void afficherPostParId_notFound_shouldRedirectHome() {
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        String redirect = postController.afficherPostParId(1, model);

        assertEquals("redirect:/home", redirect);
    }

    @Test
    void supprimerPost_withOwner_shouldDeleteAndRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(5);
        Post post = new Post();
        post.setCreateur(user);

        when(session.getAttribute("user")).thenReturn(user);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        String redirect = postController.supprimerPost(1, session);

        verify(postRepository).delete(post);
        assertEquals("redirect:/user/5", redirect);
    }

    @Test
    void supprimerPost_notOwner_shouldNotDelete() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(5);
        Utilisateur other = new Utilisateur();
        other.setIdUti(6);
        Post post = new Post();
        post.setCreateur(other);

        when(session.getAttribute("user")).thenReturn(user);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        String redirect = postController.supprimerPost(1, session);

        verify(postRepository, never()).delete(any());
        assertEquals("redirect:/user/5", redirect);
    }

    @Test
    void ajouterCommentaire_valid_shouldSaveAndRedirect() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(7);
        Post post = new Post();

        when(session.getAttribute("user")).thenReturn(user);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        Commentaire commentaire = new Commentaire();

        String redirect = postController.ajouterCommentaire(commentaire, 1, session, null);

        verify(commentaireRepository).save(commentaire);
        assertEquals("redirect:/home", redirect);
        assertEquals(user, commentaire.getUtilisateur());
        assertEquals(post, commentaire.getPost());
        assertTrue(commentaire.getDateC() > 0);
    }

    @Test
    void ajouterCommentaire_invalid_shouldRedirectLogin() {
        when(session.getAttribute("user")).thenReturn(null);
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        String redirect = postController.ajouterCommentaire(new Commentaire(), 1, session, null);

        assertEquals("redirect:/auth/login", redirect);
    }

    @Test
    void ajouterReaction_shouldDeleteAndSave() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(8);
        Post post = new Post();

        when(session.getAttribute("user")).thenReturn(user);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        String redirect = postController.ajouterReaction(1, "like", session, null);

        verify(reactionRepository).deleteByPostAndUtilisateur(post, user);
        verify(reactionRepository).save(any(Reaction.class));
        assertEquals("redirect:/home", redirect);
    }

}
