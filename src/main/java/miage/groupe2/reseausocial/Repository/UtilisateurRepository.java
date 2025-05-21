package miage.groupe2.reseausocial.Repository;


import miage.groupe2.reseausocial.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    public Utilisateur findByEmailU(String email);

    public Utilisateur findByidUti(long id);

    @Query("SELECT u.emailU FROM Utilisateur u")
    public List<String> findAllEmailU();

    List<Utilisateur> findByNomUContainingIgnoreCase(String nom);


    Utilisateur findByIdUti(Integer idUti);
}
