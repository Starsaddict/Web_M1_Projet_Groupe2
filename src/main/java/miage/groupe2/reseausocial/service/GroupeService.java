package miage.groupe2.reseausocial.service;

import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
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

    public void createGroupe(Utilisateur user, Groupe groupe) {
        long timestamp = System.currentTimeMillis();
        groupe.setDateCreation(timestamp);
        groupe.setCreateur(user);

        if (user.getGroupes() == null) {
            user.setGroupes(new ArrayList<>());
        }
        if (!user.getGroupes().contains(groupe)) {
            user.getGroupes().add(groupe);
        }

        utilisateurRepository.save(user);
        groupeRepository.save(groupe);
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

    public void quitterGroupe(Utilisateur user, Groupe groupe) {
        if( user.getGroupesAppartenance() != null && user.getGroupesAppartenance().contains(groupe)){
            user.getGroupesAppartenance().remove(groupe);
        }
        if(groupe.getMembres() != null && groupe.getMembres().contains(user)){
            groupe.getMembres().remove(user);
        }
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

    public void deleteGroupe(Integer id) {
        groupeRepository.deleteById(id);
    }

    public boolean supprimerGroupe(Utilisateur user, Groupe groupe) {
        if(groupe.getMembres()==user){
            groupeRepository.delete(groupe);
            return true;
        }
        return false;
    }

    public boolean supprimerGroupe(Utilisateur user, int groupeId) {
        Groupe groupe = groupeRepository.findGroupeByidGrp(groupeId);
        if(groupe.getMembres()==user){
            groupeRepository.delete(groupe);
            return true;
        }
        return false;
    }

}

