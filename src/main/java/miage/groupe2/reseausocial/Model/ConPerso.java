package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class ConPerso extends Conversation {
    @ManyToMany
    @JoinTable(
            name = "discuter",
            joinColumns = @JoinColumn(name = "numUtilisateur1"),
            inverseJoinColumns = @JoinColumn(name = "numUtilisateur2")
    )
    private List<Utilisateur> participants;

    public ConPerso() {
    }

    public List<Utilisateur> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Utilisateur> participants) {
        this.participants = participants;
    }
}