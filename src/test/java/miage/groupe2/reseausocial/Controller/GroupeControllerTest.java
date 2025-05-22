package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Post;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.GroupeService;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GroupeControllerTest {

    private GroupeController controller;
    private GroupeRepository groupeRepository;
    private UtilisateurRepository utilisateurRepository;
    private UtilisateurService utilisateurService;
    private GroupeService groupeService;
    private HttpSession session;
    private Model model;

    @BeforeEach
    public void setUp() {
        controller = new GroupeController();

        groupeRepository = mock(GroupeRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        utilisateurService = mock(UtilisateurService.class);
        groupeService = mock(GroupeService.class);
        session = mock(HttpSession.class);
        model = mock(Model.class);

        controller.groupeRepository = groupeRepository;
        controller.utilisateurRepository = utilisateurRepository;
        controller.setUtilisateurService(utilisateurService);
        controller.groupeService = groupeService;
    }

    @Test
    public void testIndex() {
        String view = controller.index(model);
        assertEquals("redirect:/groupe/list", view);
    }

    @Test
    public void testGroupeList() {
        Utilisateur user = new Utilisateur();
        List<Groupe> allGroupes = new ArrayList<>();

        Groupe g1 = new Groupe();
        g1.setMembres(new ArrayList<>());
        Groupe g2 = new Groupe();
        g2.setMembres(new ArrayList<>());

        allGroupes.add(g1);
        allGroupes.add(g2);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findAll()).thenReturn(allGroupes);

        user.setGroupesAppartenance(new ArrayList<>());
        user.setGroupes(new ArrayList<>());

        String view = controller.groupeList(model, session);
        assertEquals("groups", view);
    }

    @Test
    public void testCreerGroupe() {
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();
        String referer = "/groupe/list";

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String view = controller.creerGroupe(session, groupe, model, referer);
        verify(groupeService).createGroupe(user, groupe);
        verify(session).setAttribute("user", user);
        assertEquals("redirect:redirect:/groupe/list", view);
    }

    @Test
    public void testAfficherGroupe_NotFound() {
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(null);
        String view = controller.afficherGroupe(1, model, session);
        assertEquals("redirect:/groupe/list", view);
    }

    @Test
    public void testAfficherGroupe_Found() {
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setPosts(new ArrayList<>());
        groupe.setMembres(new ArrayList<>());

        Utilisateur user = new Utilisateur();
        user.setGroupesAppartenance(new ArrayList<>());
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findAll()).thenReturn(List.of(groupe));

        String view = controller.afficherGroupe(1, model, session);
        assertEquals("group_detail", view);
    }

    @Test
    public void testPosterDansGroupe() {
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setPosts(new ArrayList<>());
        Post post = new Post();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);

        String view = controller.posterDansGroupe(1, post, session);
        assertEquals("redirect:/groupe/1", view);
        assertEquals(post.getGroupe(), groupe);
        assertEquals(post.getCreateur(), user);
        verify(groupeRepository).save(groupe);
    }

    @Test
    public void testSupprimerMembreDuGroupe() {
        Utilisateur createur = new Utilisateur();
        Utilisateur membre = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setCreateur(createur);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(createur);
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        when(utilisateurRepository.findByIdUti(2)).thenReturn(membre);

        String view = controller.supprimerMembreDuGroupe(1, 2, session);
        assertEquals("redirect:/groupe/1", view);
        verify(groupeService).quitterGroupe(membre, 1);
    }
}
