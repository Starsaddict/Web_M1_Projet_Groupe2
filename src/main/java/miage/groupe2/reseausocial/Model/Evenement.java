package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "evenement")
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEv;
    private String nomEv;
    private String descriptionEv;
    private long dateEv;
    @ManyToOne
    @JoinColumn(name = "numUtilisateur")
    private Utilisateur organisateur;
    @ManyToMany
    @JoinTable(
            name = "assisterEvent",
            joinColumns = @JoinColumn(name = "idEv"),
            inverseJoinColumns = @JoinColumn(name = "numUtilisateur")
    )
    private List<Utilisateur> participants;

    public Evenement() {
    }

    public Long getIdEv() {
        return idEv;
    }

    public void setIdEv(Long idEv) {
        this.idEv = idEv;
    }

    public String getNomEv() {
        return nomEv;
    }

    public void setNomEv(String nomEv) {
        this.nomEv = nomEv;
    }

    public String getDescriptionEv() {
        return descriptionEv;
    }

    public void setDescriptionEv(String descriptionEv) {
        this.descriptionEv = descriptionEv;
    }

    public long getDateEv() {
        return dateEv;
    }

    public void setDateEv(long dateEv) {
        this.dateEv = dateEv;
    }

    public Utilisateur getOrganisateur() {
        return organisateur;
    }

    public void setOrganisateur(Utilisateur organisateur) {
        this.organisateur = organisateur;
    }

    public List<Utilisateur> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Utilisateur> participants) {
        this.participants = participants;
    }
}