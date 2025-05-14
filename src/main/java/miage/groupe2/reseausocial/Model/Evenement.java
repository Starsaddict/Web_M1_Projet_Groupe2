package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEve;

    private String nomE;
    private LocalDate dateDebutE;
    private LocalDate dateFinE;
    private String adresseE;

    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    @ManyToMany(mappedBy = "evenementsAssistes")
    private List<Utilisateur> participants;

    public Evenement(Integer idEve, List<Utilisateur> participants, Utilisateur createur, String adresseE, LocalDate dateFinE, LocalDate dateDebutE, String nomE) {
        this.idEve = idEve;
        this.participants = participants;
        this.createur = createur;
        this.adresseE = adresseE;
        this.dateFinE = dateFinE;
        this.dateDebutE = dateDebutE;
        this.nomE = nomE;
    }

    public Evenement() {

    }

    public Integer getIdEve() {
        return idEve;
    }

    public void setIdEve(Integer idEve) {
        this.idEve = idEve;
    }

    public List<Utilisateur> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Utilisateur> participants) {
        this.participants = participants;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public String getAdresseE() {
        return adresseE;
    }

    public void setAdresseE(String adresseE) {
        this.adresseE = adresseE;
    }

    public LocalDate getDateFinE() {
        return dateFinE;
    }

    public void setDateFinE(LocalDate dateFinE) {
        this.dateFinE = dateFinE;
    }

    public LocalDate getDateDebutE() {
        return dateDebutE;
    }

    public void setDateDebutE(LocalDate dateDebutE) {
        this.dateDebutE = dateDebutE;
    }

    public String getNomE() {
        return nomE;
    }

    public void setNomE(String nomE) {
        this.nomE = nomE;
    }
}
