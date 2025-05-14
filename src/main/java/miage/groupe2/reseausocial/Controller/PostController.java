package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    UtilisateurRepository utilisateurRepository;

    @Autowired
    PostRepository postRepository;

    @GetMapping("/{id}")
    public String postPersonne(
            @PathVariable long id,
            Model model
    ) {
        Utilisateur user = utilisateurRepository.findByidUti(id);
        List<Post> posts = postRepository.findByUtilisateur(user);
        model.addAttribute("Posts", posts);
        return "listPostsPersonne";
    }

    @GetMapping("/poster/{id}")
    public String CreerPost(){
        return "creerPost";
    }


}
