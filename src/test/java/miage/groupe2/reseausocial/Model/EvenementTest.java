package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvenementTest {

    private Evenement evenement;
    private Utilisateur createur;
    private List<Utilisateur> participants;

    @BeforeEach
    void setUp() {
        createur = new Utilisateur();
        createur.setIdUti(1);
        createur.setNomU("Doe");
        createur.setPrenomU("John");

        Utilisateur p1 = new Utilisateur();
        p1.setIdUti(2);

        Utilisateur p2 = new Utilisateur();
        p2.setIdUti(3);

        participants = new ArrayList<>();
        participants.add(p1);
        participants.add(p2);

        evenement = new Evenement(
            10,
            participants,
            createur,
            "Soirée de lancement",
            "10 avenue de Paris",
            1684303200000L, 
            1684216800000L, 
            "Lancement App"
        );
    }

    @Test
    void testGetters() {
        assertEquals(10, evenement.getIdEve());
        assertEquals("Soirée de lancement", evenement.getDescription());
        assertEquals("10 avenue de Paris", evenement.getAdresseE());
        assertEquals("Lancement App", evenement.getNomE());
        assertEquals(1684303200000L, evenement.getDateFinE());
        assertEquals(1684216800000L, evenement.getDateDebutE());

        assertEquals(createur, evenement.getCreateur());
        assertEquals(2, evenement.getParticipants().size());
        assertTrue(evenement.getParticipants().containsAll(participants));
    }

    @Test
    void testSetters() {
        evenement.setNomE("Nouvel Événement");
        evenement.setDescription("Description modifiée");
        evenement.setAdresseE("Nouvelle adresse");
        evenement.setDateDebutE(1684000000000L);
        evenement.setDateFinE(1684100000000L);

        Utilisateur newCreateur = new Utilisateur();
        newCreateur.setIdUti(99);
        evenement.setCreateur(newCreateur);

        assertEquals("Nouvel Événement", evenement.getNomE());
        assertEquals("Description modifiée", evenement.getDescription());
        assertEquals("Nouvelle adresse", evenement.getAdresseE());
        assertEquals(1684000000000L, evenement.getDateDebutE());
        assertEquals(1684100000000L, evenement.getDateFinE());
        assertEquals(newCreateur, evenement.getCreateur());
    }

    @Test
    void testDefaultConstructor() {
        Evenement e = new Evenement();
        assertNull(e.getIdEve());
        assertNull(e.getNomE());
        assertNull(e.getAdresseE());
        assertNull(e.getCreateur());
        assertNull(e.getParticipants());
    }
}
