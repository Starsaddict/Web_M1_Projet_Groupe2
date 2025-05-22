package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DemandeAmiControllerTest {

    private DemandeAmiController controller;
    private DemandeAmiRepository demandeAmiRepository;
    private UtilisateurRepository utilisateurRepository;
    private UtilisateurService utilisateurService;
    private HttpSession session;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp() {
        controller = new DemandeAmiController();
        demandeAmiRepository = mock(DemandeAmiRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        utilisateurService = mock(UtilisateurService.class);
        session = mock(HttpSession.class);
        redirectAttributes = mock(RedirectAttributes.class);

        controller = new DemandeAmiController();
        inject(controller, "demandeAmiRepository", demandeAmiRepository);
        inject(controller, "utilisateurRepository", utilisateurRepository);
        inject(controller, "utilisateurService", utilisateurService);
    }

    @Test
    public void testAccepterDemande() {
        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(1);

        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(2);

        DemandeAmi demande = new DemandeAmi();
        demande.setRecepteur(recepteur);
        demande.setDemandeur(demandeur);
        demande.setStatut("en attente");

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(recepteur);
        when(demandeAmiRepository.findByIdDA(10)).thenReturn(demande);

        String result = controller.accepterDemande(10, session, redirectAttributes, "/test");

        assertEquals("redirect:/home", result);
        verify(demandeAmiRepository).save(demande);
        verify(demandeAmiRepository).ajouterLienAmitie(2, 1);
    }


    @Test
    public void testRefuserDemande() {
        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(1);
        DemandeAmi demande = new DemandeAmi();
        demande.setRecepteur(recepteur);
        demande.setStatut("en attente");

        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(recepteur);
        when(demandeAmiRepository.findById(20)).thenReturn(Optional.of(demande));

        String result = controller.refuserDemande(20, session, redirectAttributes, "/test");

        assertEquals("redirect:/home", result);
        verify(demandeAmiRepository).save(demande);
    }

    @Test
    public void testEnvoyerDemandeAmi() {
        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(1);
        demandeur.setAmis(new ArrayList<>());
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(demandeur);
        when(demandeAmiRepository.findByDemandeur(demandeur)).thenReturn(new ArrayList<>());
        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(2);
        when(utilisateurRepository.findByidUti(2)).thenReturn(recepteur);

        String result = controller.envoyerDemandeAmi(2, session, "/test");

        assertEquals("redirect:/user/rechercher", result);
        verify(demandeAmiRepository).save(any(DemandeAmi.class));
    }

    private void inject(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
