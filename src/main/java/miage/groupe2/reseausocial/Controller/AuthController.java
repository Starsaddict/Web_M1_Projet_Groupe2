package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for authentication.
 */

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;



    /**
     * Displays the login page.
     *
     * @return the login view name
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "form-login";
    }


    /**
     * Handles user authentication.
     *
     * @param email email entered by the user
     * @param mdp   password entered by the user
     * @param model model to pass messages
     * @return redirect or login page if failed
     */
    @PostMapping("/login")
    public String authenticate(@RequestParam String email,
                               @RequestParam String mdp,
                               Model model,
                               HttpSession session
    ) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailU(email);
        if (utilisateur != null && BCrypt.checkpw(mdp, utilisateur.getMdpU())) {
            session.setAttribute("user", utilisateur);
        } else {
            model.addAttribute("error", "wrong password");
        }
        return "redirect:/home";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "form-login";
    }


}
