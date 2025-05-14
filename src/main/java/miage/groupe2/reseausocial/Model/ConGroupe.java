package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
public class ConGroupe extends Conversation {

    @OneToOne
    @JoinColumn(name = "idGroupe")
    private Groupe groupe;

    @Column(name = "nomGroupe")
    private String nomGroupe;

    public ConGroupe() {}

    public String getNomGroupe() {
        return nomGroupe;
    }

    public void setNomGroupe(String nomGroupe) {
        this.nomGroupe = nomGroupe;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }
}