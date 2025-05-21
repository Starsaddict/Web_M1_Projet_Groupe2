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
import java.util.List;

/**
 * Contrôleur Spring MVC pour la gestion des groupes dans le réseau social.
 * Permet aux utilisateurs de créer, consulter, rejoindre ou quitter des groupes.
 */
@Controller
@RequestMapping("/groupe")
public class GroupeController {

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private GroupeService groupeService;

    @Autowired
    private UtilisateurService utilisateurService;

    public static final String LIST_GROUPS = "redirect:/groupe/list";

    /**
     * Redirige vers la liste des groupes.
     *
     * @param model le modèle Spring MVC
     * @return la redirection vers la liste des groupes
     */
    @RequestMapping("")
    public String index(Model model) {
        return LIST_GROUPS;
    }

    /**
     * Affiche la liste des groupes disponibles, ceux recommandés et ceux auxquels l'utilisateur appartient ou a créés.
     *
     * @param model   le modèle utilisé pour la vue
     * @param session la session HTTP pour identifier l'utilisateur
     * @return le nom de la vue "groups"
     */
    @RequestMapping("/list")
    public String groupeList(Model model, HttpSession session) {
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

    /**
     * Permet de créer un nouveau groupe.
     *
     * @param session la session utilisateur
     * @param groupe  le groupe à créer (rempli par formulaire)
     * @param model   le modèle MVC
     * @param referer l'URL précédente
     * @return redirection sécurisée vers la liste des groupes
     */
    @PostMapping("/creer")
    public String creerGroupe(
            HttpSession session,
            @ModelAttribute Groupe groupe,
            Model model,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        groupeService.createGroupe(user, groupe);
        session.setAttribute("user", user);
        return RedirectUtil.getSafeRedirectUrl(referer, LIST_GROUPS);
    }

    /**
     * Affiche les détails d’un groupe spécifique.
     *
     * @param id      l'identifiant du groupe
     * @param model   le modèle MVC
     * @param session la session utilisateur
     * @return la vue des détails du groupe, ou redirection si non trouvé
     */
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

    /**
     * Permet de poster un message dans un groupe donné.
     *
     * @param id      identifiant du groupe
     * @param post    message à publier
     * @param session session HTTP de l'utilisateur
     * @return redirection vers la page du groupe
     */
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

    /**
     * Supprime un membre d’un groupe (si l’utilisateur est le créateur).
     *
     * @param id        identifiant du groupe
     * @param idMembre  identifiant du membre à retirer
     * @param session   session HTTP de l'utilisateur
     * @return redirection vers la page du groupe
     */
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
}
