package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCom;

    private String textCom;
    private String dateC;

    @ManyToOne
    @JoinColumn(name = "idUti")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "idPost")
    private Post post;
}
