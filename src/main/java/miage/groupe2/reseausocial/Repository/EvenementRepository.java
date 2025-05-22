package miage.groupe2.reseausocial.Repository;


import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface EvenementRepository extends JpaRepository<Evenement, Integer> {

    Evenement findByIdEve(Integer idEve);

}
