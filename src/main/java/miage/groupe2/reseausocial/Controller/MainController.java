package miage.groupe2.reseausocial.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @RequestMapping("/home")
    public String homepage(){
        return "redirect:/home.html";
    }
}
