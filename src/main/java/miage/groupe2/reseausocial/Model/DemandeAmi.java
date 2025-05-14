package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
public class DemandeAmi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDA;

    private String statut;
    private String dateDA;

    @ManyToOne
    @JoinColumn(name = "idUtiDemandeur")
    private Utilisateur demandeur;

    @ManyToOne
    @JoinColumn(name = "idUtiRecepteur")
    private Utilisateur recepteur;

    public DemandeAmi(Integer idDA, Utilisateur recepteur, Utilisateur demandeur, String dateDA, String statut) {
        this.idDA = idDA;
        this.recepteur = recepteur;
        this.demandeur = demandeur;
        this.dateDA = dateDA;
        this.statut = statut;
    }

    public DemandeAmi() {

    }

    public Integer getIdDA() {
        return idDA;
    }

    public void setIdDA(Integer idDA) {
        this.idDA = idDA;
    }

    public Utilisateur getRecepteur() {
        return recepteur;
    }

    public void setRecepteur(Utilisateur recepteur) {
        this.recepteur = recepteur;
    }

    public Utilisateur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Utilisateur demandeur) {
        this.demandeur = demandeur;
    }

    public String getDateDA() {
        return dateDA;
    }

    public void setDateDA(String dateDA) {
        this.dateDA = dateDA;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}

