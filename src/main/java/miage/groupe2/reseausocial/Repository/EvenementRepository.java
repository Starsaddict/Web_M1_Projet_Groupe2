package miage.groupe2.reseausocial.Repository;


import miage.groupe2.reseausocial.Model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Integer> {
}
