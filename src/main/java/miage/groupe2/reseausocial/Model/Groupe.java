package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGrp;

    private String nomG;
    private String description;
    private LocalDate dateCreation;

    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    @ManyToMany(mappedBy = "groupesAppartenance")
    private List<Utilisateur> membres;

    public Groupe(Integer idGrp, List<Utilisateur> membres, Utilisateur createur, LocalDate dateCreation, String nomG, String description) {
        this.idGrp = idGrp;
        this.membres = membres;
        this.createur = createur;
        this.dateCreation = dateCreation;
        this.nomG = nomG;
        this.description = description;
    }

    public Groupe() {

    }

    public Integer getIdGrp() {
        return idGrp;
    }

    public void setIdGrp(Integer idGrp) {
        this.idGrp = idGrp;
    }

    public List<Utilisateur> getMembres() {
        return membres;
    }

    public void setMembres(List<Utilisateur> membres) {
        this.membres = membres;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNomG() {
        return nomG;
    }

    public void setNomG(String nomG) {
        this.nomG = nomG;
    }
}
