package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "groupe")
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGroupe;
    private String nomGroupe;
    private String descriptionGroupe;
    @ManyToOne
    private Utilisateur createur;
    @ManyToMany
    @JoinTable(
            name = "appartenirGrp",
            joinColumns = @JoinColumn(name = "idGroupe"),
            inverseJoinColumns = @JoinColumn(name = "numUtilisateur")
    )
    private List<Utilisateur> membres;
}