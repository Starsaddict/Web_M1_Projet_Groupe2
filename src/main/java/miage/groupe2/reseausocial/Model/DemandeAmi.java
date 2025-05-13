package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandeAmi")
public class DemandeAmi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDa;
    @ManyToOne
    @JoinColumn(name = "numDemandeur")
    private Utilisateur demandeur;
    @ManyToOne
    @JoinColumn(name = "numReceveur")
    private Utilisateur receveur;
    private String statut; // "EN_ATTENTE", "ACCEPTEE", "REFUSEE"
    private long dateDA;
}