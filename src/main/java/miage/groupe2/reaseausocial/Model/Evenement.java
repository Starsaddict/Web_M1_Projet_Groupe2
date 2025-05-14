package miage.groupe2.reaseausocial.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEve;

    private String nomE;
    private LocalDate dateDebutE;
    private LocalDate dateFinE;
    private String adresseE;

    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    @ManyToMany(mappedBy = "evenementsAssistes")
    private List<Utilisateur> participants;
}
