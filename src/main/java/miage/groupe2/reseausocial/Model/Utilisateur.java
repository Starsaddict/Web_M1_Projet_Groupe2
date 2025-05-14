package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUti;

    private String nomU;
    private String prenomU;
    private String emailU;
    private String mdpU;

    @OneToMany(mappedBy = "createur")
    private List<Post> posts;

    @OneToMany(mappedBy = "createur")
    private List<Evenement> evenements;

    @OneToMany(mappedBy = "createur")
    private List<Groupe> groupes;

    @OneToMany(mappedBy = "createur")
    private List<Conversation> conversations;

    @OneToMany(mappedBy = "utilisateur")
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "utilisateur")
    private List<Reaction> reactions;

    @OneToMany(mappedBy = "expediteur")
    private List<Message> messages;

    @OneToMany(mappedBy = "demandeur")
    private List<DemandeAmi> demandesEnvoyees;

    @OneToMany(mappedBy = "recepteur")
    private List<DemandeAmi> demandesRecues;

    @ManyToMany
    @JoinTable(
            name = "appartenirGrp",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idGrp")
    )
    private List<Groupe> groupesAppartenance;

    @ManyToMany
    @JoinTable(
            name = "assisterEvent",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idEve")
    )
    private List<Evenement> evenementsAssistes;

    @ManyToMany
    @JoinTable(
            name = "etre_ami",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idUti_1")
    )
    private List<Utilisateur> amis;

    @ManyToMany(mappedBy = "amis")
    private List<Utilisateur> amisDe;

    @ManyToMany
    @JoinTable(
            name = "reposterPost",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idPost")
    )
    private List<Post> postsRepostes;

    @ManyToMany
    @JoinTable(
            name = "participerConv",
            joinColumns = @JoinColumn(name = "idUti"),
            inverseJoinColumns = @JoinColumn(name = "idConv")
    )
    private List<Conversation> conversationsParticipees;

    public Utilisateur(Integer idUti, List<Conversation> conversationsParticipees, List<Post> postsRepostes, List<Utilisateur> amisDe, List<Utilisateur> amis, List<Evenement> evenementsAssistes, List<Groupe> groupesAppartenance, List<DemandeAmi> demandesRecues, List<DemandeAmi> demandesEnvoyees, List<Message> messages, List<Reaction> reactions, List<Commentaire> commentaires, List<Conversation> conversations, List<Groupe> groupes, List<Evenement> evenements, List<Post> posts, String mdpU, String emailU, String prenomU, String nomU) {
        this.idUti = idUti;
        this.conversationsParticipees = conversationsParticipees;
        this.postsRepostes = postsRepostes;
        this.amisDe = amisDe;
        this.amis = amis;
        this.evenementsAssistes = evenementsAssistes;
        this.groupesAppartenance = groupesAppartenance;
        this.demandesRecues = demandesRecues;
        this.demandesEnvoyees = demandesEnvoyees;
        this.messages = messages;
        this.reactions = reactions;
        this.commentaires = commentaires;
        this.conversations = conversations;
        this.groupes = groupes;
        this.evenements = evenements;
        this.posts = posts;
        this.mdpU = mdpU;
        this.emailU = emailU;
        this.prenomU = prenomU;
        this.nomU = nomU;
    }

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

    public List<Evenement> getEvenements() {
        return evenements;
    }

    public void setEvenements(List<Evenement> evenements) {
        this.evenements = evenements;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
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

    public List<Utilisateur> getAmisDe() {
        return amisDe;
    }

    public void setAmisDe(List<Utilisateur> amisDe) {
        this.amisDe = amisDe;
    }

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
}
