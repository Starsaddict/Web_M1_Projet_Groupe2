package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;


@Controller
public class MainController {

    @Autowired
    UtilisateurRepository utilisateurRepository;
    @GetMapping("/home")
    public String homepage(
            Model model,
            HttpSession session
    ) {
        Utilisateur userSession = (Utilisateur) session.getAttribute("user");
        if (userSession == null) {
            return "redirect:/auth/login";
        }
        Utilisateur user = utilisateurRepository.findByidUti(userSession.getIdUti());
        List<Utilisateur> friends = user.getAmis().stream()
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("friends", friends);
        return "feed";
    }

    // Redirection de la racine vers la page home
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home.html";
    }

}
