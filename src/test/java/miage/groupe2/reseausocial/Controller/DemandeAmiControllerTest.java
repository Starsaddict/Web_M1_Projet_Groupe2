package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DemandeAmiControllerTest {

    @InjectMocks
    private DemandeAmiController controller;

    @Mock
    private DemandeAmiRepository demandeAmiRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Captor
    private ArgumentCaptor<DemandeAmi> demandeCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAccepterDemande() {
        Utilisateur u = new Utilisateur();
        u.setIdUti(1);
        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(2);
        DemandeAmi d = new DemandeAmi();
        d.setIdDA(10);
        d.setRecepteur(u);
        d.setDemandeur(demandeur);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);
        when(demandeAmiRepository.findByIdDA(10)).thenReturn(d);

        String result = controller.accepterDemande(10, session, redirectAttributes, "/previous");

        verify(demandeAmiRepository).save(d);
        verify(demandeAmiRepository).ajouterLienAmitie(demandeur.getIdUti(), u.getIdUti());
        verify(redirectAttributes).addFlashAttribute(eq("succes"), anyString());
        assertEquals("/previous", result);
    }

    @Test
    void testRefuserDemande() {
        Utilisateur u = new Utilisateur();
        u.setIdUti(1);
        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(2);
        DemandeAmi d = new DemandeAmi();
        d.setIdDA(10);
        d.setRecepteur(u);
        d.setDemandeur(demandeur);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);
        when(demandeAmiRepository.findById(10)).thenReturn(Optional.of(d));

        String result = controller.refuserDemande(10, session, redirectAttributes, "/previous");

        verify(demandeAmiRepository).save(d);
        verify(redirectAttributes).addFlashAttribute(eq("succes"), anyString());
        assertEquals("/previous", result);
    }

    @Test
    void testEnvoyerDemandeAmi() {
        Utilisateur u = new Utilisateur();
        u.setIdUti(1);
        Utilisateur ami = new Utilisateur();
        ami.setIdUti(2);
        ami.setNomU("Ami");

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);
        when(utilisateurRepository.findByidUti(2)).thenReturn(ami);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(eq(1), eq(2), anyList())).thenReturn(false);
        when(demandeAmiRepository.sontDejaAmis(1, 2)).thenReturn(false);

        String result = controller.envoyerDemandeAmi(2, "Jean", session, redirectAttributes, "/previous");

        verify(demandeAmiRepository).save(demandeCaptor.capture());
        DemandeAmi saved = demandeCaptor.getValue();
        assertEquals("en attente", saved.getStatut());
        assertEquals(u, saved.getDemandeur());
        assertEquals(ami, saved.getRecepteur());

        verify(redirectAttributes).addFlashAttribute(eq("success"), contains("Ami"));
        assertEquals("/previous", result);
    }

    @Test
    void testEnvoyerDemandeAmiVersSoiMeme() {
        Utilisateur u = new Utilisateur();
        u.setIdUti(1);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);

        String result = controller.envoyerDemandeAmi(1, "Jean", session, redirectAttributes, "/previous");

        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
        assertEquals("redirect:/user/rechercher?nom=Jean", result);
    }

    @Test
    void testEnvoyerDemandeAmiUtilisateurInexistant() {
        Utilisateur u = new Utilisateur();
        u.setIdUti(1);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);
        when(utilisateurRepository.findByidUti(2)).thenReturn(null);

        String result = controller.envoyerDemandeAmi(2, "Jean", session, redirectAttributes, "/previous");

        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Utilisateur non trouvé."));
        assertEquals("redirect:/user/rechercher?nom=Jean", result);
    }

    @Test
    void testEnvoyerDemandeAmiDejaExistante() {
        Utilisateur u = new Utilisateur();
        u.setIdUti(1);
        Utilisateur ami = new Utilisateur();
        ami.setIdUti(2);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);
        when(utilisateurRepository.findByidUti(2)).thenReturn(ami);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(eq(1), eq(2), anyList())).thenReturn(true);

        String result = controller.envoyerDemandeAmi(2, "Jean", session, redirectAttributes, "/previous");

        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("existe déjà"));
        assertEquals("redirect:/user/rechercher?nom=Jean", result);
    }

    @Test
    void testEnvoyerDemandeAmiDejaAmis() {
        Utilisateur u = new Utilisateur();
        u.setIdUti(1);
        Utilisateur ami = new Utilisateur();
        ami.setIdUti(2);

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(u);
        when(utilisateurRepository.findByidUti(2)).thenReturn(ami);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(eq(1), eq(2), anyList())).thenReturn(false);
        when(demandeAmiRepository.sontDejaAmis(1, 2)).thenReturn(true);

        String result = controller.envoyerDemandeAmi(2, "Jean", session, redirectAttributes, "/previous");

        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("existe déjà"));
        assertEquals("redirect:/user/rechercher?nom=Jean", result);
    }
}
