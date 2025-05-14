package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Représente un événement organisé sur le réseau social.
 * Un événement possède un nom, une adresse, des dates de début et de fin, un créateur et une liste de participants.
 */
@Entity
public class Evenement {

    /**
     * Identifiant unique de l’événement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEve;

    /**
     * Nom de l’événement.
     */
    private String nomE;

    /**
     * Date de début de l’événement.
     */
    private LocalDate dateDebutE;

    /**
     * Date de fin de l’événement.
     */
    private LocalDate dateFinE;

    /**
     * Adresse où se déroule l’événement.
     */
    private String adresseE;

    /**
     * Utilisateur ayant créé l’événement.
     */
    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    /**
     * Liste des utilisateurs participant à l’événement.
     */
    @ManyToMany(mappedBy = "evenementsAssistes")
    private List<Utilisateur> participants;

    /**
     * Constructeur avec tous les champs.
     *
     * @param idEve identifiant de l’événement
     * @param participants liste des participants
     * @param createur utilisateur créateur
     * @param adresseE adresse de l’événement
     * @param dateFinE date de fin
     * @param dateDebutE date de début
     * @param nomE nom de l’événement
     */
    public Evenement(Integer idEve, List<Utilisateur> participants, Utilisateur createur, String adresseE, LocalDate dateFinE, LocalDate dateDebutE, String nomE) {
        this.idEve = idEve;
        this.participants = participants;
        this.createur = createur;
        this.adresseE = adresseE;
        this.dateFinE = dateFinE;
        this.dateDebutE = dateDebutE;
        this.nomE = nomE;
    }

    /**
     * Constructeur par défaut requis par JPA.
     */
    public Evenement() {
    }

    /**
     * @return l’identifiant de l’événement
     */
    public Integer getIdEve() {
        return idEve;
    }

    /**
     * @param idEve identifiant à définir
     */
    public void setIdEve(Integer idEve) {
        this.idEve = idEve;
    }

    /**
     * @return la liste des participants
     */
    public List<Utilisateur> getParticipants() {
        return participants;
    }

    /**
     * @param participants liste des utilisateurs à définir comme participants
     */
    public void setParticipants(List<Utilisateur> participants) {
        this.participants = participants;
    }

    /**
     * @return l’utilisateur créateur de l’événement
     */
    public Utilisateur getCreateur() {
        return createur;
    }

    /**
     * @param createur utilisateur créateur à définir
     */
    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    /**
     * @return l’adresse de l’événement
     */
    public String getAdresseE() {
        return adresseE;
    }

    /**
     * @param adresseE adresse à définir
     */
    public void setAdresseE(String adresseE) {
        this.adresseE = adresseE;
    }

    /**
     * @return la date de fin de l’événement
     */
    public LocalDate getDateFinE() {
        return dateFinE;
    }

    /**
     * @param dateFinE date de fin à définir
     */
    public void setDateFinE(LocalDate dateFinE) {
        this.dateFinE = dateFinE;
    }

    /**
     * @return la date de début de l’événement
     */
    public LocalDate getDateDebutE() {
        return dateDebutE;
    }

    /**
     * @param dateDebutE date de début à définir
     */
    public void setDateDebutE(LocalDate dateDebutE) {
        this.dateDebutE = dateDebutE;
    }

    /**
     * @return le nom de l’événement
     */
    public String getNomE() {
        return nomE;
    }

    /**
     * @param nomE nom de l’événement à définir
     */
    public void setNomE(String nomE) {
        this.nomE = nomE;
    }
}
