package miage.groupe2.reseausocial.Model;

import java.io.Serializable;

import jakarta.persistence.*;

/**
 * Représente une réaction d'un utilisateur à un post dans le réseau social.
 * Une réaction peut être un "like", "dislike", ou tout autre type de réaction.
 */
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_post", "id_uti"})
)
public class Reaction implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Identifiant unique de la réaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idrea;

    /**
     * Type de la réaction (ex. "like", "dislike", etc.).
     */
    private String type;

    /**
     * Utilisateur ayant créé la réaction.
     */
    @ManyToOne
    @JoinColumn(name = "idUti")
    private Utilisateur utilisateur;

    /**
     * Post auquel la réaction est associée.
     */
    @ManyToOne
    @JoinColumn(name = "idPost")
    private Post post;

    /**
     * Constructeur avec paramètres.
     *
     * @param post post auquel la réaction est associée
     * @param utilisateur utilisateur ayant réagi au post
     * @param type type de la réaction
     * @param idrea identifiant unique de la réaction
     */
    public Reaction(Post post, Utilisateur utilisateur, String type, Integer idrea) {
        this.post = post;
        this.utilisateur = utilisateur;
        this.type = type;
        this.idrea = idrea;
    }

    /**
     * Constructeur par défaut requis par JPA.
     */
    public Reaction() {

    }

    /**
     * @return l'identifiant de la réaction
     */
    public Integer getIdrea() {
        return idrea;
    }

    /**
     * @param idrea identifiant de la réaction à définir
     */
    public void setIdrea(Integer idrea) {
        this.idrea = idrea;
    }

    /**
     * @return le post auquel la réaction est associée
     */
    public Post getPost() {
        return post;
    }

    /**
     * @param post post auquel la réaction est associée à définir
     */
    public void setPost(Post post) {
        this.post = post;
    }

    /**
     * @return le type de la réaction
     */
    public String getType() {
        return type;
    }

    /**
     * @param type type de la réaction à définir
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return l'utilisateur ayant réagi au post
     */
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    /**
     * @param utilisateur utilisateur ayant réagi au post à définir
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
