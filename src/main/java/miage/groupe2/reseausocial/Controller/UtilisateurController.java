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
 * Contrôleur Spring MVC gérant les opérations relatives aux utilisateurs du réseau social.
 * <p>
 * Ce contrôleur permet la gestion de l'inscription, de la recherche d'utilisateurs,
 * de la gestion des amis, la modification du profil, la gestion des groupes, et le
 * téléchargement de l'avatar utilisateur.
 * </p>
 *
 * <p>URL de base mappée : <code>/user</code></p>
 *
 * @author Groupe 2 MIAGE
 */
@Controller
@RequestMapping("/user")
public class UtilisateurController {

    /**
     * URL de redirection vers la page de connexion.
     */
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
     * Affiche le formulaire d'inscription d'un nouvel utilisateur.
     *
     * @param model modèle pour la vue
     * @return nom de la vue du formulaire d'inscription ("form-register")
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "form-register";
    }

    /**
     * Traite l'inscription d'un nouvel utilisateur en hachant son mot de passe.
     *
     * @param utilisateur objet utilisateur provenant du formulaire
     * @param model modèle pour transmettre les erreurs éventuelles à la vue
     * @return redirection vers la page de login si succès, sinon retour au formulaire d'inscription
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

    /**
     * Recherche des utilisateurs dont le nom contient la chaîne donnée, sauf l'utilisateur connecté.
     *
     * @param nom partie du nom recherché
     * @param model modèle pour transmettre la liste des utilisateurs à la vue
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @return nom de la vue affichant les résultats de la recherche ("search_results")
     */
    @GetMapping("/rechercher")
    public String rechercherUtilisateurs(@RequestParam("nom") String nom, Model model, HttpSession session) {
        Utilisateur userConnecte = utilisateurService.getUtilisateurFromSession(session);

        List<Utilisateur> utilisateurs = utilisateurRepository.findByNomUContainingIgnoreCase(nom);

        // Supprimer l'utilisateur connecté des résultats
        utilisateurs.removeIf(u -> u.getIdUti().equals(userConnecte.getIdUti()));

        model.addAttribute("utilisateurs", utilisateurs);
        return "search_results";
    }

    /**
     * Affiche la liste des amis de l'utilisateur connecté.
     *
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @param model modèle pour transmettre la liste des amis à la vue
     * @return nom de la vue affichant les amis ("listeamis")
     */
    @GetMapping("/mes-amis")
    public String voirMesAmis(HttpSession session, Model model) {
        Utilisateur utilisateurAvecAmis = utilisateurService.getUtilisateurFromSession(session);
        model.addAttribute("amis", utilisateurAvecAmis.getAmis());
        return "listeamis";
    }

    /**
     * Supprime un ami de la liste d'amis de l'utilisateur connecté.
     *
     * @param idAmi identifiant de l'ami à supprimer
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @param redirectAttributes attributs pour transmettre messages flash
     * @param referer URL de la page appelante pour redirection sûre
     * @return redirection vers la page précédente ou vers la liste d'amis
     */
    @PostMapping("/supprimer-ami")
    public String supprimerAmi(@RequestParam("idAmi") Integer idAmi,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               @RequestHeader(value = "Referer", required = false) String referer
    ) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurFromSession(session);
        Utilisateur ami = utilisateurRepository.findByIdUti(idAmi);

        if (ami != null) {
            utilisateur.getAmis().remove(ami);
            ami.getAmis().remove(utilisateur); // suppression bilatérale
            utilisateurRepository.save(utilisateur);
            utilisateurRepository.save(ami);

            redirectAttributes.addFlashAttribute("succes", "Ami supprimé avec succès.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer cet ami.");
        }
        return RedirectUtil.getSafeRedirectUrl(referer,"redirect:/user/mes-amis");
    }

    /**
     * Affiche le profil d'un utilisateur donné, avec ses posts, reposts et amis limités.
     *
     * @param type type de contenu affiché ("post" ou "repost")
     * @param id identifiant de l'utilisateur dont on affiche le profil
     * @param model modèle pour transmettre les données à la vue
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @return nom de la vue du profil utilisateur ("profil_user")
     */
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
     * Affiche la page de modification du profil utilisateur.
     *
     * @return nom de la vue des paramètres ("setting")
     */
    @GetMapping("/modifierProfil")
    public String modifierProfil() {
        return "setting";
    }

    /**
     * Modifie les informations de profil de l'utilisateur connecté.
     *
     * @param pseudoU nouveau pseudo
     * @param emailU nouvelle adresse email
     * @param introductionU nouvelle introduction personnelle
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @return redirection vers le profil utilisateur mis à jour
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
     * Modifie le mot de passe de l'utilisateur connecté après vérification du mot de passe actuel.
     *
     * @param currentP mot de passe actuel
     * @param newP nouveau mot de passe
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @param referer URL de la page appelante pour redirection sûre
     * @return redirection vers la page précédente ou vers la page de modification du profil
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
     * Permet à un utilisateur de rejoindre un groupe.
     *
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @param idGrp identifiant du groupe à rejoindre
     * @param referer URL de la page appelante pour redirection sûre
     * @return redirection vers la page précédente ou vers la liste des groupes
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
     * Permet à un utilisateur de quitter un groupe.
     * Si l'utilisateur est le créateur du groupe, le groupe est supprimé.
     *
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @param idGrp identifiant du groupe à quitter
     * @param referer URL de la page appelante pour redirection sûre
     * @return redirection vers la page précédente ou vers la liste des groupes
     */
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

    /**
     * Supprime un groupe créé par l'utilisateur connecté.
     *
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @param idGrp identifiant du groupe à supprimer
     * @return redirection vers la liste des groupes
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

    /**
     * Permet à un utilisateur de téléverser une image d'avatar.
     * L'image est recadrée en carré centré avant sauvegarde.
     *
     * @param file fichier image uploadé
     * @param session session HTTP pour récupérer l'utilisateur connecté
     * @param referer URL de la page appelante pour redirection sûre
     * @return redirection vers la page précédente ou vers le profil utilisateur
     * @throws IOException en cas d'erreur lors de la lecture du fichier
     */
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
