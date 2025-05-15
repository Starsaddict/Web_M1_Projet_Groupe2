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

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO assisterEvent (idEve, idUti) VALUES (:idEve, :idUti)", nativeQuery = true)
    void addParticipant(@Param("idEve") Integer idEve, @Param("idUti") Integer idUti);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM assisterEvent WHERE idEve = :idEve AND idUti = :idUti", nativeQuery = true)
    void removeParticipant(@Param("idEve") Integer idEve, @Param("idUti") Integer idUti);

    @Query(value = "SELECT COUNT(*) > 0 FROM assisterEvent WHERE idEve = :idEve AND idUti = :idUti", nativeQuery = true)
    boolean isParticipant(@Param("idEve") Integer idEve, @Param("idUti") Integer idUti);

    /**
     * Retourne tous les événements créés par un utilisateur donné.
     *
     * @param createur l'utilisateur créateur des événements
     * @return une liste d'événements créés par cet utilisateur
     */
    List<Evenement> findByCreateur(Utilisateur createur);
}
