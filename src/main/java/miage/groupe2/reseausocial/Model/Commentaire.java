package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCom;

    private String textCom;
    private String dateC;

    @ManyToOne
    @JoinColumn(name = "idUti")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "idPost")
    private Post post;

    public Commentaire(Integer idCom, Post post, Utilisateur utilisateur, String dateC, String textCom) {
        this.idCom = idCom;
        this.post = post;
        this.utilisateur = utilisateur;
        this.dateC = dateC;
        this.textCom = textCom;
    }

    public Commentaire() {

    }

    public Integer getIdCom() {
        return idCom;
    }

    public void setIdCom(Integer idCom) {
        this.idCom = idCom;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getDateC() {
        return dateC;
    }

    public void setDateC(String dateC) {
        this.dateC = dateC;
    }

    public String getTextCom() {
        return textCom;
    }

    public void setTextCom(String textCom) {
        this.textCom = textCom;
    }
}
