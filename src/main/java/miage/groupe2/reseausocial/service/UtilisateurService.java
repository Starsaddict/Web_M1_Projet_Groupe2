package miage.groupe2.reseausocial.service;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {
    @Autowired
    UtilisateurRepository repo;

    public Utilisateur getUtilisateurFromSession(
            HttpSession session
    ) {
        Utilisateur userSession = (Utilisateur)session.getAttribute("user");
        Utilisateur user = repo.findByidUti(userSession.getIdUti());
        return user;
    }
}
