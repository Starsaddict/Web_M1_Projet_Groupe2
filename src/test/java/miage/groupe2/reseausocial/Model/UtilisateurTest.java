package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class UtilisateurTest {

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setIdUti(1);
        utilisateur.setNomU("Dupont");
        utilisateur.setPrenomU("Jean");
        utilisateur.setEmailU("jean.dupont@example.com");
        utilisateur.setMdpU("password123");
        utilisateur.setPseudoU("jeanjean");
        utilisateur.setAvatar(new byte[]{1, 2, 3});
    }

    @Test
    void testAttributsSimples() {
        assertThat(utilisateur.getIdUti()).isEqualTo(1);
        assertThat(utilisateur.getNomU()).isEqualTo("Dupont");
        assertThat(utilisateur.getPrenomU()).isEqualTo("Jean");
        assertThat(utilisateur.getEmailU()).isEqualTo("jean.dupont@example.com");
        assertThat(utilisateur.getMdpU()).isEqualTo("password123");
        assertThat(utilisateur.getPseudoU()).isEqualTo("jeanjean");
        assertThat(utilisateur.getAvatar()).isEqualTo(new byte[]{1, 2, 3});
    }

    @Test
    void testCollectionsInitialisationEtAssignation() {
        Post post = new Post();
        Evenement evenement = new Evenement();
        Groupe groupe = new Groupe();
        Conversation conv = new Conversation();
        Commentaire commentaire = new Commentaire();
        Reaction reaction = new Reaction();
        Message message = new Message();
        DemandeAmi demande = new DemandeAmi();

        utilisateur.setPosts(new ArrayList<>(Arrays.asList(post)));
        utilisateur.setEvenements(new ArrayList<>(Arrays.asList(evenement)));
        utilisateur.setGroupes(new ArrayList<>(Arrays.asList(groupe)));
        utilisateur.setConversations(new ArrayList<>(Arrays.asList(conv)));
        utilisateur.setCommentaires(new ArrayList<>(Arrays.asList(commentaire)));
        utilisateur.setReactions(new ArrayList<>(Arrays.asList(reaction)));
        utilisateur.setMessages(new ArrayList<>(Arrays.asList(message)));
        utilisateur.setDemandesEnvoyees(new ArrayList<>(Arrays.asList(demande)));
        utilisateur.setDemandesRecues(new ArrayList<>(Arrays.asList(demande)));
        utilisateur.setGroupesAppartenance(new ArrayList<>(Arrays.asList(groupe)));
        utilisateur.setEvenementsAssistes(new ArrayList<>(Arrays.asList(evenement)));
        utilisateur.setAmis(new ArrayList<>());
        utilisateur.setPostsRepostes(new ArrayList<>(Arrays.asList(post)));
        utilisateur.setConversationsParticipees(new ArrayList<>(Arrays.asList(conv)));

        assertThat(utilisateur.getPosts()).containsExactly(post);
        assertThat(utilisateur.getEvenements()).containsExactly(evenement);
        assertThat(utilisateur.getGroupes()).containsExactly(groupe);
        assertThat(utilisateur.getConversations()).containsExactly(conv);
        assertThat(utilisateur.getCommentaires()).containsExactly(commentaire);
        assertThat(utilisateur.getReactions()).containsExactly(reaction);
        assertThat(utilisateur.getMessages()).containsExactly(message);
        assertThat(utilisateur.getDemandesEnvoyees()).containsExactly(demande);
        assertThat(utilisateur.getDemandesRecues()).containsExactly(demande);
        assertThat(utilisateur.getGroupesAppartenance()).containsExactly(groupe);
        assertThat(utilisateur.getEvenementsAssistes()).containsExactly(evenement);
        assertThat(utilisateur.getAmis()).isEmpty();
        assertThat(utilisateur.getPostsRepostes()).containsExactly(post);
        assertThat(utilisateur.getConversationsParticipees()).containsExactly(conv);
    }

    @Test
    void testEqualsAndHashCode() {
        Utilisateur autre = new Utilisateur();
        autre.setIdUti(1);

        Utilisateur different = new Utilisateur();
        different.setIdUti(2);

        assertThat(utilisateur).isEqualTo(autre);
        assertThat(utilisateur).hasSameHashCodeAs(autre);
        assertThat(utilisateur).isNotEqualTo(different);
    }

    @Test
    void testEqualsAvecNullEtAutreClasse() {
        assertThat(utilisateur.equals(null)).isFalse();
        assertThat(utilisateur.equals("string")).isFalse();
    }
}
