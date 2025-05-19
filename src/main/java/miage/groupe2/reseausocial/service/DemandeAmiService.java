package miage.groupe2.reseausocial.service;


import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemandeAmiService {

    @Autowired
    UtilisateurService utilisateurService;
    @Autowired
    private DemandeAmiRepository demandeAmiRepository;

    public List<DemandeAmi> getDemandeMessages(Utilisateur user) {
        List<DemandeAmi> demandes = demandeAmiRepository.findByRecepteur(user);
        System.out.println("找到的好友请求数量: " + demandes.size());
        return demandes.stream()
                .filter(demandeAmi -> "en attente".equals(demandeAmi.getStatut()))
                .toList();
    }


}
