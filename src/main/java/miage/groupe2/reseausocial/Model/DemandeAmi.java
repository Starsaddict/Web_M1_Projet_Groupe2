package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

/**
 * Représente une demande d’amitié entre deux utilisateurs du réseau social.
 * Une demande est composée d’un demandeur, d’un récepteur, d’un statut et d’une date.
 */
@Entity
public class DemandeAmi {

    /**
     * Identifiant unique de la demande d’amitié.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDA;

    /**
     * Statut de la demande (ex: "en attente", "acceptée", "refusée").
     */
    private String statut;

    /**
     * Date à laquelle la demande a été envoyée.
     */
    private long dateDA;

    /**
     * Utilisateur ayant envoyé la demande.
     */
    @ManyToOne
    @JoinColumn(name = "idUtiDemandeur")
    private Utilisateur demandeur;

    /**
     * Utilisateur qui a reçu la demande.
     */
    @ManyToOne
    @JoinColumn(name = "idUtiRecepteur")
    private Utilisateur recepteur;

    /**
     * Constructeur avec tous les champs.
     *
     * @param idDA identifiant de la demande
     * @param recepteur utilisateur recevant la demande
     * @param demandeur utilisateur envoyant la demande
     * @param dateDA date d’envoi
     * @param statut statut de la demande
     */
    public DemandeAmi(Integer idDA, Utilisateur recepteur, Utilisateur demandeur, long dateDA, String statut) {
        this.idDA = idDA;
        this.recepteur = recepteur;
        this.demandeur = demandeur;
        this.dateDA = dateDA;
        this.statut = statut;
    }

    /**
     * Constructeur par défaut requis par JPA.
     */
    public DemandeAmi() {
    }

    /**
     * @return l’identifiant de la demande
     */
    public Integer getIdDA() {
        return idDA;
    }

    /**
     * @param idDA identifiant à définir
     */
    public void setIdDA(Integer idDA) {
        this.idDA = idDA;
    }

    /**
     * @return le récepteur de la demande
     */
    public Utilisateur getRecepteur() {
        return recepteur;
    }

    /**
     * @param recepteur utilisateur recevant la demande
     */
    public void setRecepteur(Utilisateur recepteur) {
        this.recepteur = recepteur;
    }

    /**
     * @return le demandeur de la demande
     */
    public Utilisateur getDemandeur() {
        return demandeur;
    }

    /**
     * @param demandeur utilisateur envoyant la demande
     */
    public void setDemandeur(Utilisateur demandeur) {
        this.demandeur = demandeur;
    }

    /**
     * @return la date d’envoi de la demande
     */
    public long getDateDA() {
        return dateDA;
    }

    /**
     * @param dateDA date d’envoi à définir
     */
    public void setDateDA(long dateDA) {
        this.dateDA = dateDA;
    }

    /**
     * @return le statut de la demande
     */
    public String getStatut() {
        return statut;
    }

    /**
     * @param statut statut à définir (ex: en attente, acceptée, refusée)
     */
    public void setStatut(String statut) {
        this.statut = statut;
    }
}
