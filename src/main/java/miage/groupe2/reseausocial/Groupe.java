package miage.groupe2.reseausocial;

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
}
