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

@Controller
public class ReactionController {

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UtilisateurService utilisateurService;


}
