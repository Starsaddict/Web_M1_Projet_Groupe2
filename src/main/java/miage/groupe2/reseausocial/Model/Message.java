package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMsg;

    private String textM;
    private LocalDateTime dateM;

    @ManyToOne
    @JoinColumn(name = "idConv")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "idUti")
    private Utilisateur expediteur;

    public Message(Integer idMsg, Utilisateur expediteur, Conversation conversation, LocalDateTime dateM, String textM) {
        this.idMsg = idMsg;
        this.expediteur = expediteur;
        this.conversation = conversation;
        this.dateM = dateM;
        this.textM = textM;
    }

    public Message() {

    }

    public Integer getIdMsg() {
        return idMsg;
    }

    public void setIdMsg(Integer idMsg) {
        this.idMsg = idMsg;
    }

    public Utilisateur getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(Utilisateur expediteur) {
        this.expediteur = expediteur;
    }

    public LocalDateTime getDateM() {
        return dateM;
    }

    public void setDateM(LocalDateTime dateM) {
        this.dateM = dateM;
    }

    public String getTextM() {
        return textM;
    }

    public void setTextM(String textM) {
        this.textM = textM;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
