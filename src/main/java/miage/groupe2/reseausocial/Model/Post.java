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

    public Post(Integer idPost, List<Utilisateur> utilisateursRepost, List<Reaction> reactions, Utilisateur createur, List<Commentaire> commentaires, LocalDate datePost, String textePost) {
        this.idPost = idPost;
        this.utilisateursRepost = utilisateursRepost;
        this.reactions = reactions;
        this.createur = createur;
        this.commentaires = commentaires;
        this.datePost = datePost;
        this.textePost = textePost;
    }

    public Post() {

    }

    public List<Utilisateur> getUtilisateursRepost() {
        return utilisateursRepost;
    }

    public void setUtilisateursRepost(List<Utilisateur> utilisateursRepost) {
        this.utilisateursRepost = utilisateursRepost;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public LocalDate getDatePost() {
        return datePost;
    }

    public void setDatePost(LocalDate datePost) {
        this.datePost = datePost;
    }

    public String getTextePost() {
        return textePost;
    }

    public void setTextePost(String textePost) {
        this.textePost = textePost;
    }

    public Integer getIdPost() {
        return idPost;
    }

    public void setIdPost(Integer idPost) {
        this.idPost = idPost;
    }
}
