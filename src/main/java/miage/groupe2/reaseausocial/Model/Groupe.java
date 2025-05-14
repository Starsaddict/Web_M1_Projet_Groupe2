package miage.groupe2.reaseausocial.Model;

@Entity
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGrp;

    private String nomG;
    private String description;
    private LocalDate dateCreation;

    @ManyToOne
    @JoinColumn(name = "idUtiCreateur")
    private Utilisateur createur;

    @ManyToMany(mappedBy = "groupesAppartenance")
    private List<Utilisateur> membres;
}
