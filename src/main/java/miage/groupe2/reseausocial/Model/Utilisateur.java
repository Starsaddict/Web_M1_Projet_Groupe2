package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;


import java.util.List;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numUtilisateur;

    private String nomUtilisateur;
    private String prenomUtilisateur;
    private String emailUtilisateur;
    private String passwordUtilisateur;


    @ManyToMany(mappedBy = "participants")
    private List<ConPerso> conPersos;

    @ManyToMany(mappedBy = "membres")
    private List<Groupe> groupes;

    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "organisateur", cascade = CascadeType.ALL)
    private List<Evenement> evenementsCrees;

    @ManyToMany(mappedBy = "participants")
    private List<Evenement> evenements;

    @ManyToMany
    @JoinTable(
            name = "etreAmi",
            joinColumns = @JoinColumn(name = "numUtilisateur"),
            inverseJoinColumns = @JoinColumn(name = "numAmi")
    )
    private List<Utilisateur> amis;


}
