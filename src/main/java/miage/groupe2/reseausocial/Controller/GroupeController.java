package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.GroupeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/groupe")
public class GroupeController {

    @Autowired
    GroupeRepository groupeRepository;

    @Autowired
    UtilisateurRepository utilisateurRepository;

    @Autowired
    GroupeService groupeService;

    @RequestMapping("/list")
    public String GroupeList(
            Model model
    ) {
        List<Groupe> groupes = groupeRepository.findAll();
        model.addAttribute("groupes", groupes);
        return "listGroupe";
    }

    @GetMapping("/creer")
    public String creerGroupe(
            Model model
    ){
        model.addAttribute("groupe", new Groupe());
        return "creerGroupe";
    }

    @PostMapping("/creer")
    public String creerGroupe(
            HttpSession session,
            @ModelAttribute Groupe groupe,
            Model model
    ){
        Utilisateur userSession = (Utilisateur) session.getAttribute("user");
        Utilisateur user = utilisateurRepository.findByidUti(userSession.getIdUti());

        groupeService.createGroupe(user, groupe);

        session.setAttribute("user", user);
        return "redirect:/list";
    }


}
