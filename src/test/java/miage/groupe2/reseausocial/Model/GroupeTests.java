package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupeTests {

    @Test
    void testConstructeurAvecParametres() {
        Utilisateur createur = new Utilisateur();
        createur.setIdUti(1);
        createur.setNomU("Alice");

        Utilisateur membre1 = new Utilisateur();
        membre1.setIdUti(2);

        Utilisateur membre2 = new Utilisateur();
        membre2.setIdUti(3);

        Groupe groupe = new Groupe(
                100,
                List.of(membre1, membre2),
                createur,
                1716220800000L,
                "Groupe MIAGE",
                "Groupe pour les étudiants en MIAGE"
        );

        assertEquals(100, groupe.getIdGrp());
        assertEquals("Groupe MIAGE", groupe.getNomG());
        assertEquals("Groupe pour les étudiants en MIAGE", groupe.getDescription());
        assertEquals(1716220800000L, groupe.getDateCreation());
        assertEquals(createur, groupe.getCreateur());
        assertEquals(2, groupe.getMembres().size());
        assertTrue(groupe.getMembres().contains(membre1));
        assertTrue(groupe.getMembres().contains(membre2));
    }

    @Test
    void testConstructeurParDefautEtSetters() {
        Groupe groupe = new Groupe();

        Utilisateur createur = new Utilisateur();
        createur.setIdUti(10);

        Utilisateur membre = new Utilisateur();
        membre.setIdUti(11);

        groupe.setIdGrp(200);
        groupe.setNomG("Test Groupe");
        groupe.setDescription("Description test");
        groupe.setDateCreation(1716229999000L);
        groupe.setCreateur(createur);
        groupe.setMembres(List.of(membre));

        assertEquals(200, groupe.getIdGrp());
        assertEquals("Test Groupe", groupe.getNomG());
        assertEquals("Description test", groupe.getDescription());
        assertEquals(1716229999000L, groupe.getDateCreation());
        assertEquals(createur, groupe.getCreateur());
        assertEquals(1, groupe.getMembres().size());
        assertEquals(membre, groupe.getMembres().get(0));
    }
}
