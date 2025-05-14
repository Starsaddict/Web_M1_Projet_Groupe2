package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConv;

    private String nomConv;

    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    @OneToMany(mappedBy = "conversation")
    private List<Message> messages;

    @ManyToMany(mappedBy = "conversationsParticipees")
    private List<Utilisateur> participants;

    public Integer getIdConv() {
        return idConv;
    }

    public void setIdConv(Integer idConv) {
        this.idConv = idConv;
    }

    public List<Utilisateur> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Utilisateur> participants) {
        this.participants = participants;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public String getNomConv() {
        return nomConv;
    }

    public void setNomConv(String nomConv) {
        this.nomConv = nomConv;
    }
}
