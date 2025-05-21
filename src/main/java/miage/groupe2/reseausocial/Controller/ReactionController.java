package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Reaction;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.ReactionRepository;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller Spring MVC pour gérer les réactions des utilisateurs sur les posts.
 *
 * <p>
 * Cette classe expose les endpoints REST permettant de créer, modifier ou supprimer
 * des réactions sur les publications dans le réseau social.
 * </p>
 *
 * <p>
 * Elle utilise les services {@link PostService} et {@link UtilisateurService} pour
 * accéder aux données métier des posts et des utilisateurs, ainsi que
 * {@link ReactionRepository} pour persister les réactions.
 * </p>
 *
 * @author
 * @version 1.0
 */
@Controller
public class ReactionController {

    /**
     * Repository JPA pour la gestion des entités Reaction en base de données.
     */
    @Autowired
    private ReactionRepository reactionRepository;

    /**
     * Service métier pour la gestion des publications (posts).
     */
    @Autowired
    private PostService postService;

    /**
     * Service métier pour la gestion des utilisateurs.
     */
    @Autowired
    private UtilisateurService utilisateurService;


}