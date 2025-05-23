package miage.groupe2.reseausocial.service;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {
    @Autowired
    UtilisateurRepository repo;

    public Utilisateur getUtilisateurFromSession(HttpSession session) {
        Object obj = session.getAttribute("user");
        if (obj instanceof Utilisateur user) {
            return repo.findByidUti(user.getIdUti());
        }
        return null;
    }


}
