package miage.groupe2.reseausocial.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @GetMapping("/home")
    public String homepage() {
        return "redirect:/home.html";
    }

    // Redirection de la racine vers la page home
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home.html";
    }
}
