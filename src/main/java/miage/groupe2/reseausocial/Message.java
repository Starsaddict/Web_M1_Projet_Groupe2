package miage.groupe2.reseausocial;

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
}
