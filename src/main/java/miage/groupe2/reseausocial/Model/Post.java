package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPost;

    private String titrePost;

    private Long timestamp;

    private String contenuPoste;

    @ManyToOne
    @JoinColumn(name = "numUtilisateur")
    private Utilisateur auteur;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions;

    @ManyToMany
    @JoinTable( name = "reposterPost",
            joinColumns = @JoinColumn(name = "idPost"),
            inverseJoinColumns = @JoinColumn(name = "numUtilisateur"))
    private List<Utilisateur> utilisateursRepost;

}
