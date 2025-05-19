package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReactionTests {

    private Reaction reaction;

    @BeforeEach
    void setUp() {
        reaction = new Reaction();
    }

    @Test
    void testSetAndGetIdrea() {
        reaction.setIdrea(42);
        assertEquals(42, reaction.getIdrea());
    }

    @Test
    void testSetAndGetType() {
        reaction.setType("like");
        assertEquals("like", reaction.getType());
    }

    @Test
    void testSetAndGetUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        reaction.setUtilisateur(utilisateur);
        assertEquals(utilisateur, reaction.getUtilisateur());
    }

    @Test
    void testSetAndGetPost() {
        Post post = new Post();
        reaction.setPost(post);
        assertEquals(post, reaction.getPost());
    }

    @Test
    void testConstructorWithParams() {
        Post post = new Post();
        Utilisateur utilisateur = new Utilisateur();
        Reaction r = new Reaction(post, utilisateur, "dislike", 7);

        assertEquals(post, r.getPost());
        assertEquals(utilisateur, r.getUtilisateur());
        assertEquals("dislike", r.getType());
        assertEquals(7, r.getIdrea());
    }
}
