package miage.groupe2.reseausocial.Repository;



import miage.groupe2.reseausocial.Model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
}
