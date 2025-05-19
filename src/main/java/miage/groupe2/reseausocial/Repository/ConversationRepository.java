package miage.groupe2.reseausocial.Repository;



import jakarta.transaction.Transactional;
import miage.groupe2.reseausocial.Model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 WHERE p1.idUti = :id1 AND p2.idUti = :id2")
    List<Conversation> findConversationBetweenTwoUsers(@Param("id1") Integer id1, @Param("id2") Integer id2);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO participer_conv (id_uti, id_conv) VALUES (:id1, :id2)", nativeQuery = true)
    void ajouterutilisateuralaconversation(@Param("id1") Integer id1, @Param("id2") Integer id2);



}
