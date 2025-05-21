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
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GroupeControllerTest {

    @InjectMocks
    GroupeController groupeController;

    @Mock
    GroupeRepository groupeRepository;

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    GroupeService groupeService;

    @Mock
    UtilisateurService utilisateurService;

    @Mock
    HttpSession session;

    @Mock
    Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIndex() {
        String result = groupeController.index(model);
        assertEquals("redirect:/groupe/list", result);
    }

    @Test
    void testGroupeList() {
        Utilisateur user = new Utilisateur();

        Groupe g1 = new Groupe();
        g1.setMembres(new ArrayList<>());
        Groupe g2 = new Groupe();
        g2.setMembres(new ArrayList<>());

        List<Groupe> allGroupes = List.of(g1, g2);

        user.setGroupesAppartenance(List.of(g1));
        user.setGroupes(List.of(g2));

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findAll()).thenReturn(allGroupes);

        String view = groupeController.groupeList(model, session);

        verify(model).addAttribute(eq("recommandGroupes"), anyList());
        verify(model).addAttribute("monGroupes", user.getGroupesAppartenance());
        verify(model).addAttribute("monGroupCreer", user.getGroupes());
        assertEquals("groups", view);
    }


    @Test
    void testCreerGroupe() {
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        String referer = "/groupe/list";
        String result = groupeController.creerGroupe(session, groupe, model, referer);

        verify(groupeService).createGroupe(user, groupe);
        verify(session).setAttribute("user", user);
        assertTrue(result.contains("redirect:"));
    }

    @Test
    void testAfficherGroupe() {
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setPosts(new ArrayList<>());
        groupe.setMembres(new ArrayList<>());

        Utilisateur user = new Utilisateur();
        user.setGroupesAppartenance(List.of(groupe));

        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findAll()).thenReturn(List.of(new Groupe(), groupe));

        String view = groupeController.afficherGroupe(1, model, session);

        verify(model).addAttribute(eq("groupe"), eq(groupe));
        verify(model).addAttribute(eq("membres"), eq(groupe.getMembres()));
        verify(model).addAttribute(eq("posts"), anyList());
        verify(model).addAttribute(eq("post"), any(Post.class));
        verify(model).addAttribute(eq("estMembre"), eq(true));
        verify(model).addAttribute(eq("groupes"), anyList());
        assertEquals("group_detail", view);
    }

    @Test
    void testPosterDansGroupe() {
        Utilisateur user = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setPosts(new ArrayList<>());

        Post post = new Post();

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);

        String result = groupeController.posterDansGroupe(1, post, session);

        assertTrue(groupe.getPosts().contains(post));
        assertEquals("redirect:/groupe/1", result);
    }

    @Test
    void testSupprimerMembreDuGroupe() {
        Utilisateur user = new Utilisateur();
        Utilisateur membre = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setCreateur(user);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        when(utilisateurRepository.findByIdUti(2)).thenReturn(membre);

        String result = groupeController.supprimerMembreDuGroupe(1, 2, session);

        verify(groupeService).quitterGroupe(membre, groupe.getIdGrp());
        assertEquals("redirect:/groupe/1", result);
    }
}
