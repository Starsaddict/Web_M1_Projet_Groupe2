package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "reaction")
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRea;
    private String type;

    @ManyToOne
    @JoinColumn(name = "numUtilisateur")
    private Utilisateur auteur;
    @ManyToOne
    @JoinColumn(name = "idPost")
    private Post post;

}