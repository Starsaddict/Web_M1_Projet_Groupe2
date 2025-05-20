package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;

import miage.groupe2.reseausocial.Util.ImageUtil;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;


@Controller
@RequestMapping("/user")
public class UtilisateurController {

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
        return "redirect:/auth/login";
    }

    @GetMapping("/rechercher")
    public String rechercherUtilisateurs(@RequestParam("nom") String nom, Model model, HttpSession session) {
        Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
        if (userConnecte == null) {
            return "redirect:/auth/login";
        }

        List<Utilisateur> utilisateurs = utilisateurRepository.findByNomUContainingIgnoreCase(nom);

        // Supprimer l'utilisateur connecté des résultats
        utilisateurs.removeIf(u -> u.getIdUti().equals(userConnecte.getIdUti()));

        model.addAttribute("utilisateurs", utilisateurs);
        return "search_results";
    }

    @GetMapping("/mes-amis")
    public String voirMesAmis(HttpSession session, Model model) {
        Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
        if (userConnecte == null) {
            return "redirect:/auth/login";
        }

        // Rafraîchir l’utilisateur depuis la BDD pour charger les relations (lazy loading)
        Utilisateur utilisateurAvecAmis = utilisateurRepository.findById(userConnecte.getIdUti()).orElse(null);
        if (utilisateurAvecAmis == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("amis", utilisateurAvecAmis.getAmis());
        return "listeamis"; // correspond à amis.html
    }

    @PostMapping("/supprimer-ami")
    public String supprimerAmi(@RequestParam("idAmi") Integer idAmi, HttpSession session, RedirectAttributes redirectAttributes) {
        Utilisateur userConnecte = (Utilisateur) session.getAttribute("user");
        if (userConnecte == null) {
            return "redirect:/auth/login";
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userConnecte.getIdUti()).orElse(null);
        Utilisateur ami = utilisateurRepository.findById(idAmi).orElse(null);

        if (utilisateur != null && ami != null) {
            utilisateur.getAmis().remove(ami);
            ami.getAmis().remove(utilisateur); // pour supprimer dans les deux sens
            utilisateurRepository.save(utilisateur);
            utilisateurRepository.save(ami);

            redirectAttributes.addFlashAttribute("succes", "Ami supprimé avec succès.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer cet ami.");
        }

        return "redirect:/user/mes-amis";
    }

@GetMapping("/{id}/profil")
public String userProfil(
        @RequestParam(value = "type", defaultValue = "post") String type,
        @PathVariable Integer id,
        Model model
){
    Utilisateur user = utilisateurRepository.findByIdUti(id);

    model.addAttribute("user", user);
    List<Post> posts = user.getPosts().stream()
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
    return "profil_user";
}

    @GetMapping("/modifierProfil")
    public String modifierProfil( ) {

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
        return "redirect:/user/"+user.getIdUti() + "/profil";
    }

    @GetMapping("/modifierPassword")
    public String modifierPassword(
    ){
        return "modifier_password";
    }

    @PostMapping("/modifierPassword")
    public String modifierPassword(
            @RequestParam String currentP,
            @RequestParam String newP,
            HttpSession session,
            @RequestHeader(value = "Referer", required = false) String referer
    ){
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        if(BCrypt.checkpw(currentP, user.getMdpU())){
            newP = BCrypt.hashpw(newP, BCrypt.gensalt());
            user.setMdpU(newP);
            utilisateurRepository.save(user);
            session.setAttribute("user", user);
            System.out.println("修改成功");
            return "redirect:" + (referer != null ? referer : "/user/modifierProfil");
        }
        System.out.println("修改失败");
        return "redirect:" + (referer != null ? referer : "/user/modifierProfil");

    }

    @RequestMapping("/joinGroupe")
    public String joinGroupe(
            HttpSession session,
            @RequestParam int idGrp,
            @RequestHeader(value = "Referer", required = false) String referer
    ){
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);

        Groupe groupe = groupeRepository.findGroupeByidGrp(idGrp);
        groupeService.joinGroupe(user,groupe);

        user.getGroupesAppartenance().size();
        session.setAttribute("user", user);
        return "redirect:" + (referer != null ? referer : "/user/mes-groupes");

    }

    @RequestMapping("/quitterGroupe")
    public String quitterGroupe(
            HttpSession session,
            @RequestParam int idGrp,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Groupe groupe = groupeRepository.findGroupeByidGrp(idGrp);
        groupeService.quitterGroupe(user,groupe);

        if (user.equals(groupe.getCreateur())) {
            groupeService.supprimerGroupe(user, groupe);
        }
        user.getGroupesAppartenance().size();
        session.setAttribute("user", user);
        return "redirect:" + (referer != null ? referer : "/user/mes-groupes");
        }

    @RequestMapping("/supprimerGroupe")
    public String supprimerGroupe(
            HttpSession session,
            @RequestParam int idGrp
    ){
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        groupeService.supprimerGroupe(user,idGrp);

        return "redirect:/user/mes-groupes";
    }

    @GetMapping("/mes-groupes")
    public String voirMesGroupes(HttpSession session, Model model) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        model.addAttribute("groupesCrees", user.getGroupes()); // createur
        model.addAttribute("groupesMembre", user.getGroupesAppartenance()); // membre
        return "mes_groupes";
    }


    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                               HttpSession session,
                               @RequestHeader(value = "Referer", required = false) String referer
    ) throws IOException {
        System.out.println("uploadAvatar");
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user != null && !file.isEmpty()) {
            byte[] originalBytes = file.getBytes();
            byte[] croppedBytes = ImageUtil.cropCenterSquare(originalBytes);
            user.setAvatar(croppedBytes);
            utilisateurRepository.save(user);
        }

        session.setAttribute("user", user);
        return "redirect:" + (referer != null ? referer : "/user/" + user.getIdUti());
    }



}
