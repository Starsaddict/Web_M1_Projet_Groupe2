package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class ConPerso extends Conversation {
    @ManyToMany
    @JoinTable(
            name = "discuter",
            joinColumns = @JoinColumn(name = "con_perso_id"),
            inverseJoinColumns = @JoinColumn(name = "etudiant_id")
    )
    private List<Utilisateur> participants;

}