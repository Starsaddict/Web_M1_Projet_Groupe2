package miage.groupe2.reseausocial;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUti;

    private String nomU;
    private String prenomU;
    private String emailU;
    private String mdpU;

    @OneToMany(mappedBy = "createur")
    private List<Post> posts;

    @OneToMany(mappedBy = "createur")
    private List<Evenement> evenements;

    @OneToMany(mappedBy = "createur")
    private List<Groupe> groupes;

    @OneToMany(mappedBy = "createur")
    private List<Conversation> conversations;

    @OneToMany(mappedBy = "utilisateur")
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "utilisateur")
    private List<Reaction> reactions;

    @OneToMany(mappedBy = "expediteur")
    private List<Message> messages;

    @OneToMany(mappedBy = "demandeur")
    private List<DemandeAmi> demandesEnvoyees;

    @OneToMany(mappedBy = "recepteur")
    private List<DemandeAmi> demandesRecues;

    @ManyToMany
    @JoinTable(
            name = "appartenirGrp",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idGrp")
    )
    private List<Groupe> groupesAppartenance;

    @ManyToMany
    @JoinTable(
            name = "assisterEvent",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idEve")
    )
    private List<Evenement> evenementsAssistes;

    @ManyToMany
    @JoinTable(
            name = "etre_ami",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idUti_1")
    )
    private List<Utilisateur> amis;

    @ManyToMany(mappedBy = "amis")
    private List<Utilisateur> amisDe;

    @ManyToMany
    @JoinTable(
            name = "reposterPost",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idPost")
    )
    private List<Post> postsRepostes;

    @ManyToMany
    @JoinTable(
            name = "participerConv",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idConv")
    )
    private List<Conversation> conversationsParticipees;
}
