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

    public Reaction(Post post, Utilisateur utilisateur, String type, Integer idrea) {
        this.post = post;
        this.utilisateur = utilisateur;
        this.type = type;
        this.idrea = idrea;
    }

    public Reaction() {

    }

    public Integer getIdrea() {
        return idrea;
    }

    public void setIdrea(Integer idrea) {
        this.idrea = idrea;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
