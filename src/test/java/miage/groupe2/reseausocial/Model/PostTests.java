package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostTests {

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
    }

    @Test
    void testSetAndGetIdPost() {
        post.setIdPost(1);
        assertEquals(1, post.getIdPost());
    }

    @Test
    void testSetAndGetTitrePost() {
        post.setTitrePost("Mon Titre");
        assertEquals("Mon Titre", post.getTitrePost());
    }

    @Test
    void testSetAndGetTextePost() {
        post.setTextePost("Contenu du post");
        assertEquals("Contenu du post", post.getTextePost());
    }

    @Test
    void testSetAndGetDatePost() {
        post.setDatePost(123456789L);
        assertEquals(123456789L, post.getDatePost());
    }

    @Test
    void testSetAndGetImagePost() {
        byte[] image = {1, 2, 3};
        post.setImagePost(image);
        assertArrayEquals(image, post.getImagePost());
    }

    @Test
    void testSetAndGetCreateur() {
        Utilisateur utilisateur = new Utilisateur();
        post.setCreateur(utilisateur);
        assertEquals(utilisateur, post.getCreateur());
    }

    @Test
    void testSetAndGetCommentaires() {
        Commentaire c1 = new Commentaire();
        Commentaire c2 = new Commentaire();
        List<Commentaire> commentaires = List.of(c1, c2);
        post.setCommentaires(commentaires);
        assertEquals(commentaires, post.getCommentaires());
    }

    @Test
    void testSetAndGetReactions() {
        Reaction r1 = new Reaction();
        Reaction r2 = new Reaction();
        List<Reaction> reactions = List.of(r1, r2);
        post.setReactions(reactions);
        assertEquals(reactions, post.getReactions());
    }

    @Test
    void testSetAndGetUtilisateursRepost() {
        Utilisateur u1 = new Utilisateur();
        Utilisateur u2 = new Utilisateur();
        List<Utilisateur> utilisateurs = List.of(u1, u2);
        post.setUtilisateursRepost(utilisateurs);
        assertEquals(utilisateurs, post.getUtilisateursRepost());
    }
}
