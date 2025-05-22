package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Contrôleur gérant l'authentification des utilisateurs.
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Affiche la page de connexion.
     *
     * @return le nom de la vue du formulaire de connexion
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "form-login";
    }

    /**
     * Traite l'authentification d'un utilisateur.
     *
     * @param email l'email saisi par l'utilisateur
     * @param mdp le mot de passe saisi par l'utilisateur
     * @param model modèle pour transmettre des messages à la vue
     * @param session session HTTP courante
     * @return redirection vers la page d'accueil si succès, sinon retour à la page de connexion
     */
    @PostMapping("/login")
    public String authenticate(@RequestParam String email,
                               @RequestParam String mdp,
                               Model model,
                               HttpSession session) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailU(email);
        if (utilisateur != null && BCrypt.checkpw(mdp, utilisateur.getMdpU())) {
            session.setAttribute("user", utilisateur);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Mot de passe incorrect");
            return "form-login";
        }
    }

    /**
     * Déconnecte l'utilisateur en invalidant la session.
     *
     * @param session session HTTP courante
     * @return nom de la vue de connexion
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "form-login";
    }
}
