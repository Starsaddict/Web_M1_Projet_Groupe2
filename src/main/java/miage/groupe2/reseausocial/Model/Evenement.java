package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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
}