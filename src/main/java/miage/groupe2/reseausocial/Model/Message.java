package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMessage;
    private String textMessage;
    private long dateMessage;
    @ManyToOne
    @JoinColumn(name = "numUtilisateur")
    private Utilisateur auteur;
    @ManyToOne
    @JoinColumn(name = "idConv")
    private Conversation conversation;
    private String statut;
}