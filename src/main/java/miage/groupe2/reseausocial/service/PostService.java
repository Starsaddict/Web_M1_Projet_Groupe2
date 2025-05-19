package miage.groupe2.reseausocial.service;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private GroupeRepository groupeRepository;
    @Autowired
    private UtilisateurService utilisateurService;

    public void publierPostDansGroupe(Post post, Utilisateur auteur, Groupe groupe) {
        post.setDatePost(System.currentTimeMillis());
        post.setCreateur(auteur);
        post.setGroupe(groupe);
        groupe.getPosts().add(post);
        auteur.getPosts().add(post);

        postRepository.save(post);
        utilisateurRepository.save(auteur);
        groupeRepository.save(groupe);
    }

    public void publierPostSansGroupe(
            Post post,
            Utilisateur user
    ){
        post.setDatePost(System.currentTimeMillis());
        post.setCreateur(user);
        post.setGroupe(null);
        user.getPosts().add(post);
        postRepository.save(post);
        utilisateurRepository.save(user);
    }

    public List<Post> listPostFriends(
            HttpSession session
    ) {
        List<Post> posts = new ArrayList<>();
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        if (user == null) {
            return posts;
        }
        List<Utilisateur> amis = user.getAmis();
        for (Utilisateur u : amis) {
            List<Post> postsAmis = postRepository.findByCreateur(u);
            posts.addAll(postsAmis);
        }
        posts = posts.stream()
                .filter(post -> post.getGroupe() == null)
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost()))
                .toList();
        return posts;
    }

    public Post findPostById(Integer id) {
        return postRepository.findByIdPost(id);
    }
}
