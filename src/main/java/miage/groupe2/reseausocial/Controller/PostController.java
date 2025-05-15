package miage.groupe2.reseausocial.Controller;


import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
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
        List<Post> posts = postRepository.findByCreateur(user);
        model.addAttribute("Posts", posts);
        model.addAttribute("userId", id);
        return "listPostsPersonne";
    }


    @GetMapping("/creer")
    public String CreerPost(Model model) {
        Post post = new Post();
        model.addAttribute("Post", post);
        return "creerPost";
    }

    @PostMapping("/creer")
    public String CreerPost(
            @ModelAttribute("post") Post post,
            HttpSession session
    ) {
        long timestamp = System.currentTimeMillis();
        post.setDatePost(timestamp);

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        post.setCreateur(user);
        postRepository.save(post);

        return "redirect:/user/" + user.getIdUti();
    }

    @GetMapping("/list")
    // Algorithmes later
    public String listPosts(Model model) {
        List<Post> posts = postRepository.findAll();
        posts = posts.stream()
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost())) // 降序
                .limit(10)
                .toList();
        model.addAttribute("posts", posts);
        return "listPosts";
    }

    @GetMapping("/list/amis")
    public String listAmis(
            Model model,
            HttpSession session) {
        List<Post> posts = new ArrayList<>();

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        List<Utilisateur> amis = user.getAmis();
        for (Utilisateur u : amis) {
            List<Post> postsAmis = postRepository.findByCreateur(u);
            posts.addAll(postsAmis);
        }
        model.addAttribute("posts", posts);
        return "listPostAmis";
    }

    @GetMapping("")
    public String afficherPostParId(@RequestParam("id") Integer id, Model model) {
        Post post = postRepository.findById(id).orElse(null);

        if (post == null) {
            return "redirect:/home";
        }

        model.addAttribute("post", post);
        return "detailPost";
    }

    @GetMapping("/modifier")
    public String afficherFormulaireModification(@RequestParam("id") Integer id, Model model) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/home";
        }

        model.addAttribute("post", post);
        return "modifierPost";
    }

    @PostMapping("/modifier")
    public String modifierPost(@ModelAttribute("post") Post post, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        post.setCreateur(user);
        postRepository.save(post);
        return "redirect:/user/" + user.getIdUti();
    }

    @GetMapping("/supprimer")
    public String supprimerPost(@RequestParam("id") Integer id, HttpSession session) {
        Post post = postRepository.findById(id).orElse(null);
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (post != null && post.getCreateur().getIdUti().equals(user.getIdUti())) {
            postRepository.delete(post);
        }

        return "redirect:/user/" + user.getIdUti();
    }



}
