package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idrea;

    private String type;

    @ManyToOne
    @JoinColumn(name = "idUti")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "idPost")
    private Post post;
}
