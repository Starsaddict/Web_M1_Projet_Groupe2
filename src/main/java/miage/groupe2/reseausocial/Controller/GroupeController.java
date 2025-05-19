package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/groupe")
public class GroupeController {

    @Autowired
    GroupeRepository groupeRepository;

    @Autowired
    UtilisateurRepository utilisateurRepository;

    @Autowired
    GroupeService groupeService;
    @Autowired
    private UtilisateurService utilisateurService;

    @RequestMapping("")
    public String index(Model model) {
        return "redirect:/groupe/list";
    }

    @RequestMapping("/list")
    public String GroupeList(
            Model model
    ) {
        List<Groupe> groupes = groupeRepository.findAll();
        model.addAttribute("groupes", groupes);
        return "listGroupe";
    }

    @GetMapping("/creer")
    public String creerGroupe(
            Model model
    ){
        model.addAttribute("groupe", new Groupe());
        return "creerGroupe";
    }

    @PostMapping("/creer")
    public String creerGroupe(
            HttpSession session,
            @ModelAttribute Groupe groupe,
            Model model
    ){
        Utilisateur userSession = (Utilisateur) session.getAttribute("user");
        Utilisateur user = utilisateurRepository.findByidUti(userSession.getIdUti());

        groupeService.createGroupe(user, groupe);

        session.setAttribute("user", user);
        return "redirect:/groupe/list";
    }

    @GetMapping("/{id}")
    public String afficherGroupe(
            @PathVariable("id") int id,
            Model model,
            HttpSession session
    ) {
        Groupe groupe = groupeRepository.findGroupeByidGrp(id);
        if (groupe == null) return "redirect:/groupe/list";

        Utilisateur user = utilisateurRepository.findByidUti(
                ((Utilisateur) session.getAttribute("user")).getIdUti()
        );

        user.getGroupesAppartenance().size();

        boolean estMembre = user.getGroupesAppartenance().stream()
                .anyMatch(g -> g.getIdGrp().equals(groupe.getIdGrp()));

        model.addAttribute("groupe", groupe);
        model.addAttribute("membres", groupe.getMembres());
        model.addAttribute("posts", groupe.getPosts());
        model.addAttribute("post", new Post());
        model.addAttribute("estMembre", estMembre);

        return "groupe_detail";
    }


    @GetMapping("/{id}/poster")
    public String formPosterDansGroupe(
            @PathVariable("id") int id,
            Model model
    ) {
        Groupe groupe = groupeRepository.findGroupeByidGrp(id);
        if (groupe == null) return "redirect:/groupe/list";

        model.addAttribute("post", new Post());
        model.addAttribute("groupe", groupe);
        return "posterDansGroupe";
    }

    @PostMapping("/{id}/poster")
    public String posterDansGroupe(
            @PathVariable("id") int id,
            @ModelAttribute Post post,
            HttpSession session
    ) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        Groupe groupe = groupeRepository.findGroupeByidGrp(id);

        post.setDatePost(System.currentTimeMillis());
        post.setCreateur(user);
        post.setGroupe(groupe); // 要求你的 Post 实体有 Groupe groupe 字段

        if (groupe.getPosts() == null) {
            groupe.setPosts(new ArrayList<>());
        }
        groupe.getPosts().add(post);

        groupeRepository.save(groupe); // 级联保存 Post（或用 postRepository.save(post)）

        return "redirect:/groupe/" + id;
    }

    @PostMapping("/{id}/supprimerMembre")
    public String supprimerMembreDuGroupe(
            @PathVariable("id") int id,
            @RequestParam("idMembre") int idMembre,
            HttpSession session
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Groupe groupe = groupeRepository.findGroupeByidGrp(id);

        if (groupe != null && user != null && user.equals(groupe.getCreateur())) {
            Utilisateur membre = utilisateurRepository.findByidUti(idMembre);
            if (membre != null) {
                groupeService.quitterGroupe(membre, groupe);
            }
        }

        return "redirect:/groupe/" + id;
    }


}
