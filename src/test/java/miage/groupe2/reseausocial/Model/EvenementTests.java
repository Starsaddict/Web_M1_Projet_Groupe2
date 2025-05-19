package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

 class EvenementTests {

    @Test
    void testConstructeurEtGettersSetters() {
        Utilisateur createur = new Utilisateur();
        createur.setIdUti(1);
        createur.setNomU("Durand");

        Utilisateur participant1 = new Utilisateur();
        participant1.setIdUti(2);
        participant1.setNomU("Martin");

        Utilisateur participant2 = new Utilisateur();
        participant2.setIdUti(3);
        participant2.setNomU("Dupont");

        Evenement evenement = new Evenement(
                10,
                List.of(participant1, participant2),
                createur,
                "Soirée festive",
                "10 rue des Lilas, Paris",
                1716224400000L,
                1716220800000L,
                "Anniversaire"
        );

        assertEquals(10, evenement.getIdEve());
        assertEquals("Anniversaire", evenement.getNomE());
        assertEquals("Soirée festive", evenement.getDescription());
        assertEquals("10 rue des Lilas, Paris", evenement.getAdresseE());
        assertEquals(1716220800000L, evenement.getDateDebutE());
        assertEquals(1716224400000L, evenement.getDateFinE());
        assertEquals(createur, evenement.getCreateur());
        assertEquals(2, evenement.getParticipants().size());
    }

    @Test
    void testDatesTransitoires() {
        Evenement evenement = new Evenement();
        evenement.setDateDebutEString("20/05/2025 19:00");
        evenement.setDateFinEString("20/05/2025 23:00");

        assertEquals("20/05/2025 19:00", evenement.getDateDebutEString());
        assertEquals("20/05/2025 23:00", evenement.getDateFinEString());
    }

    @Test
    void testSettersEtGettersIndividuels() {
        Evenement e = new Evenement();
        e.setNomE("Conférence IA");
        e.setDescription("Présentation sur l'IA");
        e.setAdresseE("Université X");
        e.setDateDebutE(1717000000000L);
        e.setDateFinE(1717003600000L);

        assertEquals("Conférence IA", e.getNomE());
        assertEquals("Présentation sur l'IA", e.getDescription());
        assertEquals("Université X", e.getAdresseE());
        assertEquals(1717000000000L, e.getDateDebutE());
        assertEquals(1717003600000L, e.getDateFinE());
    }
}
