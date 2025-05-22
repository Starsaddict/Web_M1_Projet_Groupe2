package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.ImageUtil;
import miage.groupe2.reseausocial.Util.RedirectUtil;
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
    public String rechercherUtilisateurs(@RequestParam("nom") String nom, Model model, HttpSession session) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);
        List<Utilisateur> utilisateurs = utilisateurRepository.findByNomUContainingIgnoreCase(nom);
        utilisateurs.removeIf(u -> u.getIdUti().equals(userConnecte.getIdUti()));
        model.addAttribute("utilisateurs", utilisateurs);
        return "search_results";
    }

    /**
     * Displays the current user's friends list.
     * @param session HTTP session for current user
     * @param model model to bind data
     * @return friends list view
     */
    @GetMapping("/mes-amis")
    public String voirMesAmis(HttpSession session, Model model) {
        Utilisateur utilisateurAvecAmis = utilisateurService.getUtilisateurFromSession(session);
        model.addAttribute("amis", utilisateurAvecAmis.getAmis());
        return "listeamis";
    }

    /**
     * Removes a friend from the user's friends list.
     * @param idAmi id of friend to remove
     * @param session HTTP session for current user
     * @param redirectAttributes attributes for flash messages
     * @param referer referring URL
     * @return redirect to previous or default page
     */
    @PostMapping("/supprimer-ami")
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
        return RedirectUtil.getSafeRedirectUrl(referer,"redirect:/user/mes-amis");
    }

    /**
     * Displays a user's profile page.
     * @param type type of posts to show ("post" or "repost")
     * @param id user id
     * @param model model to bind data
     * @param session HTTP session for current user
     * @return user profile view
     */
    @GetMapping("/{id}/profil")
    public String userProfil(@RequestParam(value = "type", defaultValue = "post") String type,
                             @PathVariable Integer id,
                             Model model,
                             HttpSession session) {
        Utilisateur user = utilisateurRepository.findByIdUti(id);
        Utilisateur sessionUser = utilisateurService.getUtilisateurFromSession(session);

        model.addAttribute("user", user);
        List<Post> posts = user.getPosts().stream()
                .filter(i -> i.getGroupe()==null)
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost()))
                .limit(10)
                .toList();
        List<Post> reposts = user.getPostsRepostes().stream()
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost()))
                .limit(10)
                .toList();
        List<Utilisateur> Amis = user.getAmis().stream().limit(6).toList();

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
    public String modifierProfil(@RequestParam String pseudoU,
                                 @RequestParam String emailU,
                                 @RequestParam String introductionU,
                                 HttpSession session) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        user.setPseudoU(pseudoU);
        user.setEmailU(emailU);
        user.setIntroductionU(introductionU);
        utilisateurRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/user/"+user.getIdUti() + "/profil";
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
    public String modifierPassword(@RequestParam String currentP,
                                   @RequestParam String newP,
                                   HttpSession session,
                                   @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        if(BCrypt.checkpw(currentP, user.getMdpU())) {
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
    public String joinGroupe(HttpSession session,
                             @RequestParam int idGrp,
                             @RequestHeader(value = "Referer", required = false) String referer) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        Groupe groupe = groupeRepository.findGroupeByidGrp(idGrp);
        groupeService.joinGroupe(user,groupe);
        user.getGroupesAppartenance().size();
        session.setAttribute("user", user);
        return RedirectUtil.getSafeRedirectUrl(referer,"/user/mes-groupes");
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
        groupeService.quitterGroupe(user,idGrp);
        if (user.equals(groupe.getCreateur())) {
            groupeService.supprimerGroupe(user, groupe);
        }
        user.getGroupesAppartenance().size();
        session.setAttribute("user", user);
        return RedirectUtil.getSafeRedirectUrl(referer,"/user/mes-groupes");
    }

    /**
     * Deletes a group.
     * @param session HTTP session for current user
     * @param idGrp group id
     * @return redirect to groups page
     */
    @RequestMapping("/supprimerGroupe")
    public String supprimerGroupe(HttpSession session,
                                  @RequestParam int idGrp) {
        Utilisateur user = utilisateurService.getUtilisateurFromSession(session);
        groupeService.supprimerGroupe(user,idGrp);
        return "redirect:/user/mes-groupes";
    }

    /**
     * Uploads and crops user's avatar.
     * @param file uploaded avatar file
     * @param session HTTP session for current user
     * @param referer referring URL
     * @return redirect to referer or user profile
     * @throws IOException in case of file read errors
     */
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
}
