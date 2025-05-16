package miage.groupe2.reseausocial.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DemandeAmiTest {

    @Test
    void testConstructeurParDefaut() {
        DemandeAmi demande = new DemandeAmi();
        assertNotNull(demande);
    }

    @Test
    void testConstructeurComplet() {
        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(1);
        demandeur.setNomU("Alice");

        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(2);
        recepteur.setNomU("Bob");

        long date = System.currentTimeMillis();
        DemandeAmi demande = new DemandeAmi(10, recepteur, demandeur, date, "en attente");

        assertEquals(10, demande.getIdDA());
        assertEquals(recepteur, demande.getRecepteur());
        assertEquals(demandeur, demande.getDemandeur());
        assertEquals(date, demande.getDateDA());
        assertEquals("en attente", demande.getStatut());
    }

    @Test
    void testSettersEtGetters() {
        DemandeAmi demande = new DemandeAmi();

        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(1);

        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(2);

        demande.setIdDA(5);
        demande.setDemandeur(demandeur);
        demande.setRecepteur(recepteur);
        demande.setDateDA(123456789L);
        demande.setStatut("acceptée");

        assertEquals(5, demande.getIdDA());
        assertEquals(demandeur, demande.getDemandeur());
        assertEquals(recepteur, demande.getRecepteur());
        assertEquals(123456789L, demande.getDateDA());
        assertEquals("acceptée", demande.getStatut());
    }
}
