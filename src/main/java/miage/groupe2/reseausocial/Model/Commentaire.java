package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

/**
 * Représente un commentaire laissé par un utilisateur sur un post.
 */
@Entity
public class Commentaire {

    /**
     * Identifiant unique du commentaire.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCom;

    /**
     * Texte du commentaire.
     */
    private String textCom;

    /**
     * Date à laquelle le commentaire a été posté.
     */
    private long dateC;

    /**
     * Utilisateur ayant rédigé le commentaire.
     */
    @ManyToOne
    @JoinColumn(name = "idUti")
    private Utilisateur utilisateur;

    /**
     * Post sur lequel le commentaire a été publié.
     */
    @ManyToOne
    @JoinColumn(name = "idPost")
    private Post post;

    /**
     * Constructeur avec paramètres.
     *
     * @param idCom       identifiant du commentaire
     * @param post        post associé au commentaire
     * @param utilisateur auteur du commentaire
     * @param dateC       date du commentaire
     * @param textCom     contenu du commentaire
     */
    public Commentaire(Integer idCom, Post post, Utilisateur utilisateur, long dateC, String textCom) {
        this.idCom = idCom;
        this.post = post;
        this.utilisateur = utilisateur;
        this.dateC = dateC;
        this.textCom = textCom;
    }

    /**
     * Constructeur sans paramètres requis par JPA.
     */
    public Commentaire() {
    }

    /**
     * @return l'identifiant du commentaire
     */
    public Integer getIdCom() {
        return idCom;
    }

    /**
     * @param idCom identifiant du commentaire
     */
    public void setIdCom(Integer idCom) {
        this.idCom = idCom;
    }

    /**
     * @return le post associé au commentaire
     */
    public Post getPost() {
        return post;
    }

    /**
     * @param post post associé
     */
    public void setPost(Post post) {
        this.post = post;
    }

    /**
     * @return l'utilisateur ayant rédigé le commentaire
     */
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    /**
     * @param utilisateur auteur du commentaire
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    /**
     * @return la date du commentaire
     */
    public long getDateC() {
        return dateC;
    }

    /**
     * @param dateC date du commentaire
     */
    public void setDateC(long dateC) {
        this.dateC = dateC;
    }

    /**
     * @return le texte du commentaire
     */
    public String getTextCom() {
        return textCom;
    }

    /**
     * @param textCom contenu du commentaire
     */
    public void setTextCom(String textCom) {
        this.textCom = textCom;
    }
}
