package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;

import miage.groupe2.reseausocial.Util.ImageUtil;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.Util.TextUtil;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/user")
public class UtilisateurController {

    public static final String LOGIN = "redirect:/auth/login";
    @Autowired
    UtilisateurRepository utilisateurRepository;

    @Autowired
    GroupeRepository groupeRepository;
    @Autowired
    private GroupeService groupeService;
    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "form-register";
    }

    /**
     * Handles user registration with password hashing.
     *
     * @param utilisateur the user to register
     * @return redirection to login page after successful registration
     */

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute Utilisateur utilisateur,
            Model model
    ) {

        List<String> emails = utilisateurRepository.findAllEmailU();
        if (emails.contains(utilisateur.getEmailU())) {
            model.addAttribute("error", "email exist");
            return "form-register";
        }

        String hashedPassword = BCrypt.hashpw(utilisateur.getMdpU(), BCrypt.gensalt());
        utilisateur.setMdpU(hashedPassword);
        utilisateurRepository.save(utilisateur);
        return LOGIN;
    }

    @GetMapping("/rechercher")
    public String rechercherUtilisateurs(@RequestParam("nom") String nom,
                                         Model model,
                                         HttpSession session) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);

        // 全部用户
        List<Utilisateur> all = utilisateurRepository.findAll();

        // 1. almostFriends：已经发出“en attente”请求的对方
        List<Utilisateur> almostFriends = userConnecte.getDemandesEnvoyees().stream()
                .filter(d -> "en attente".equals(d.getStatut()))
                .map(d -> d.getRecepteur())
                .collect(Collectors.toCollection(ArrayList::new));
        model.addAttribute("almostFriends", almostFriends);

        // 2. recommande：随机推荐（不是自己、不是好友、也没发请求），限制 5 个
        List<Utilisateur> notFriends = all.stream()
                .filter(u -> !u.getIdUti().equals(userConnecte.getIdUti()))
                .filter(u -> !userConnecte.getAmis().contains(u))
                .filter(u -> !almostFriends.contains(u))
                .limit(5)
                .collect(Collectors.toCollection(ArrayList::new));
        model.addAttribute("recommande", notFriends);

        // 3. 搜索匹配：按 姓、名、昵称、姓名组合、反向组合 分别搜集
        List<Utilisateur> results = new ArrayList<>();

        List<Utilisateur> nomMatch = all.stream()
                .filter(u -> TextUtil.containsIgnoreAccent(u.getNomU(), nom))
                .collect(Collectors.toCollection(ArrayList::new));
        results.addAll(nomMatch);

        List<Utilisateur> prenomMatch = all.stream()
                .filter(u -> TextUtil.containsIgnoreAccent(u.getPrenomU(), nom))
                .collect(Collectors.toCollection(ArrayList::new));
        results.addAll(prenomMatch);

        List<Utilisateur> pseudoMatch = all.stream()
                .filter(u -> TextUtil.containsIgnoreAccent(u.getPseudoU(), nom))
                .collect(Collectors.toCollection(ArrayList::new));
        results.addAll(pseudoMatch);

        List<Utilisateur> nomPrenomMatch = all.stream()
                .filter(u -> TextUtil.containsIgnoreAccent(u.getNomU() + " " + u.getPrenomU(), nom))
                .collect(Collectors.toCollection(ArrayList::new));
        results.addAll(nomPrenomMatch);

        List<Utilisateur> prenomNomMatch = all.stream()
                .filter(u -> TextUtil.containsIgnoreAccent(u.getPrenomU() + " " + u.getNomU(), nom))
                .collect(Collectors.toCollection(ArrayList::new));
        results.addAll(prenomNomMatch);

        // 4. 去掉自己
        results.removeIf(u -> u.getIdUti().equals(userConnecte.getIdUti()));

        // 5. 去重并保持插入顺序
        LinkedHashSet<Utilisateur> dedup = new LinkedHashSet<>(results);
        results.clear();
        results.addAll(dedup);

        // 6. alreadyFriend：在 results 里已是好友的
        List<Utilisateur> alreadyFriend = results.stream()
                .filter(u -> userConnecte.getAmis().stream()
                        .anyMatch(a -> a.getIdUti().equals(u.getIdUti()))
                )
                .collect(Collectors.toCollection(ArrayList::new));
        model.addAttribute("alreadyFriend", alreadyFriend);

        // 从 results 中移除好友，剩下“非好友”
        results.removeIf(u -> userConnecte.getAmis().stream()
                .anyMatch(a -> a.getIdUti().equals(u.getIdUti()))
        );

        // 7. alreadySent：非好友里已经发过请求的
        List<Utilisateur> alreadySent = results.stream()
                .filter(almostFriends::contains)
                .collect(Collectors.toCollection(ArrayList::new));
        model.addAttribute("alreadySent", alreadySent);

        // 从 results 中移除已发请求的，剩下“完全不是好友的”
        results.removeIf(almostFriends::contains);

        model.addAttribute("notFriendsAtAll", results);

        return "results";
    }


    @GetMapping("/mes-amis")
    public String voirMesAmis(HttpSession session, Model model) {

        // Rafraîchir l’utilisateur depuis la BDD pour charger les relations (lazy loading)
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        List<Utilisateur> allUtilisateurs = utilisateurRepository.findAll();
        List<Utilisateur> almostFriends = user.getDemandesEnvoyees().stream()
                .filter(d -> d.getStatut().equals("en attente"))
                .map(d -> d.getRecepteur())
                .toList();
        List<Utilisateur> notFriends = allUtilisateurs.stream()
                .filter(u -> !u.getIdUti().equals(user.getIdUti()))
                .filter(u -> !user.getAmis().contains(u))
                .filter(u -> !almostFriends.contains(u))
                .limit(5)
                .toList();

        model.addAttribute("recommande", notFriends);
        model.addAttribute("almostFriends", almostFriends);
        return "friends";
    }

    @RequestMapping("/supprimer-ami")
    public String supprimerAmi(@RequestParam("idAmi") Integer idAmi,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               @RequestHeader(value = "Referer", required = false) String referer
    ) {


        Utilisateur utilisateur = utilisateurService.getUtilisateurFromSession(session);
        Utilisateur ami = utilisateurRepository.findByIdUti(idAmi);

        if (ami != null) {
            utilisateur.getAmis().remove(ami);
            ami.getAmis().remove(utilisateur); // pour supprimer dans les deux sens
            utilisateurRepository.save(utilisateur);
            utilisateurRepository.save(ami);

            redirectAttributes.addFlashAttribute("succes", "Ami supprimé avec succès.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer cet ami.");
        }
        return RedirectUtil.getSafeRedirectUrl(referer, "redirect:/user/mes-amis");
    }

    @GetMapping("/{id}/profil")
    public String userProfil(
            @RequestParam(value = "type", defaultValue = "post") String type,
            @PathVariable Integer id,
            Model model,
            HttpSession session
    ) {
        Utilisateur user = utilisateurRepository.findByIdUti(id);
        Utilisateur sessionUser = utilisateurService.getUtilisateurFromSession(session);

        model.addAttribute("user", user);
        List<Post> posts = user.getPosts().stream()
                .filter(i -> i.getGroupe() == null)
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost()))
                .limit(10)
                .toList();

        List<Post> reposts = user.getPostsRepostes().stream()
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost()))
                .limit(10)
                .toList();

        List<Utilisateur> Amis = user.getAmis().stream()
                .limit(6)
                .toList();

        model.addAttribute("Friends", Amis);
        model.addAttribute("posts", "repost".equals(type) ? reposts : posts);
        model.addAttribute("type", type);
        model.addAttribute("reposts", reposts);

        boolean etreAmi = sessionUser.getAmis().stream()
                .anyMatch(u -> u.getIdUti().equals(user.getIdUti()));
        model.addAttribute("etreAmi", etreAmi);

        boolean demandeEnvoyee = sessionUser.getDemandesEnvoyees().stream()
                .anyMatch(d -> d.getRecepteur().getIdUti().equals(user.getIdUti()) && d.getStatut().equals("en attente"));
        model.addAttribute("demandeEnvoyee", demandeEnvoyee);

        return "profil_user";
    }

    @GetMapping("/modifierProfil")
    public String modifierProfil() {
        return "setting";
    }

    @PostMapping("/modifierProfil")
    public String modifierProfil(
            @RequestParam String pseudoU,
            @RequestParam String emailU,
            @RequestParam String introductionU,
            HttpSession session
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        user.setPseudoU(pseudoU);
        user.setEmailU(emailU);
        user.setIntroductionU(introductionU);
        utilisateurRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/user/" + user.getIdUti() + "/profil";
    }


    @PostMapping("/modifierPassword")
    public String modifierPassword(
            @RequestParam String currentP,
            @RequestParam String newP,
            HttpSession session,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        if (BCrypt.checkpw(currentP, user.getMdpU())) {
            newP = BCrypt.hashpw(newP, BCrypt.gensalt());
            user.setMdpU(newP);
            utilisateurRepository.save(user);
            session.setAttribute("user", user);
        }
        return RedirectUtil.getSafeRedirectUrl(referer, "/user/modifierProfil");

    }

    @RequestMapping("/joinGroupe")
    public String joinGroupe(
            HttpSession session,
            @RequestParam int idGrp,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        Groupe groupe = groupeRepository.findGroupeByidGrp(idGrp);
        groupeService.joinGroupe(user, groupe);

        user.getGroupesAppartenance().size();
        session.setAttribute("user", user);
        return RedirectUtil.getSafeRedirectUrl(referer, "/user/mes-groupes");
    }

    @RequestMapping("/quitterGroupe")
    public String quitterGroupe(
            HttpSession session,
            @RequestParam int idGrp,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Groupe groupe = groupeRepository.findGroupeByidGrp(idGrp);

        groupeService.quitterGroupe(user, idGrp);
        if (user.equals(groupe.getCreateur())) {
            groupeService.supprimerGroupe(user, groupe);
        }
        user.getGroupesAppartenance().size();
        session.setAttribute("user", user);
        return RedirectUtil.getSafeRedirectUrl(referer, "/user/mes-groupes");
    }

    @RequestMapping("/supprimerGroupe")
    public String supprimerGroupe(
            HttpSession session,
            @RequestParam int idGrp
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        groupeService.supprimerGroupe(user, idGrp);

        return "redirect:/user/mes-groupes";
    }


    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                               HttpSession session,
                               @RequestHeader(value = "Referer", required = false) String referer
    ) throws IOException {

        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        if (!file.isEmpty()) {
            byte[] originalBytes = file.getBytes();
            byte[] croppedBytes = ImageUtil.cropCenterSquare(originalBytes);
            user.setAvatar(croppedBytes);
            utilisateurRepository.save(user);
        }

        session.setAttribute("user", user);

        return RedirectUtil.getSafeRedirectUrl(referer, "/user/" + user.getIdUti());
    }


}
