package miage.groupe2.reseausocial.Repository;


import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Reaction;
import miage.groupe2.reseausocial.Model.Utilisateur;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReactionRepository extends JpaRepository<Reaction, Integer> {

    List<Reaction> findByPost(Post post);

    List<Reaction> findByPostAndType(Post post, String type);

    Reaction findByPostAndUtilisateur(Post post, Utilisateur utilisateur);

    void deleteByPostAndUtilisateur(Post post, Utilisateur utilisateur);


}
