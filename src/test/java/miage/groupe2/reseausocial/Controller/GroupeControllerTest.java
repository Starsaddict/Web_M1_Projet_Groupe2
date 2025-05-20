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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GroupeControllerTest {

    @InjectMocks
    private GroupeController groupeController;

    @Mock
    private GroupeRepository groupeRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private GroupeService groupeService;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIndexRedirect() {
        String viewName = groupeController.index(model);
        assertEquals("redirect:/groupe/list", viewName);
    }

    @Test
    void testGroupeList() {
        List<Groupe> groupes = Arrays.asList(new Groupe(), new Groupe());
        when(groupeRepository.findAll()).thenReturn(groupes);

        String viewName = groupeController.GroupeList(model);

        verify(model).addAttribute("groupes", groupes);
        assertEquals("listGroupe", viewName);
    }

    @Test
    void testCreerGroupeGet() {
        String viewName = groupeController.creerGroupe(model);
        verify(model).addAttribute(eq("groupe"), any(Groupe.class));
        assertEquals("creerGroupe", viewName);
    }

    @Test
    void testCreerGroupePost() {
        Utilisateur sessionUser = new Utilisateur();
        sessionUser.setIdUti(1);
        Groupe groupe = new Groupe();

        when(session.getAttribute("user")).thenReturn(sessionUser);
        when(utilisateurRepository.findByidUti(1)).thenReturn(sessionUser);

        String viewName = groupeController.creerGroupe(session, groupe, model);

        verify(groupeService).createGroupe(sessionUser, groupe);
        verify(session).setAttribute("user", sessionUser);
        assertEquals("redirect:/groupe/list", viewName);
    }

    @Test
    void testAfficherGroupe_NotFound() {
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(null);
        String view = groupeController.afficherGroupe(1, model, session);
        assertEquals("redirect:/groupe/list", view);
    }

    @Test
    void testAfficherGroupe_FoundAndMember() {
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setPosts(new ArrayList<>());
        groupe.setMembres(new ArrayList<>());

        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        user.setGroupesAppartenance(List.of(groupe));

        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(1)).thenReturn(user);

        String view = groupeController.afficherGroupe(1, model, session);

        verify(model).addAttribute("groupe", groupe);
        verify(model).addAttribute("membres", groupe.getMembres());
        verify(model).addAttribute("posts", groupe.getPosts());
        verify(model).addAttribute(eq("post"), any(Post.class));
        verify(model).addAttribute("estMembre", true);
        assertEquals("groupe_detail", view);
    }

    @Test
    void testSupprimerMembreDuGroupe_Authorized() {
        Utilisateur createur = new Utilisateur();
        createur.setIdUti(1);
        Utilisateur membre = new Utilisateur();
        Groupe groupe = new Groupe();
        groupe.setIdGrp(1);
        groupe.setCreateur(createur);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(createur);
        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);
        when(utilisateurRepository.findByidUti(2)).thenReturn(membre);

        String view = groupeController.supprimerMembreDuGroupe(1, 2, session);

        verify(groupeService).quitterGroupe(membre, groupe);
        assertEquals("redirect:/groupe/1", view);
    }
}
