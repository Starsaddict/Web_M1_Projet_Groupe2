package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

/**
 * Représente un groupe d’utilisateurs dans le réseau social.
 * Un groupe possède un nom, une description, une date de création, un créateur et des membres.
 */
@Entity
public class Groupe implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Identifiant unique du groupe.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGrp;

    /**
     * Nom du groupe.
     */
    private String nomG;

    /**
     * Description du groupe.
     */
    private String description;

    /**
     * Date de création du groupe.
     */
    private long dateCreation;

    /**
     * Utilisateur ayant créé le groupe.
     */
    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    /**
     * Liste des membres appartenant au groupe.
     */
    @ManyToMany(mappedBy = "groupesAppartenance")
    private List<Utilisateur> membres;

    @OneToMany(mappedBy = "groupe" ,cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Post> posts;

    /**
     * Constructeur avec paramètres.
     *
     * @param idGrp identifiant du groupe
     * @param membres liste des membres du groupe
     * @param createur utilisateur créateur du groupe
     * @param dateCreation date de création du groupe
     * @param nomG nom du groupe
     * @param description description du groupe
     */
//    public Groupe(Integer idGrp, List<Utilisateur> membres, Utilisateur createur, long dateCreation, String nomG, String description) {
//        this.idGrp = idGrp;
//        this.membres = membres;
//        this.createur = createur;
//        this.dateCreation = dateCreation;
//        this.nomG = nomG;
//        this.description = description;
//    }

    /**
     * Constructeur par défaut requis par JPA.
     */
    public Groupe() {
    }

    /**
     * @return l’identifiant du groupe
     */
    public Integer getIdGrp() {
        return idGrp;
    }

    /**
     * @param idGrp identifiant du groupe à définir
     */
    public void setIdGrp(Integer idGrp) {
        this.idGrp = idGrp;
    }

    /**
     * @return la liste des membres du groupe
     */
    public List<Utilisateur> getMembres() {
        return membres;
    }

    /**
     * @param membres liste des membres à définir
     */
    public void setMembres(List<Utilisateur> membres) {
        this.membres = membres;
    }

    /**
     * @return le créateur du groupe
     */
    public Utilisateur getCreateur() {
        return createur;
    }

    /**
     * @param createur créateur à définir
     */
    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }


    /**
     * @return la date de création du groupe
     */
    public long getDateCreation() {
        return dateCreation;
    }

    /**
     * @param dateCreation date de création à définir
     */
    public void setDateCreation(long dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * @return la description du groupe
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description description à définir
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return le nom du groupe
     */
    public String getNomG() {
        return nomG;
    }

    /**
     * @param nomG nom du groupe à définir
     */
    public void setNomG(String nomG) {
        this.nomG = nomG;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
