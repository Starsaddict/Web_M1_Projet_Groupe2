package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new Utilisateur());
        return "register";
    }

    /**
     * Handles user registration with password hashing.
     *
     * @param utilisateur the user to register
     * @return redirection to login page after successful registration
     */

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Utilisateur utilisateur) {
        String hashedPassword = BCrypt.hashpw(utilisateur.getMdpU(), BCrypt.gensalt());
        utilisateur.setMdpU(hashedPassword);
        utilisateurRepository.save(utilisateur);
        return "redirect:/auth/login";
    }

}
