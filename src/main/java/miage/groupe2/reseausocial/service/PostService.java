package miage.groupe2.reseausocial.service;

import miage.groupe2.reseausocial.Model.*;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private GroupeRepository groupeRepository;

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
}
