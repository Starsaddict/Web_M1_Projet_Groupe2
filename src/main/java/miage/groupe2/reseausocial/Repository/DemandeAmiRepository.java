package miage.groupe2.reseausocial.Repository;


import jakarta.transaction.Transactional;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandeAmiRepository extends JpaRepository<DemandeAmi, Integer> {

    public List<DemandeAmi> findByRecepteur(Utilisateur recepteur);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO etre_ami (id_Uti, id_Uti_1) VALUES (:id1, :id2), (:id2, :id1)", nativeQuery = true)
    void ajouterLienAmitie(@Param("id1") Integer id1, @Param("id2") Integer id2);


    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Utilisateur u JOIN u.amis a WHERE u.idUti = :id1 AND a.idUti = :id2")
    boolean sontDejaAmis(@Param("id1") Integer id1, @Param("id2") Integer id2);


    DemandeAmi findByIdDA(Integer idDemande);

    List<DemandeAmi> findByDemandeur(Utilisateur demandeur);
}
