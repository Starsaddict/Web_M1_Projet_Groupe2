package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.UtilisateurService;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DemandeAmiControllerTest {

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

    private Utilisateur userConnecte;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userConnecte = new Utilisateur();
        userConnecte.setIdUti(1);
        userConnecte.setNomU("TestUser");

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(userConnecte);
    }

    @Test
    void testEnvoyerDemandeAmi_UserNotFound() {
        int idAmi = 2;
        String nomRecherche = "toto";

        when(utilisateurRepository.findByidUti(idAmi)).thenReturn(null);

        String redirect = controller.envoyerDemandeAmi(idAmi, nomRecherche, session, redirectAttributes, null);

        verify(redirectAttributes).addFlashAttribute("error", "Utilisateur non trouvé.");
        assertEquals("redirect:/user/rechercher?nom=" + nomRecherche, redirect);
    }

    @Test
    void testEnvoyerDemandeAmi_SameUser() {
        int idAmi = userConnecte.getIdUti();

        String redirect = controller.envoyerDemandeAmi(idAmi, "toto", session, redirectAttributes, null);

        verify(redirectAttributes).addFlashAttribute("error", "Vous ne pouvez pas vous ajouter vous-même.");
        assertEquals("redirect:/user/rechercher?nom=toto", redirect);
    }

    @Test
    void testEnvoyerDemandeAmi_Success() {
        int idAmi = 2;
        String nomRecherche = "toto";

        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(idAmi);
        recepteur.setNomU("AmiTest");

        when(utilisateurRepository.findByidUti(idAmi)).thenReturn(recepteur);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(
                userConnecte.getIdUti(), idAmi, List.of("en attente"))).thenReturn(false);
        when(demandeAmiRepository.sontDejaAmis(userConnecte.getIdUti(), idAmi)).thenReturn(false);

        String redirect = controller.envoyerDemandeAmi(idAmi, nomRecherche, session, redirectAttributes, null);

        verify(demandeAmiRepository).save(any(DemandeAmi.class));
        verify(redirectAttributes).addFlashAttribute("success", "Demande d'ami envoyée à " + recepteur.getNomU() + ".");
        assertEquals("redirect:/user/rechercher?nom=" + nomRecherche, redirect);
    }

    @Test
    void testAccepterDemande_RedirectsToHome() {
        int idDemande = 10;

        DemandeAmi demande = new DemandeAmi();
        demande.setStatut("en attente");
        demande.setDemandeur(new Utilisateur());
        demande.setRecepteur(userConnecte);

        when(demandeAmiRepository.findByIdDA(idDemande)).thenReturn(demande);

        String redirect = controller.accepterDemande(idDemande, session, redirectAttributes, null);

        assertEquals("redirect:/home", redirect);
        assertEquals("acceptée", demande.getStatut());

        verify(demandeAmiRepository).save(demande);
        verify(demandeAmiRepository).ajouterLienAmitie(demande.getDemandeur().getIdUti(), userConnecte.getIdUti());
        verify(redirectAttributes).addFlashAttribute("succes", "Demande d'ami acceptée.");
    }

    @Test
    void testRefuserDemande_RedirectsToHome() {
        int idDemande = 10;

        DemandeAmi demande = new DemandeAmi();
        demande.setStatut("en attente");
        demande.setRecepteur(userConnecte);

        when(demandeAmiRepository.findById(idDemande)).thenReturn(Optional.of(demande));

        String redirect = controller.refuserDemande(idDemande, session, redirectAttributes, null);

        assertEquals("redirect:/home", redirect);
        assertEquals("refusée", demande.getStatut());

        verify(demandeAmiRepository).save(demande);
        verify(redirectAttributes).addFlashAttribute("succes", "Demande d'ami refusée.");
    }
}
