package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "commentaire")
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCom;
    private String textCom;
    private long dateC;

    @ManyToOne
    @JoinColumn(name = "numUtilisateur")
    private Utilisateur auteur;
    @ManyToOne
    @JoinColumn(name="idPost")
    private Post post;
}