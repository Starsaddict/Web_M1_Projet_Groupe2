package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GroupeTest {

    private Groupe groupe;
    private Utilisateur createur;
    private List<Utilisateur> membres;
    private List<Post> posts;

    @BeforeEach
    void setUp() {
        createur = new Utilisateur();
        createur.setIdUti(1);
        createur.setNomU("Doe");

        Utilisateur membre1 = new Utilisateur();
        membre1.setIdUti(2);

        Utilisateur membre2 = new Utilisateur();
        membre2.setIdUti(3);

        membres = new ArrayList<>();
        membres.add(membre1);
        membres.add(membre2);

        Post post = new Post();
        post.setIdPost(10);

        posts = new ArrayList<>();
        posts.add(post);

        groupe = new Groupe(100, membres, createur, 1716076782L, "MIAGE Group", "Groupe de discussion");
        groupe.setPosts(posts);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(100, groupe.getIdGrp());
        assertEquals("MIAGE Group", groupe.getNomG());
        assertEquals("Groupe de discussion", groupe.getDescription());
        assertEquals(1716076782L, groupe.getDateCreation());

        assertEquals(createur, groupe.getCreateur());
        assertEquals(2, groupe.getMembres().size());
        assertEquals(posts, groupe.getPosts());
    }

    @Test
    void testSetters() {
        Groupe g = new Groupe();

        g.setIdGrp(200);
        g.setNomG("Test Group");
        g.setDescription("Description test");
        g.setDateCreation(123456789L);
        g.setCreateur(createur);
        g.setMembres(membres);
        g.setPosts(posts);

        assertEquals(200, g.getIdGrp());
        assertEquals("Test Group", g.getNomG());
        assertEquals("Description test", g.getDescription());
        assertEquals(123456789L, g.getDateCreation());
        assertEquals(createur, g.getCreateur());
        assertEquals(membres, g.getMembres());
        assertEquals(posts, g.getPosts());
    }
}
