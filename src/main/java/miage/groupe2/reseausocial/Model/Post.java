package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

/**
 * Représente un post créé par un utilisateur dans le réseau social.
 * Un post contient du texte, une date de publication, des commentaires, des réactions et des reposts.
 */
@Entity
public class Post {

    /**
     * Identifiant unique du post.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPost;

    public String getTitrePost() {
        return titrePost;
    }

    public void setTitrePost(String titrePost) {
        this.titrePost = titrePost;
    }

    /**
     * Texte du post.
     */
    private String titrePost;

    private String textePost;


    @Basic(fetch = FetchType.LAZY)
    @Column(name = "imagePost")
    private byte[] imagePost;

    /**
     * Date de publication du post.
     */
    @Column(name = "datePost")
    private long datePost;

    /**
     * Utilisateur ayant créé le post.
     */
    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    /**
     * Liste des commentaires associés au post.
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires;

    /**
     * Liste des réactions associées au post.
     */

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions;

    /**
     * Liste des utilisateurs ayant reposté ce post.
     */
    @ManyToMany(mappedBy = "postsRepostes")
    private List<Utilisateur> utilisateursRepost;

    @ManyToOne
    @JoinColumn(name = "id_grp")
    private Groupe groupe;


//    /**
//     * Constructeur avec paramètres.
//     *
//     * @param idPost identifiant du post
//     * @param utilisateursRepost liste des utilisateurs ayant reposté le post
//     * @param reactions liste des réactions associées au post
//     * @param createur utilisateur ayant créé le post
//     * @param commentaires liste des commentaires associés au post
//     * @param datePost date de publication du post
//     * @param textePost texte du post
//     */
//    public Post(Integer idPost, List<Utilisateur> utilisateursRepost, List<Reaction> reactions, Utilisateur createur, List<Commentaire> commentaires, long datePost, String textePost) {
//        this.idPost = idPost;
//        this.utilisateursRepost = utilisateursRepost;
//        this.reactions = reactions;
//        this.createur = createur;
//        this.commentaires = commentaires;
//        this.datePost = datePost;
//        this.textePost = textePost;
//    }

    /**
     * Constructeur par défaut requis par JPA.
     */
    public Post() {

    }

    /**
     * @return la liste des utilisateurs ayant reposté ce post
     */
    public List<Utilisateur> getUtilisateursRepost() {
        return utilisateursRepost;
    }

    /**
     * @param utilisateursRepost liste des utilisateurs ayant reposté ce post à définir
     */
    public void setUtilisateursRepost(List<Utilisateur> utilisateursRepost) {
        this.utilisateursRepost = utilisateursRepost;
    }

    /**
     * @return la liste des réactions associées au post
     */
    public List<Reaction> getReactions() {
        return reactions;
    }

    /**
     * @param reactions liste des réactions à associer au post
     */
    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    /**
     * @return la liste des commentaires associés au post
     */
    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    /**
     * @param commentaires liste des commentaires à associer au post
     */
    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    /**
     * @return l'utilisateur ayant créé le post
     */
    public Utilisateur getCreateur() {
        return createur;
    }

    /**
     * @param createur utilisateur à définir comme créateur du post
     */
    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    /**
     * @return la date de publication du post
     */
    public long getDatePost() {
        return datePost;
    }

    /**
     * @param datePost date de publication du post à définir
     */
    public void setDatePost(long datePost) {
        this.datePost = datePost;
    }

    /**
     * @return le texte du post
     */
    public String getTextePost() {
        return textePost;
    }

    /**
     * @param textePost texte du post à définir
     */
    public void setTextePost(String textePost) {
        this.textePost = textePost;
    }

    /**
     * @return l'identifiant du post
     */
    public Integer getIdPost() {
        return idPost;
    }

    /**
     * @param idPost identifiant du post à définir
     */
    public void setIdPost(Integer idPost) {
        this.idPost = idPost;
    }

    public byte[] getImagePost() {
        return imagePost;
    }

    public void setImagePost(byte[] imagePost) {
        this.imagePost = imagePost;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(getIdPost(), post.getIdPost());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdPost());
    }
}
