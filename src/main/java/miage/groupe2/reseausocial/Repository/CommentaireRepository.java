package miage.groupe2.reseausocial.Repository;



import miage.groupe2.reseausocial.Model.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentaireRepository extends JpaRepository<Commentaire, Integer> {
}
