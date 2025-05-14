package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "commentaire")
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCom;
    private String textCom;
    private long dateCom;

    @ManyToOne
    @JoinColumn(name = "numUtilisateur")
    private Utilisateur auteur;
    @ManyToOne
    @JoinColumn(name="idPost")
    private Post post;

    public Commentaire() {}

    public Long getIdCom() {
        return idCom;
    }

    public void setIdCom(Long idCom) {
        this.idCom = idCom;
    }

    public String getTextCom() {
        return textCom;
    }

    public void setTextCom(String textCom) {
        this.textCom = textCom;
    }

    public long getDateCom() {
        return dateCom;
    }

    public void setDateCom(long dateCom) {
        this.dateCom = dateCom;
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