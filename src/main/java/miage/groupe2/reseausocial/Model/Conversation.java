package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Représente une conversation à plusieurs utilisateurs.
 * Une conversation peut contenir plusieurs messages et plusieurs participants.
 */
@Entity
public class Conversation {

    /**
     * Identifiant unique de la conversation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConv;

    /**
     * Nom de la conversation.
     */
    private String nomConv;

    /**
     * Utilisateur ayant créé la conversation.
     */
    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    /**
     * Liste des messages échangés dans la conversation.
     */
    @OneToMany(mappedBy = "conversation")
    private List<Message> messages;

    /**
     * Liste des participants à la conversation.
     */
    @ManyToMany
    @JoinTable(
            name = "participer_conv",
            joinColumns = @JoinColumn(name = "id_conv"),
            inverseJoinColumns = @JoinColumn(name = "id_uti")
    )
    private List<Utilisateur> participants;


    private boolean estconversationDeGroupe = false;

    public Conversation() {

    }


    /**
     * @return l'identifiant de la conversation
     */
    public Integer getIdConv() {
        return idConv;
    }

    /**
     * @param idConv identifiant à définir
     */
    public void setIdConv(Integer idConv) {
        this.idConv = idConv;
    }

    /**
     * @return la liste des participants de la conversation
     */
    public List<Utilisateur> getParticipants() {
        return participants;
    }

    /**
     * @param participants liste des participants à associer
     */
    public void setParticipants(List<Utilisateur> participants) {
        this.participants = participants;
    }

    /**
     * @return la liste des messages de la conversation
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * @param messages liste des messages à associer
     */
    public void setMessages(List<Message> messages) {
    }

    public String getNomConv() {
        return nomConv;
    }

    public void setNomConv(String nomConv) {
        this.nomConv = nomConv;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public Conversation(String nomConv, Integer idConv, Utilisateur createur, List<Message> messages, List<Utilisateur> participants, boolean estconversationDeGroupe) {
        this.nomConv = nomConv;
        this.idConv = idConv;
        this.createur = createur;
        this.messages = messages;
        this.participants = participants;
        this.estconversationDeGroupe = estconversationDeGroupe;
    }

    public boolean isEstconversationDeGroupe() {
        return estconversationDeGroupe;
    }

    public void setEstconversationDeGroupe(boolean estconversationDeGroupe) {
        this.estconversationDeGroupe = estconversationDeGroupe;
    }
}