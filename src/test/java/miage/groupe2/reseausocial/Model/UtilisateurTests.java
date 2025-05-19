package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilisateurTests {

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
    }

    @Test
    void testSetAndGetBasicAttributes() {
        utilisateur.setIdUti(1);
        utilisateur.setNomU("Dupont");
        utilisateur.setPrenomU("Jean");
        utilisateur.setEmailU("jean.dupont@example.com");
        utilisateur.setMdpU("password123");

        assertEquals(1, utilisateur.getIdUti());
        assertEquals("Dupont", utilisateur.getNomU());
        assertEquals("Jean", utilisateur.getPrenomU());
        assertEquals("jean.dupont@example.com", utilisateur.getEmailU());
        assertEquals("password123", utilisateur.getMdpU());
    }

    @Test
    void testManipulateAmis() {
        Utilisateur ami1 = new Utilisateur();
        ami1.setIdUti(2);
        ami1.setNomU("Martin");

        Utilisateur ami2 = new Utilisateur();
        ami2.setIdUti(3);
        ami2.setNomU("Bernard");

        List<Utilisateur> amis = new ArrayList<>();
        amis.add(ami1);
        amis.add(ami2);

        utilisateur.setAmis(amis);

        assertEquals(2, utilisateur.getAmis().size());
        assertTrue(utilisateur.getAmis().contains(ami1));
        assertTrue(utilisateur.getAmis().contains(ami2));

        utilisateur.getAmis().remove(ami1);
        assertEquals(1, utilisateur.getAmis().size());
        assertFalse(utilisateur.getAmis().contains(ami1));
    }

    @Test
    void testManipulatePosts() {
        Post post1 = new Post();
        post1.setIdPost(10);
        Post post2 = new Post();
        post2.setIdPost(20);

        List<Post> posts = new ArrayList<>();
        posts.add(post1);
        posts.add(post2);

        utilisateur.setPosts(posts);

        assertEquals(2, utilisateur.getPosts().size());
        assertTrue(utilisateur.getPosts().contains(post1));
        assertTrue(utilisateur.getPosts().contains(post2));
    }

    @Test
    void testManipulateGroupes() {
        Groupe grp1 = new Groupe();
        grp1.setIdGrp(100);
        Groupe grp2 = new Groupe();
        grp2.setIdGrp(200);

        List<Groupe> groupes = new ArrayList<>();
        groupes.add(grp1);
        groupes.add(grp2);

        utilisateur.setGroupesAppartenance(groupes);

        assertEquals(2, utilisateur.getGroupesAppartenance().size());
        assertTrue(utilisateur.getGroupesAppartenance().contains(grp1));
        assertTrue(utilisateur.getGroupesAppartenance().contains(grp2));
    }

    @Test
    void testEqualsAndHashCode() {
        Utilisateur u1 = new Utilisateur();
        u1.setIdUti(10);
        Utilisateur u2 = new Utilisateur();
        u2.setIdUti(10);
        Utilisateur u3 = new Utilisateur();
        u3.setIdUti(20);

        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertEquals(u1.hashCode(), u2.hashCode());
        assertNotEquals(u1.hashCode(), u3.hashCode());
    }
}
