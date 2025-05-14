package miage.groupe2.reaseausocial.Model;

@Entity
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idrea;

    private String type;

    @ManyToOne
    @JoinColumn(name = "idUti")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "idPost")
    private Post post;
}
