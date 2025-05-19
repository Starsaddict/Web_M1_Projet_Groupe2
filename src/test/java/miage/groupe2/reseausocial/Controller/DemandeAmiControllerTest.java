package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DemandeAmiControllerTest {

    @InjectMocks
    DemandeAmiController controller;

    @Mock
    DemandeAmiRepository demandeAmiRepository;

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    HttpSession session;

    @Mock
    RedirectAttributes redirectAttributes;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void accepte_demande_nonConnecte_redirigeLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String res = controller.accepterDemande(1, session, redirectAttributes);

        assertEquals("redirect:/auth/login", res);
    }

    @Test
    void accepte_demande_valide_modifieEtSauve() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(2);

        DemandeAmi demande = new DemandeAmi();
        demande.setIdDA(10);
        demande.setRecepteur(user);
        demande.setDemandeur(demandeur);
        demande.setStatut("en attente");

        when(session.getAttribute("user")).thenReturn(user);
        when(demandeAmiRepository.findById(10)).thenReturn(Optional.of(demande));
        doNothing().when(demandeAmiRepository).ajouterLienAmitie(demandeur.getIdUti(), user.getIdUti());
        when(redirectAttributes.addFlashAttribute(anyString(), anyString())).thenReturn(redirectAttributes);

        String res = controller.accepterDemande(10, session, redirectAttributes);

        assertEquals("redirect:/demande/demandes-recues", res);
        assertEquals("acceptée", demande.getStatut());
        verify(demandeAmiRepository).save(demande);
        verify(demandeAmiRepository).ajouterLienAmitie(demandeur.getIdUti(), user.getIdUti());
        verify(redirectAttributes).addFlashAttribute("succes", "Demande d'ami acceptée.");
    }

    @Test
    void accepte_demande_pasPourUser_rienFait() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        Utilisateur autre = new Utilisateur();
        autre.setIdUti(3);

        DemandeAmi demande = new DemandeAmi();
        demande.setRecepteur(autre); 

        when(session.getAttribute("user")).thenReturn(user);
        when(demandeAmiRepository.findById(5)).thenReturn(Optional.of(demande));

        String res = controller.accepterDemande(5, session, redirectAttributes);

        assertEquals("redirect:/demande/demandes-recues", res);
        verify(demandeAmiRepository, never()).save(any());
        verify(demandeAmiRepository, never()).ajouterLienAmitie(anyInt(), anyInt());
    }
}
