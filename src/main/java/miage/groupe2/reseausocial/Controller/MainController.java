package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.DemandeAmiService;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;


@Controller
public class MainController {

    @Autowired
    UtilisateurRepository utilisateurRepository;
    @Autowired
    private PostService postService;
    @Autowired
    DemandeAmiService demandeAmiService;
    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping("/home")
    public String homepage(
            Model model,
            HttpSession session
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        if (user == null) {
            return "redirect:/auth/login";
        }

        Hibernate.initialize(user.getPostsRepostes());
        List<Post> posts = postService.listPostFriends(session);
        posts = posts.stream().limit(10).toList();
        List<DemandeAmi> demandeAmis = demandeAmiService.getDemandeMessages(user);
        model.addAttribute("posts", posts);
        return "feed";
    }

    // Redirection de la racine vers la page home
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

//    @RequestMapping("groupes/details")
//    public String details(){
//        return "group_detail";
//    }

}
