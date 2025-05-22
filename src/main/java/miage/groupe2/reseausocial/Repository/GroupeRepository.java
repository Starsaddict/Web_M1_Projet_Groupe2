package miage.groupe2.reseausocial.Repository;


import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupeRepository extends JpaRepository<Groupe, Integer> {


    Groupe findGroupeByidGrp(int id);

    List<Groupe> findAll();


}
