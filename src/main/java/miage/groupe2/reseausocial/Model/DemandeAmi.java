package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandeAmi")
public class DemandeAmi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_demandeami;
    @ManyToOne
    @JoinColumn(name = "numUDemandeur")
    private Utilisateur demandeur;
    @ManyToOne
    @JoinColumn(name = "numUReceveur")
    private Utilisateur receveur;
    private String statut; // "EN_ATTENTE", "ACCEPTEE", "REFUSEE"
    private long dateDA;
}