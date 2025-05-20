package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Conversation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConv;

    private String nomConv;

    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    @OneToMany(mappedBy = "conversation")
    private List<Message> messages;

    @ManyToMany
    @JoinTable(
        name = "participer_conv",
        joinColumns = @JoinColumn(name = "id_conv"),
        inverseJoinColumns = @JoinColumn(name = "id_uti")
    )
    private List<Utilisateur> participants;

    private boolean estconversationDeGroupe = false;

    public Conversation() {}

    public Conversation(String nomConv, Integer idConv, Utilisateur createur, List<Message> messages, List<Utilisateur> participants, boolean estconversationDeGroupe) {
        this.nomConv = nomConv;
        this.idConv = idConv;
        this.createur = createur;
        this.messages = messages;
        this.participants = participants;
        this.estconversationDeGroupe = estconversationDeGroupe;
    }

    public Integer getIdConv() {
        return idConv;
    }

    public void setIdConv(Integer idConv) {
        this.idConv = idConv;
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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Utilisateur> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Utilisateur> participants) {
        this.participants = participants;
    }

    public boolean isEstconversationDeGroupe() {
        return estconversationDeGroupe;
    }

    public void setEstconversationDeGroupe(boolean estconversationDeGroupe) {
        this.estconversationDeGroupe = estconversationDeGroupe;
    }
}
