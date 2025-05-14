package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "reaction")
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRea;
    private String type;

    @ManyToOne
    @JoinColumn(name = "numUtilisateur")
    private Utilisateur auteur;
    @ManyToOne
    @JoinColumn(name = "idPost")
    private Post post;

    public Reaction() {
    }

    public Long getIdRea() {
        return idRea;
    }

    public void setIdRea(Long idRea) {
        this.idRea = idRea;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public void setAuteur(Utilisateur auteur) {
        this.auteur = auteur;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}