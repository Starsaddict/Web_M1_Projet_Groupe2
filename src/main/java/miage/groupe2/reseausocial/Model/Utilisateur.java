package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

/**
 * Représente un utilisateur dans le réseau social.
 * Contient toutes les informations et associations relatives à un utilisateur,
 * y compris ses posts, groupes, conversations, amis, etc.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    /**
     * Identifiant unique de l'utilisateur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUti;

    /**
     * Nom de l'utilisateur.
     */
    private String nomU;

    /**
     * Prénom de l'utilisateur.
     */
    private String prenomU;

    /**
     * Adresse email de l'utilisateur.
     */
    private String emailU;

    /**
     * Mot de passe de l'utilisateur.
     */
    private String mdpU;

    /**
     * Liste des posts créés par l'utilisateur.
     */
    @OneToMany(mappedBy = "createur")
    private List<Post> posts;

    /**
     * Liste des événements créés par l'utilisateur.
     */
    @OneToMany(mappedBy = "createur")
    private List<Evenement> evenements;

    /**
     * Liste des groupes créés par l'utilisateur.
     */
    @OneToMany(mappedBy = "createur")
    private List<Groupe> groupes;

    /**
     * Liste des conversations créées par l'utilisateur.
     */
    @OneToMany(mappedBy = "createur")
    private List<Conversation> conversations;

    /**
     * Liste des commentaires créés par l'utilisateur.
     */
    @OneToMany(mappedBy = "utilisateur")
    private List<Commentaire> commentaires;

    /**
     * Liste des réactions de l'utilisateur aux posts.
     */
    @OneToMany(mappedBy = "utilisateur")
    private List<Reaction> reactions;

    /**
     * Liste des messages envoyés par l'utilisateur.
     */
    @OneToMany(mappedBy = "expediteur")
    private List<Message> messages;

    /**
     * Liste des demandes d'amitié envoyées par l'utilisateur.
     */
    @OneToMany(mappedBy = "demandeur")
    private List<DemandeAmi> demandesEnvoyees;

    /**
     * Liste des demandes d'amitié reçues par l'utilisateur.
     */
    @OneToMany(mappedBy = "recepteur")
    private List<DemandeAmi> demandesRecues;

    /**
     * Liste des groupes auxquels l'utilisateur appartient.
     */
    @ManyToMany
    @JoinTable(
            name = "appartenirGrp",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idGrp")
    )
    private List<Groupe> groupesAppartenance;

    /**
     * Liste des événements auxquels l'utilisateur a assisté.
     */
    @ManyToMany
    @JoinTable(
            name = "assisterEvent",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idEve")
    )
    private List<Evenement> evenementsAssistes;

    /**
     * Liste des utilisateurs qui sont amis avec l'utilisateur.
     */
    @ManyToMany
    @JoinTable(
            name = "etre_ami",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idUti_1")
    )
    private List<Utilisateur> amis;

//    /**
//     * Liste des utilisateurs qui considèrent cet utilisateur comme un ami.
//     */
//    @ManyToMany(mappedBy = "amis")
//    private List<Utilisateur> amisDe;

    /**
     * Liste des posts que l'utilisateur a repostés.
     */
    @ManyToMany
    @JoinTable(
            name = "reposterPost",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idPost")
    )
    private List<Post> postsRepostes;

    /**
     * Liste des conversations auxquelles l'utilisateur participe.
     */
    @ManyToMany
    @JoinTable(
            name = "participerConv",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idConv")
    )
    private List<Conversation> conversationsParticipees;


    public Integer getIdUti() {
        return idUti;
    }

    public void setIdUti(Integer idUti) {
        this.idUti = idUti;
    }

    public String getNomU() {
        return nomU;
    }

    public void setNomU(String nomU) {
        this.nomU = nomU;
    }

    public String getPrenomU() {
        return prenomU;
    }

    public void setPrenomU(String prenomU) {
        this.prenomU = prenomU;
    }

    public String getEmailU() {
        return emailU;
    }

    public void setEmailU(String emailU) {
        this.emailU = emailU;
    }

    public String getMdpU() {
        return mdpU;
    }

    public void setMdpU(String mdpU) {
        this.mdpU = mdpU;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Evenement> getEvenements() {
        return evenements;
    }

    public void setEvenements(List<Evenement> evenements) {
        this.evenements = evenements;
    }

    public List<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(List<Groupe> groupes) {
        this.groupes = groupes;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<DemandeAmi> getDemandesEnvoyees() {
        return demandesEnvoyees;
    }

    public void setDemandesEnvoyees(List<DemandeAmi> demandesEnvoyees) {
        this.demandesEnvoyees = demandesEnvoyees;
    }

    public List<DemandeAmi> getDemandesRecues() {
        return demandesRecues;
    }

    public void setDemandesRecues(List<DemandeAmi> demandesRecues) {
        this.demandesRecues = demandesRecues;
    }

    public List<Groupe> getGroupesAppartenance() {
        return groupesAppartenance;
    }

    public void setGroupesAppartenance(List<Groupe> groupesAppartenance) {
        this.groupesAppartenance = groupesAppartenance;
    }

    public List<Evenement> getEvenementsAssistes() {
        return evenementsAssistes;
    }

    public void setEvenementsAssistes(List<Evenement> evenementsAssistes) {
        this.evenementsAssistes = evenementsAssistes;
    }

    public List<Utilisateur> getAmis() {
        return amis;
    }

    public void setAmis(List<Utilisateur> amis) {
        this.amis = amis;
    }

//    public List<Utilisateur> getAmisDe() {
//        return amisDe;
//    }
//
//    public void setAmisDe(List<Utilisateur> amisDe) {
//        this.amisDe = amisDe;
//    }

    public List<Post> getPostsRepostes() {
        return postsRepostes;
    }

    public void setPostsRepostes(List<Post> postsRepostes) {
        this.postsRepostes = postsRepostes;
    }

    public List<Conversation> getConversationsParticipees() {
        return conversationsParticipees;
    }

    public void setConversationsParticipees(List<Conversation> conversationsParticipees) {
        this.conversationsParticipees = conversationsParticipees;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(getIdUti(), that.getIdUti());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdUti());
    }

}
