package miage.groupe2.reseausocial.Controller;


import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.CommentaireRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.ReactionRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.PostService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    public static final String HOME_PAGE = "/home";

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
            return RedirectUtil.getSafeRedirectUrl(referer,"redirect:/groupe/" + idGrp);
        } else {
            postService.publierPostSansGroupe(post, user);
            return RedirectUtil.getSafeRedirectUrl(referer,"/user/" + user.getIdUti() + "/profil");

        }
    }



    @PostMapping("/modifier")
    public String modifierPost(@RequestParam(name = "titre", required = false ) String titre,
                               @RequestParam(name = "text", required = false ) String text,
                               @RequestParam(value = "imagePost", required = false) MultipartFile imageFile,
                               @RequestParam(value = "idPost") Integer idPost,
                               @RequestParam(name = "deleteImage", required = false) Boolean deleteImage,
                               @RequestHeader(value = "Referer", required = false) String referer
    ) throws IOException {
        Post post = postRepository.findByIdPost(idPost);

        if ( titre != null && !titre.isEmpty()) {
            post.setTitrePost(titre);
        }
        if ( text != null && !text.isEmpty()) {
            post.setTextePost(text);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            post.setImagePost(imageFile.getBytes());

        }else if (Boolean.TRUE.equals(deleteImage)) {
            post.setImagePost(null);
        }

        postRepository.save(post);

        return RedirectUtil.getSafeRedirectUrl(referer,HOME_PAGE);
    }

    @GetMapping("/supprimer")
    public String supprimerPost(@RequestParam("id") Integer id,
                                HttpSession session,
                                @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Post post = postRepository.findByIdPost(id);
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (post != null && post.getCreateur().getIdUti().equals(user.getIdUti())) {
            postRepository.delete(post);
        }

        return RedirectUtil.getSafeRedirectUrl(referer,"/user/" + user.getIdUti() + "/profil");
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
        return RedirectUtil.getSafeRedirectUrl(referer,HOME_PAGE);
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

        return RedirectUtil.getSafeRedirectUrl(referer,HOME_PAGE);
    }
    @PostMapping("/commenter")
    public String ajouterCommentaire(@ModelAttribute("nouveauCommentaire") Commentaire commentaire,
                                     @RequestParam("postId") Integer postId,
                                     HttpSession session,
                                     @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Post post = postRepository.findById(postId).orElse(null);

        if (user == null || post == null) {
            return "redirect:/auth/login";
        }

        commentaire.setUtilisateur(user);
        commentaire.setPost(post);
        commentaire.setDateC(System.currentTimeMillis());

        commentaireRepository.save(commentaire);

        return RedirectUtil.getSafeRedirectUrl(referer,HOME_PAGE);
    }

    @PostMapping("/react")
    @Transactional
    public String ajouterReaction(@RequestParam("id") Integer postId,
                                  @RequestParam("type") String emoji,
                                  HttpSession session,
                                  @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Post post = postRepository.findByIdPost(postId);

        reactionRepository.deleteByPostAndUtilisateur(post, user);

        Reaction reaction = new Reaction();
        reaction.setPost(post);
        reaction.setUtilisateur(user);
        reaction.setType(emoji);
        reactionRepository.save(reaction);

        return RedirectUtil.getSafeRedirectUrl(referer,HOME_PAGE);
    }





}
