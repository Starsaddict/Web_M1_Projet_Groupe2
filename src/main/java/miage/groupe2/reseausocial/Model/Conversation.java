package miage.groupe2.reseausocial.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConv;
    private LocalDateTime timestamp;
}