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

import java.util.*;

/**
 * Contrôleur principal de l'application de réseau social.
 * Il gère les routes liées à la page d'accueil et à la racine.
 */
@Controller
public class MainController {

    /**
     * Référentiel des utilisateurs pour l'accès aux données persistantes.
     */
    @Autowired
    UtilisateurRepository utilisateurRepository;

    /**
     * Service de gestion des publications.
     */
    @Autowired
    private PostService postService;

    /**
     * Service de gestion des demandes d’amis.
     */
    @Autowired
    DemandeAmiService demandeAmiService;

    /**
     * Service pour la gestion des utilisateurs (récupération de session, etc.).
     */
    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Méthode de gestion de la page d'accueil du réseau social.
     * Elle collecte tous les posts de l'utilisateur connecté et de ses amis
     * (posts normaux et repostés), les trie par date décroissante, puis en affiche les 20 plus récents.
     *
     * @param model   Le modèle Spring pour transmettre les données à la vue.
     * @param session La session HTTP pour récupérer l'utilisateur connecté.
     * @return Le nom de la vue correspondant au fil d'actualité ("feed"), ou redirection vers la page de connexion si non connecté.
     */
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
                .toList();
        posts = posts.stream().limit(20).toList();

        model.addAttribute("posts", posts);
        return "feed";
    }

    /**
     * Redirige automatiquement la racine du site ("/") vers la page d'accueil "/home".
     *
     * @return Redirection vers "/home".
     */
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

}
