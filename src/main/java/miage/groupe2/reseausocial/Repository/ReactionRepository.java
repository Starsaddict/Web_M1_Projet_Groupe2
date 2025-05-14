package miage.groupe2.reseausocial.Repository;


import miage.groupe2.reseausocial.Model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
}
