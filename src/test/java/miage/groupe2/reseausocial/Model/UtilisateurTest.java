package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UtilisateurTest {

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setIdUti(1);
        utilisateur.setNomU("Doe");
        utilisateur.setPrenomU("John");
        utilisateur.setPseudoU("jdoe");
        utilisateur.setEmailU("john.doe@example.com");
        utilisateur.setMdpU("password123");
        utilisateur.setIntroductionU("Salut, je suis John !");
        utilisateur.setAvatar(new byte[]{1, 2, 3});
    }

    @Test
    void testGettersSetters() {
        assertEquals(1, utilisateur.getIdUti());
        assertEquals("Doe", utilisateur.getNomU());
        assertEquals("John", utilisateur.getPrenomU());
        assertEquals("jdoe", utilisateur.getPseudoU());
        assertEquals("john.doe@example.com", utilisateur.getEmailU());
        assertEquals("password123", utilisateur.getMdpU());
        assertEquals("Salut, je suis John !", utilisateur.getIntroductionU());
        assertArrayEquals(new byte[]{1, 2, 3}, utilisateur.getAvatar());
    }

    @Test
    void testListesAssociations() {
        Post post = new Post();
        Evenement evenement = new Evenement();
        Groupe groupe = new Groupe();
        Conversation conv = new Conversation();
        Commentaire commentaire = new Commentaire();
        Reaction reaction = new Reaction();
        Message message = new Message();
        DemandeAmi demande = new DemandeAmi();

        utilisateur.setPosts(Arrays.asList(post));
        utilisateur.setEvenements(Arrays.asList(evenement));
        utilisateur.setGroupes(Arrays.asList(groupe));
        utilisateur.setConversations(Arrays.asList(conv));
        utilisateur.setCommentaires(Arrays.asList(commentaire));
        utilisateur.setReactions(Arrays.asList(reaction));
        utilisateur.setMessages(Arrays.asList(message));
        utilisateur.setDemandesEnvoyees(Arrays.asList(demande));
        utilisateur.setDemandesRecues(Arrays.asList(demande));
        utilisateur.setGroupesAppartenance(Arrays.asList(groupe));
        utilisateur.setEvenementsAssistes(Arrays.asList(evenement));
        utilisateur.setAmis(new ArrayList<>(Arrays.asList(new Utilisateur())));
        utilisateur.setPostsRepostes(Arrays.asList(post));
        utilisateur.setConversationsParticipees(Arrays.asList(conv));

        assertEquals(1, utilisateur.getPosts().size());
        assertEquals(1, utilisateur.getEvenements().size());
        assertEquals(1, utilisateur.getGroupes().size());
        assertEquals(1, utilisateur.getConversations().size());
        assertEquals(1, utilisateur.getCommentaires().size());
        assertEquals(1, utilisateur.getReactions().size());
        assertEquals(1, utilisateur.getMessages().size());
        assertEquals(1, utilisateur.getDemandesEnvoyees().size());
        assertEquals(1, utilisateur.getDemandesRecues().size());
        assertEquals(1, utilisateur.getGroupesAppartenance().size());
        assertEquals(1, utilisateur.getEvenementsAssistes().size());
        assertEquals(1, utilisateur.getAmis().size());
        assertEquals(1, utilisateur.getPostsRepostes().size());
        assertEquals(1, utilisateur.getConversationsParticipees().size());
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
        assertEquals(u1.hashCode(), u2.hashCode());
        assertNotEquals(u1, u3);
    }

    @Test
    void testEqualsNullAndDifferentClass() {
        assertNotEquals(utilisateur, null);
        assertNotEquals(utilisateur, "not a user");
    }

    @Test
    void testGetAmisLazyInit() {
        Utilisateur u = new Utilisateur();
        assertNotNull(u.getAmis());
        assertTrue(u.getAmis().isEmpty());
    }
}
