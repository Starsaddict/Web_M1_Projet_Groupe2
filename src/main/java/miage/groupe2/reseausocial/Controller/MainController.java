package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.DemandeAmiService;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;


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

    /**
     * Affiche le fil d'actualité personnalisé.
     */
    @GetMapping("/home")
    public String homepage(Model model, HttpSession session) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        if (user == null) {
            return "redirect:/auth/login";
        }

        Hibernate.initialize(user.getPostsRepostes());
        List<Post> posts = user.getPosts();
        posts.addAll(user.getPostsRepostes());
        List<Utilisateur> friends = user.getAmis();

        for (Utilisateur u : friends) {
            posts.addAll(u.getPostsRepostes());
            posts.addAll(u.getPosts());
        }

        posts = posts.stream()
                .filter(i -> i.getGroupe() == null)
                .distinct()
                .sorted(Comparator.comparing(Post::getDatePost).reversed())
                .limit(20)
                .toList();

        model.addAttribute("posts", posts);
        return "feed";
    }

    /**
     * Redirige la racine vers la page d'accueil.
     */
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }
}
