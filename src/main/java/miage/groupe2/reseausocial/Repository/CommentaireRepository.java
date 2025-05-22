package miage.groupe2.reseausocial.Repository;



import miage.groupe2.reseausocial.Model.Commentaire;
import miage.groupe2.reseausocial.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Integer> {

}
