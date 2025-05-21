package miage.groupe2.reseausocial.service;

import jakarta.transaction.Transactional;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.PostRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupeService {

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private PostRepository postRepository;

    public Groupe getGroupeByidGrp(int id) {
        return groupeRepository.findGroupeByidGrp(id);
    }

    public void createGroupe(Utilisateur user, Groupe groupe) {
        long timestamp = System.currentTimeMillis();
        groupe.setDateCreation(timestamp);
        groupe.setCreateur(user);

        groupe = groupeRepository.save(groupe);

        if (user.getGroupes() == null) {
            user.setGroupes(new ArrayList<>());
        }
        if (!user.getGroupes().contains(groupe)) {
            user.getGroupes().add(groupe);
        }

        if (user.getGroupesAppartenance() == null) {
            user.setGroupesAppartenance(new ArrayList<>());
        }
        if (!user.getGroupesAppartenance().contains(groupe)) {
            user.getGroupesAppartenance().add(groupe);
        }

        utilisateurRepository.save(user);
    }


    public void joinGroupe(Utilisateur user, Groupe groupe) {
        if(user.getGroupesAppartenance() == null){
            user.setGroupesAppartenance(new ArrayList<>());
        }
        if (!user.getGroupesAppartenance().contains(groupe)) {
            user.getGroupesAppartenance().add(groupe);
        }

        if(groupe.getMembres() == null){
            groupe.setMembres(new ArrayList<>());
        }
        if (!groupe.getMembres().contains(user)) {
            groupe.getMembres().add(user);
        }
        groupeRepository.save(groupe);
        utilisateurRepository.save(user);
    }

    public void quitterGroupe(Utilisateur user, Integer id) {

        Groupe groupe = groupeRepository.findGroupeByidGrp(id);
        groupe.getMembres().remove(user);
        user.getGroupesAppartenance().remove(groupe);
        groupeRepository.save(groupe);
        utilisateurRepository.save(user);

    }


    public List<Groupe> getAllGroupes() {
        return groupeRepository.findAll();
    }

    public Groupe updateGroupe(Groupe groupe) {
        if (groupeRepository.existsById(groupe.getIdGrp())) {
            return groupeRepository.save(groupe);
        }
        return null;
    }

    @Transactional
    public void deleteGroupe(Integer id) {
        Groupe groupe = groupeRepository.findGroupeByidGrp(id);
        if (groupe == null) return;

        groupe.getMembres().clear();

        groupeRepository.delete(groupe);
    }


    public void supprimerGroupe(Utilisateur user, Groupe groupe) {
        supprimerGroupe(user, groupe.getIdGrp());
    }

    public void supprimerGroupe(Utilisateur user, int idGroupe) {
        Groupe managedGroupe = groupeRepository
                .findById(idGroupe)
                .orElseThrow(() -> new IllegalArgumentException("Groupe non trouvé : " + idGroupe));

        for (Utilisateur membre : new ArrayList<>(managedGroupe.getMembres())) {
            membre.getGroupesAppartenance().remove(managedGroupe);
            utilisateurRepository.save(membre);
        }

        groupeRepository.delete(managedGroupe);
    }

}

