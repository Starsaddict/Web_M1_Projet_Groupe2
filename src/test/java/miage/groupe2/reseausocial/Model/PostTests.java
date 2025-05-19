package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostTests {

    @Test
    void testConstructeurParDefautEtSetters() {
        Post post = new Post();

        Utilisateur createur = new Utilisateur();
        createur.setIdUti(1);
        createur.setNomU("Alice");

        Commentaire commentaire = new Commentaire();
        commentaire.setIdCommentaire(10);

        Reaction reaction = new Reaction();
        reaction.setIdReaction(20);

        Utilisateur reposteur = new Utilisateur();
        reposteur.setIdUti(2);

        post.setIdPost(100);
        post.setTitrePost("Titre exemple");
        post.setTextePost("Contenu du post");
        post.setDatePost(1716221234567L);
        post.setCreateur(createur);
        post.setCommentaires(List.of(commentaire));
        post.setReactions(List.of(reaction));
        post.setUtilisateursRepost(List.of(reposteur));
        post.setImagePost("image".getBytes());

        assertEquals(100, post.getIdPost());
        assertEquals("Titre exemple", post.getTitrePost());
        assertEquals("Contenu du post", post.getTextePost());
        assertEquals(1716221234567L, post.getDatePost());
        assertEquals(createur, post.getCreateur());
        assertEquals(1, post.getCommentaires().size());
        assertEquals(commentaire, post.getCommentaires().get(0));
        assertEquals(1, post.getReactions().size());
        assertEquals(reaction, post.getReactions().get(0));
        assertEquals(1, post.getUtilisateursRepost().size());
        assertEquals(reposteur, post.getUtilisateursRepost().get(0));
        assertArrayEquals("image".getBytes(), post.getImagePost());
    }
}
