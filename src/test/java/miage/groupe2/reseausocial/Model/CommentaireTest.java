package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

 class CommentaireTest {

    @Test
    void testConstructeurVide() {
        Commentaire commentaire = new Commentaire();
        assertNotNull(commentaire);
    }

    @Test
    void testConstructeurAvecParametres() {
        Post post = new Post();
        Utilisateur utilisateur = new Utilisateur();
        long dateC = 123456789L;
        String texte = "Ceci est un commentaire.";
        Integer id = 1;

        Commentaire commentaire = new Commentaire(id, post, utilisateur, dateC, texte);

        assertEquals(id, commentaire.getIdCom());
        assertEquals(post, commentaire.getPost());
        assertEquals(utilisateur, commentaire.getUtilisateur());
        assertEquals(dateC, commentaire.getDateC());
        assertEquals(texte, commentaire.getTextCom());
    }

    @Test
    void testSettersEtGetters() {
        Commentaire commentaire = new Commentaire();

        Integer id = 10;
        String texte = "Nouveau commentaire";
        long date = 987654321L;
        Post post = new Post();
        Utilisateur utilisateur = new Utilisateur();

        commentaire.setIdCom(id);
        commentaire.setTextCom(texte);
        commentaire.setDateC(date);
        commentaire.setPost(post);
        commentaire.setUtilisateur(utilisateur);

        assertEquals(id, commentaire.getIdCom());
        assertEquals(texte, commentaire.getTextCom());
        assertEquals(date, commentaire.getDateC());
        assertEquals(post, commentaire.getPost());
        assertEquals(utilisateur, commentaire.getUtilisateur());
    }
}
