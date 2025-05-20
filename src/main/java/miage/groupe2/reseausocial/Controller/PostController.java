package miage.groupe2.reseausocial.Controller;


import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.CommentaireRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.ReactionRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    UtilisateurRepository utilisateurRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentaireRepository commentaireRepository;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private GroupeService groupeService;
    @Autowired
    private PostService postService;
    @Autowired
    ReactionRepository reactionRepository;


    @GetMapping("/{id}")
    public String postPersonne(
            @PathVariable long id,
            Model model
    ) {
        Utilisateur user = utilisateurRepository.findByIdUti(id);
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
    public String creerPost(
            @ModelAttribute("post") Post post,
            @RequestParam(value = "idgrp", required = false) Integer idGrp,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            HttpSession session,
            @RequestHeader(value = "Referer", required = false) String referer
    ) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            post.setImagePost(imageFile.getBytes());
        }
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        if (idGrp != null) {
            Groupe groupe = groupeService.getGroupeByidGrp(idGrp);
            postService.publierPostDansGroupe(post, user, groupe);
            return "redirect:" + (referer != null ? referer : "redirect:/groupe/" + idGrp);
        } else {
            postService.publierPostSansGroupe(post, user);
            return "redirect:" + (referer != null ? referer : user.getIdUti() + "/profil");
        }
    }

    @GetMapping("/list")
    // Algorithmes later
    public String listPosts(Model model) {
        List<Post> posts = postRepository.findAll();
        posts = posts.stream()
                .filter(post -> post.getGroupe() == null)
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost()))
                .limit(10)
                .toList();
        model.addAttribute("posts", posts);
        return "listPosts";
    }

    @GetMapping("/list/amis")
    public String listAmis(
            Model model,
            HttpSession session) {

        List<Post> posts = postService.listPostFriends(session);

        posts = posts.stream().limit(10).toList();
        model.addAttribute("posts", posts);
        return "listPostAmis";
    }

    @GetMapping("")
    public String afficherPostParId(@RequestParam("id") Integer id, Model model) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/home";
        }

        List<Commentaire> commentaires = commentaireRepository.findByPost(post);
        model.addAttribute("post", post);
        model.addAttribute("commentaires", commentaires);
        model.addAttribute("nouveauCommentaire", new Commentaire());

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
    public String modifierPost(@ModelAttribute("post") Post post,
                               HttpSession session,
                               @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        post.setCreateur(user);
        postRepository.save(post);
        return "redirect:" + (referer != null ? referer : "/user/" + user.getIdUti() + "/profil");
    }

    @GetMapping("/supprimer")
    public String supprimerPost(@RequestParam("id") Integer id,
                                HttpSession session,
                                @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Post post = postRepository.findById(id).orElse(null);
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (post != null && post.getCreateur().getIdUti().equals(user.getIdUti())) {
            postRepository.delete(post);
        }

        return "redirect:" + (referer != null ? referer : "/user/" + user.getIdUti() + "/profil");
    }

    @GetMapping("/repost")
    public String repostPost(@RequestParam("id") Integer postId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             @RequestHeader(value = "Referer", required = false) String referer
    ) {

        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        Post post = postService.findPostById(postId);

        List<Post> repostList = user.getPostsRepostes();
        if (!repostList.contains(post)) {
            repostList.add(post);
            user.setPostsRepostes(repostList);
            utilisateurRepository.save(user);
        }
        session.setAttribute("user", user);
        return "redirect:" + (referer != null ? referer : "/home");
    }

    @GetMapping("/repost/annuler")
    public String repostAnnuler(@RequestParam("id") Integer postId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Post post = postService.findPostById(postId);

        List<Post> repostList = user.getPostsRepostes();
        if (repostList.contains(post)) {
            repostList.remove(post);
            user.setPostsRepostes(repostList);
            utilisateurRepository.save(user);
        }
        session.setAttribute("user", user);

        return "redirect:" + (referer != null ? referer : "/home");
    }
    @PostMapping("/commenter")
    public String ajouterCommentaire(@ModelAttribute("nouveauCommentaire") Commentaire commentaire,
                                     @RequestParam("postId") Integer postId,
                                     HttpSession session,
                                     @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        Post post = postRepository.findById(postId).orElse(null);

        if (user == null || post == null) {
            return "redirect:/auth/login";
        }

        commentaire.setUtilisateur(user);
        commentaire.setPost(post);
        commentaire.setDateC(System.currentTimeMillis());

        commentaireRepository.save(commentaire);

        return "redirect:" + (referer != null ? referer : "/home");
    }

    @PostMapping("/react")
    @Transactional
    public String ajouterReaction(@RequestParam("id") Integer postId,
                                  @RequestParam("type") String emoji,
                                  HttpSession session,
                                  @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        Post post = postRepository.findById(postId).orElseThrow();

        reactionRepository.deleteByPostAndUtilisateur(post, user);

        Reaction reaction = new Reaction();
        reaction.setPost(post);
        reaction.setUtilisateur(user);
        reaction.setType(emoji);
        reactionRepository.save(reaction);

        return "redirect:" + (referer != null ? referer : "/home");
    }





}
