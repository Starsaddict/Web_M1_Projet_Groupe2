package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConv;

    private String nomConv;

    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    @OneToMany(mappedBy = "conversation")
    private List<Message> messages;

    @ManyToMany(mappedBy = "conversationsParticipees")
    private List<Utilisateur> participants;
}
