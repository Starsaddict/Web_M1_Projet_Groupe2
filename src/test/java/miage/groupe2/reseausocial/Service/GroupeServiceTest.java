package miage.groupe2.reseausocial.Service;

import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.Repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GroupeServiceTest {

    private GroupeService groupeService;
    private GroupeRepository groupeRepository;
    private UtilisateurRepository utilisateurRepository;
    private PostRepository postRepository;

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @BeforeEach
    void setUp() throws Exception {
        groupeService = new GroupeService();
        groupeRepository = mock(GroupeRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        postRepository = mock(PostRepository.class);

        setPrivateField(groupeService, "groupeRepository", groupeRepository);
        setPrivateField(groupeService, "utilisateurRepository", utilisateurRepository);
        setPrivateField(groupeService, "postRepository", postRepository);
    }

    @Test
    void testGetGroupeByidGrp() {
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        Groupe result = groupeService.getGroupeByidGrp(1);
        assertEquals(groupe, result);
    }

    @Test
    void testCreateGroupeAddsGroupToUser() {
        Utilisateur user = new Utilisateur();
        user.setGroupes(new ArrayList<>());
        user.setGroupesAppartenance(new ArrayList<>());
        Groupe groupe = new Groupe();

        when(groupeRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(utilisateurRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        groupeService.createGroupe(user, groupe);

        assertTrue(user.getGroupes().contains(groupe));
        assertTrue(user.getGroupesAppartenance().contains(groupe));
        assertNotNull(groupe.getDateCreation());
        verify(groupeRepository).save(groupe);
        verify(utilisateurRepository).save(user);
    }

    @Test
    void testJoinGroupeAddsUserToGroupeAndUser() {
        Utilisateur user = new Utilisateur();
        user.setGroupesAppartenance(new ArrayList<>());
        Groupe groupe = new Groupe();
        groupe.setMembres(new ArrayList<>());

        when(groupeRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(utilisateurRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        groupeService.joinGroupe(user, groupe);

        assertTrue(groupe.getMembres().contains(user));
        assertTrue(user.getGroupesAppartenance().contains(groupe));
        verify(groupeRepository).save(groupe);
        verify(utilisateurRepository).save(user);
    }

    @Test
    void testQuitterGroupeRemovesUserFromGroupeAndUser() {
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setMembres(new ArrayList<>());
        groupe.getMembres().add(user);
        user.setGroupesAppartenance(new ArrayList<>());
        user.getGroupesAppartenance().add(groupe);

        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        when(groupeRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(utilisateurRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        groupeService.quitterGroupe(user, 1);

        assertFalse(groupe.getMembres().contains(user));
        assertFalse(user.getGroupesAppartenance().contains(groupe));
        verify(groupeRepository).save(groupe);
        verify(utilisateurRepository).save(user);
    }

    @Test
    void testGetAllGroupes() {
        List<Groupe> groupes = new ArrayList<>();
        groupes.add(new Groupe());
        when(groupeRepository.findAll()).thenReturn(groupes);
        List<Groupe> result = groupeService.getAllGroupes();
        assertEquals(groupes, result);
    }

    @Test
    void testUpdateGroupeExists() {
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        when(groupeRepository.existsById(1)).thenReturn(true);
        when(groupeRepository.save(groupe)).thenReturn(groupe);
        Groupe result = groupeService.updateGroupe(groupe);
        assertEquals(groupe, result);
    }

    @Test
    void testUpdateGroupeNotExists() {
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        when(groupeRepository.existsById(1)).thenReturn(false);
        Groupe result = groupeService.updateGroupe(groupe);
        assertNull(result);
    }

    @Test
    void testDeleteGroupe() {
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setMembres(new ArrayList<>());
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        groupeService.deleteGroupe(1);
        assertTrue(groupe.getMembres().isEmpty());
        verify(groupeRepository).delete(groupe);
    }

    @Test
    void testSupprimerGroupeByUserAndGroupe() {
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setMembres(new ArrayList<>());

        Utilisateur membre = new Utilisateur();
        membre.setGroupesAppartenance(new ArrayList<>());
        groupe.getMembres().add(membre);

        when(groupeRepository.findById(1)).thenReturn(Optional.of(groupe));
        doNothing().when(groupeRepository).delete(groupe);
        when(utilisateurRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        groupeService.supprimerGroupe(user, groupe);

        verify(groupeRepository).delete(groupe);
        assertFalse(membre.getGroupesAppartenance().contains(groupe));
    }

    @Test
    void testSupprimerGroupeByUserAndId() {
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setMembres(new ArrayList<>());

        Utilisateur membre = new Utilisateur();
        membre.setGroupesAppartenance(new ArrayList<>());
        groupe.getMembres().add(membre);

        when(groupeRepository.findById(1)).thenReturn(Optional.of(groupe));
        doNothing().when(groupeRepository).delete(groupe);
        when(utilisateurRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        groupeService.supprimerGroupe(user, 1);

        verify(groupeRepository).delete(groupe);
        assertFalse(membre.getGroupesAppartenance().contains(groupe));
    }
}
