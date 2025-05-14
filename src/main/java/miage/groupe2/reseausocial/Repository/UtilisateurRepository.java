package miage.groupe2.reseausocial.Repository;


import miage.groupe2.reseausocial.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    public Utilisateur findByEmailU(String email);

    public Utilisateur findByidUti(long id);
}
