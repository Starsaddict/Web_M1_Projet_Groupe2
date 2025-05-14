package miage.groupe2.reseausocial.Repository;


import miage.groupe2.reseausocial.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
