package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;


@Controller
@RequestMapping("/user")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

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

    @RequestMapping("/{id:[0-9]+}")
    public String userProfil(
            @PathVariable long id,
            Model model
    ){
        Utilisateur user = utilisateurRepository.findByidUti(id);
        model.addAttribute("user", user);
        List<Post> posts = user.getPosts().stream()
                .sorted((p1, p2) -> Long.compare(p2.getDatePost(), p1.getDatePost())) // 降序
                .limit(3)
                .toList();

        model.addAttribute("posts", posts);
        return "user_profil";
    }

    @GetMapping("/{id}/modifierProfil")
    public String modifierProfil(@PathVariable long id, Model model) {
        Utilisateur user = utilisateurRepository.findByidUti(id);
        model.addAttribute("user", user);
        return "modifier_profil";
    }

    @PostMapping("/{id}/modifierProfil")
    public String modifierProfil(
            @PathVariable long id,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String email,
            HttpSession session
            ) {
        Utilisateur user = utilisateurRepository.findByidUti(id);
        user.setNomU(nom);
        user.setPrenomU(prenom);
        user.setEmailU(email);
        utilisateurRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/user/"+id;
    }

    @GetMapping("/{id}/modifierPassword")
    public String modifierPassword(
            @PathVariable long id,
            Model model
    ){
        Utilisateur user = utilisateurRepository.findByidUti(id);
        model.addAttribute("user", user);
        return "modifier_password";
    }

    @PostMapping("/{id}/modifierPassword")
    public String modifierPassword(
            @PathVariable long id,
            @RequestParam String password,
            HttpSession session
    ){
        Utilisateur user = utilisateurRepository.findByidUti(id);
        password = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setMdpU(password);
        utilisateurRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/user/"+id;
    }




}
