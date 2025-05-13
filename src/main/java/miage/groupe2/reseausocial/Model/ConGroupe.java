package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
public class ConGroupe extends Conversation {

    @OneToOne
    @JoinColumn(name = "idGroupe")
    private Groupe groupe;

    @Column(name = "nomGroupe")
    private String nomGroupe;
}