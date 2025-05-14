package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Représente un message envoyé dans une conversation.
 * Un message possède un texte, une date d'envoi, un expéditeur et une conversation associée.
 */
@Entity
public class Message {

    /**
     * Identifiant unique du message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMsg;

    /**
     * Contenu textuel du message.
     */
    private String textM;

    /**
     * Date et heure d'envoi du message.
     */
    private LocalDateTime dateM;

    /**
     * Conversation à laquelle appartient le message.
     */
    @ManyToOne
    @JoinColumn(name = "idConv")
    private Conversation conversation;

    /**
     * Utilisateur ayant envoyé le message.
     */
    @ManyToOne
    @JoinColumn(name = "idUti")
    private Utilisateur expediteur;

    /**
     * Constructeur avec paramètres.
     *
     * @param idMsg identifiant du message
     * @param expediteur utilisateur ayant envoyé le message
     * @param conversation conversation à laquelle appartient le message
     * @param dateM date et heure d'envoi du message
     * @param textM contenu textuel du message
     */
    public Message(Integer idMsg, Utilisateur expediteur, Conversation conversation, LocalDateTime dateM, String textM) {
        this.idMsg = idMsg;
        this.expediteur = expediteur;
        this.conversation = conversation;
        this.dateM = dateM;
        this.textM = textM;
    }

    /**
     * Constructeur par défaut requis par JPA.
     */
    public Message() {

    }

    /**
     * @return l’identifiant du message
     */
    public Integer getIdMsg() {
        return idMsg;
    }

    /**
     * @param idMsg identifiant du message à définir
     */
    public void setIdMsg(Integer idMsg) {
        this.idMsg = idMsg;
    }

    /**
     * @return l’expéditeur du message
     */
    public Utilisateur getExpediteur() {
        return expediteur;
    }

    /**
     * @param expediteur utilisateur à définir comme expéditeur
     */
    public void setExpediteur(Utilisateur expediteur) {
        this.expediteur = expediteur;
    }

    /**
     * @return la date et l’heure d’envoi du message
     */
    public LocalDateTime getDateM() {
        return dateM;
    }

    /**
     * @param dateM date et heure d’envoi du message à définir
     */
    public void setDateM(LocalDateTime dateM) {
        this.dateM = dateM;
    }

    /**
     * @return le contenu textuel du message
     */
    public String getTextM() {
        return textM;
    }

    /**
     * @param textM contenu textuel à définir pour le message
     */
    public void setTextM(String textM) {
        this.textM = textM;
    }

    /**
     * @return la conversation à laquelle appartient le message
     */
    public Conversation getConversation() {
        return conversation;
    }

    /**
     * @param conversation conversation à définir pour le message
     */
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
