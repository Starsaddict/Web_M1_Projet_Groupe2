package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "demandeAmi")
public class DemandeAmi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDa;
    @ManyToOne
    @JoinColumn(name = "numDemandeur")
    private Utilisateur demandeur;
    @ManyToOne
    @JoinColumn(name = "numReceveur")
    private Utilisateur receveur;
    private String statut; // "EN_ATTENTE", "ACCEPTEE", "REFUSEE"
    private long dateDA;

    public DemandeAmi() {
    }

    public Long getIdDa() {
        return idDa;
    }

    public void setIdDa(Long idDa) {
        this.idDa = idDa;
    }

    public Utilisateur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Utilisateur demandeur) {
        this.demandeur = demandeur;
    }

    public Utilisateur getReceveur() {
        return receveur;
    }

    public void setReceveur(Utilisateur receveur) {
        this.receveur = receveur;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public long getDateDA() {
        return dateDA;
    }

    public void setDateDA(long dateDA) {
        this.dateDA = dateDA;
    }
}