package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public static final String LIST_GROUPS = "redirect:/groupe/list";
    @RequestMapping("")
    public String index(Model model) {
        return LIST_GROUPS;
    }

    @RequestMapping("/list")
    public String groupeList(
            Model model,
            HttpSession session
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        List<Groupe> recommandGroupes = groupeRepository.findAll().stream()
                .filter(groupe -> !groupe.getMembres().contains(user))
                .toList();
        model.addAttribute("recommandGroupes", recommandGroupes);

        List<Groupe> monGroupes = user.getGroupesAppartenance();
        model.addAttribute("monGroupes", monGroupes);
        List<Groupe> monGroupCreer = user.getGroupes();
        model.addAttribute("monGroupCreer", monGroupCreer);

        return "groups";
    }

    @PostMapping("/creer")
    public String creerGroupe(
            HttpSession session,
            @ModelAttribute Groupe groupe,
            Model model,
            @RequestHeader(value = "Referer", required = false) String referer

    ){
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        groupeService.createGroupe(user, groupe);

        session.setAttribute("user", user);
        return RedirectUtil.getSafeRedirectUrl(referer,LIST_GROUPS);
    }

    @GetMapping("/{id}")
    public String afficherGroupe(
            @PathVariable("id") int id,
            Model model,
            HttpSession session
    ) {
        Groupe groupe = groupeRepository.findGroupeByidGrp(id);
        if (groupe == null) return LIST_GROUPS;

        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);



        boolean estMembre = user.getGroupesAppartenance().stream()
                .anyMatch(g -> g.getIdGrp().equals(groupe.getIdGrp()));

        user.getGroupesAppartenance().size();

        List<Post> posts = groupe.getPosts().stream()
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost()))
                .toList();

        List<Groupe> groupes = groupeRepository.findAll().stream()
                .filter(g -> !g.equals(groupe))
                .limit(6)
                .toList();

        model.addAttribute("groupe", groupe);
        model.addAttribute("membres", groupe.getMembres());
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        model.addAttribute("estMembre", estMembre);
        model.addAttribute("groupes", groupes);

        return "group_detail";
    }

    @PostMapping("/{id}/poster")
    public String posterDansGroupe(
            @PathVariable("id") int id,
            @ModelAttribute Post post,
            HttpSession session
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Groupe groupe = groupeRepository.findGroupeByidGrp(id);

        post.setDatePost(System.currentTimeMillis());
        post.setCreateur(user);
        post.setGroupe(groupe);

        if (groupe.getPosts() == null) {
            groupe.setPosts(new ArrayList<>());
        }
        groupe.getPosts().add(post);

        groupeRepository.save(groupe);

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
            Utilisateur membre = utilisateurRepository.findByIdUti(idMembre);
            if (membre != null) {
                groupeService.quitterGroupe(membre, groupe.getIdGrp());
            }
        }

        return "redirect:/groupe/" + id;
    }

    public void setUtilisateurService(UtilisateurService utilisateurService) {
    this.utilisateurService = utilisateurService;
    }


}
