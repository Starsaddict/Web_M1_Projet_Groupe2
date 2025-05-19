package miage.groupe2.reseausocial.Repository;

import miage.groupe2.reseausocial.Model.Conversation;
import miage.groupe2.reseausocial.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Trouver tous les messages d'une conversation, triés par date croissante
    List<Message> findByConversationOrderByDateMAsc(Conversation conversation);

}
