package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "GroupeInteret")
public class GroupeInteret {
    @Id
    @Column(name = "idGrpI")
    private Long idGrpI;

    @Column(name = "nomGrpI")
    private String nomGrpI;

    @Column(name = "description")
    private String description;

    @Column(name = "dateDeCreation")
    private long date;

    @ManyToOne
    @JoinColumn(name = "numUtilisateur")
    private Utilisateur createur;

    @ManyToMany
    @JoinTable(
            name = "appartenirGrpI",
            joinColumns = @JoinColumn(name = "idGrpI"),
            inverseJoinColumns = @JoinColumn(name = "numUtilisateur")
    )
    private List<Utilisateur> participants;
}
