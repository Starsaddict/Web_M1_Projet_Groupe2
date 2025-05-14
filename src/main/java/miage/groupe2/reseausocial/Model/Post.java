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

    public Post() {
    }

    public Long getIdPost() {
        return idPost;
    }

    public void setIdPost(Long idPost) {
        this.idPost = idPost;
    }

    public String getTitrePost() {
        return titrePost;
    }

    public void setTitrePost(String titrePost) {
        this.titrePost = titrePost;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContenuPoste() {
        return contenuPoste;
    }

    public void setContenuPoste(String contenuPoste) {
        this.contenuPoste = contenuPoste;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public void setAuteur(Utilisateur auteur) {
        this.auteur = auteur;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public List<Utilisateur> getUtilisateursRepost() {
        return utilisateursRepost;
    }

    public void setUtilisateursRepost(List<Utilisateur> utilisateursRepost) {
        this.utilisateursRepost = utilisateursRepost;
    }
}
