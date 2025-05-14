package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPost;

    private String textePost;
    private LocalDate datePost;

    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    @OneToMany(mappedBy = "post")
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "post")
    private List<Reaction> reactions;

    @ManyToMany(mappedBy = "postsRepostes")
    private List<Utilisateur> utilisateursRepost;
}
