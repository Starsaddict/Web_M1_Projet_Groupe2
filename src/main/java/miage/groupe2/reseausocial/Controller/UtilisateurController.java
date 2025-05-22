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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller managing user-related actions.
 */
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

    /**
     * Displays the registration form.
     * @param model model to bind data
     * @return registration form view
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "form-register";
    }

    /**
     * Registers a new user with hashed password.
     * @param utilisateur user to register
     * @param model model to bind data
     * @return redirection to login or form with error
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute Utilisateur utilisateur, Model model) {
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

    /**
     * Searches users by name, excluding the current user.
     * @param nom searched name
     * @param model model to bind data
     * @param session HTTP session for current user
     * @return search results view
     */
    @GetMapping("/rechercher")
    public String rechercherUtilisateurs(@RequestParam("nom") String nom,
                                         Model model,
                                         HttpSession session) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);

        List<Utilisateur> all = utilisateurRepository.findAll();

        List<Utilisateur> almostFriends = userConnecte.getDemandesEnvoyees().stream()
                .filter(d -> "en attente".equals(d.getStatut()))
                .map(d -> d.getRecepteur())
                .collect(Collectors.toCollection(ArrayList::new));
        model.addAttribute("almostFriends", almostFriends);

        List<Utilisateur> notFriends = all.stream()
                .filter(u -> !u.getIdUti().equals(userConnecte.getIdUti()))
                .filter(u -> !userConnecte.getAmis().contains(u))
                .filter(u -> !almostFriends.contains(u))
                .limit(5)
                .collect(Collectors.toCollection(ArrayList::new));
        model.addAttribute("recommande", notFriends);

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

        results.removeIf(u -> u.getIdUti().equals(userConnecte.getIdUti()));

        LinkedHashSet<Utilisateur> dedup = new LinkedHashSet<>(results);
        results.clear();
        results.addAll(dedup);

        List<Utilisateur> alreadyFriend = results.stream()
                .filter(u -> userConnecte.getAmis().stream()
                        .anyMatch(a -> a.getIdUti().equals(u.getIdUti()))
                )
                .collect(Collectors.toCollection(ArrayList::new));
        model.addAttribute("alreadyFriend", alreadyFriend);

        results.removeIf(u -> userConnecte.getAmis().stream()
                .anyMatch(a -> a.getIdUti().equals(u.getIdUti()))
        );

        List<Utilisateur> alreadySent = results.stream()
                .filter(almostFriends::contains)
                .collect(Collectors.toCollection(ArrayList::new));
        model.addAttribute("alreadySent", alreadySent);

        results.removeIf(almostFriends::contains);

        model.addAttribute("notFriendsAtAll", results);

        return "results";
    }


    @GetMapping("/mes-amis")
    public String voirMesAmis(HttpSession session, Model model) {

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
                               @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurFromSession(session);
        Utilisateur ami = utilisateurRepository.findByIdUti(idAmi);
        if (ami != null) {
            utilisateur.getAmis().remove(ami);
            ami.getAmis().remove(utilisateur);
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

    /**
     * Shows the profile modification page.
     * @return profile settings view
     */
    @GetMapping("/modifierProfil")
    public String modifierProfil() {
        return "setting";
    }

    /**
     * Updates user's profile info.
     * @param pseudoU new pseudo
     * @param emailU new email
     * @param introductionU new introduction text
     * @param session HTTP session for current user
     * @return redirect to updated profile
     */
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

    /**
     * Updates user's password after checking current password.
     * @param currentP current password input
     * @param newP new password input
     * @param session HTTP session for current user
     * @param referer referring URL
     * @return redirect to referer or profile modification page
     */
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

    /**
     * Adds the current user to a group.
     * @param session HTTP session for current user
     * @param idGrp group id
     * @param referer referring URL
     * @return redirect to referer or groups page
     */
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

    /**
     * Removes the current user from a group, deletes group if user is creator.
     * @param session HTTP session for current user
     * @param idGrp group id
     * @param referer referring URL
     * @return redirect to referer or groups page
     */
    @RequestMapping("/quitterGroupe")
    public String quitterGroupe(HttpSession session,
                                @RequestParam int idGrp,
                                @RequestHeader(value = "Referer", required = false) String referer) {
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

    /**
     * Deletes a group.
     * @param session HTTP session for current user
     * @param idGrp group id
     * @return redirect to groups page
     */
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
                               @RequestHeader(value = "Referer", required = false) String referer) throws IOException {
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

    public void setGroupeService(GroupeService groupeService) {
        this.groupeService = groupeService;
    }


}
