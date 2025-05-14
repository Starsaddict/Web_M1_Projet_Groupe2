package miage.groupe2.reseausocial;

import jakarta.persistence.*;

@Entity
public class DemandeAmi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDA;

    private String statut;
    private String dateDA;

    @ManyToOne
    @JoinColumn(name = "idUtiDemandeur")
    private Utilisateur demandeur;

    @ManyToOne
    @JoinColumn(name = "idUtiRecepteur")
    private Utilisateur recepteur;
}

