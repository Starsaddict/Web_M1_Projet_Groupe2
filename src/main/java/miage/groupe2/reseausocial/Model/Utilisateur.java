package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;


import java.util.List;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numUtilisateur;

    private String nomUtilisateur;
    private String prenomUtilisateur;
    private String emailUtilisateur;
    private String passwordUtilisateur;


    @ManyToMany(mappedBy = "participants")
    private List<ConPerso> conPersos;

    @ManyToMany(mappedBy = "membres")
    private List<Groupe> groupes;

    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "organisateur", cascade = CascadeType.ALL)
    private List<Evenement> evenementsCrees;

    @ManyToMany(mappedBy = "participants")
    private List<Evenement> evenements;

    @ManyToMany
    @JoinTable(
            name = "etreAmi",
            joinColumns = @JoinColumn(name = "numUtilisateur"),
            inverseJoinColumns = @JoinColumn(name = "numAmi")
    )
    private List<Utilisateur> amis;

    @ManyToMany(mappedBy = "utilisateursRepost")
    private List<Post> repostes;

    public Utilisateur() {
    }

    public Long getNumUtilisateur() {
        return numUtilisateur;
    }

    public void setNumUtilisateur(Long numUtilisateur) {
        this.numUtilisateur = numUtilisateur;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getPrenomUtilisateur() {
        return prenomUtilisateur;
    }

    public void setPrenomUtilisateur(String prenomUtilisateur) {
        this.prenomUtilisateur = prenomUtilisateur;
    }

    public String getEmailUtilisateur() {
        return emailUtilisateur;
    }

    public void setEmailUtilisateur(String emailUtilisateur) {
        this.emailUtilisateur = emailUtilisateur;
    }

    public String getPasswordUtilisateur() {
        return passwordUtilisateur;
    }

    public void setPasswordUtilisateur(String passwordUtilisateur) {
        this.passwordUtilisateur = passwordUtilisateur;
    }

    public List<ConPerso> getConPersos() {
        return conPersos;
    }

    public void setConPersos(List<ConPerso> conPersos) {
        this.conPersos = conPersos;
    }

    public List<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(List<Groupe> groupes) {
        this.groupes = groupes;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Evenement> getEvenementsCrees() {
        return evenementsCrees;
    }

    public void setEvenementsCrees(List<Evenement> evenementsCrees) {
        this.evenementsCrees = evenementsCrees;
    }

    public List<Evenement> getEvenements() {
        return evenements;
    }

    public void setEvenements(List<Evenement> evenements) {
        this.evenements = evenements;
    }

    public List<Utilisateur> getAmis() {
        return amis;
    }

    public void setAmis(List<Utilisateur> amis) {
        this.amis = amis;
    }

    public List<Post> getRepostes() {
        return repostes;
    }

    public void setRepostes(List<Post> repostes) {
        this.repostes = repostes;
    }
}
