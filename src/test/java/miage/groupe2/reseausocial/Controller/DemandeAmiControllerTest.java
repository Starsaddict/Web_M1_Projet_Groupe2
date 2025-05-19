package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DemandeAmiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DemandeAmiRepository demandeAmiRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DemandeAmiController demandeAmiController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(demandeAmiController).build();
    }

    @Test
    void afficherDemandesRecues_userNotLoggedIn_redirectToLogin() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);

        mockMvc.perform(get("/demande/demandes-recues").sessionAttr("user", null))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void afficherDemandesRecues_userLoggedIn_showList() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        when(session.getAttribute("user")).thenReturn(user);
        when(demandeAmiRepository.findByRecepteurIdUtiAndStatut(1, "en attente"))
                .thenReturn(List.of(new DemandeAmi()));

        mockMvc.perform(get("/demande/demandes-recues").sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("listeDemandeAmi"))
                .andExpect(model().attributeExists("demandesRecues"));
    }

    @Test
    void accepterDemande_userNotLoggedIn_redirectToLogin() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);

        mockMvc.perform(post("/demande/accepter").param("idDemande", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void accepterDemande_validRequest_saveAndRedirect() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setIdUti(2);

        Utilisateur demandeur = new Utilisateur();
        demandeur.setIdUti(1);

        DemandeAmi demande = new DemandeAmi();
        demande.setRecepteur(user);
        demande.setDemandeur(demandeur);
        demande.setStatut("en attente");

        when(session.getAttribute("user")).thenReturn(user);
        when(demandeAmiRepository.findById(10)).thenReturn(Optional.of(demande));

        mockMvc.perform(post("/demande/accepter")
                        .param("idDemande", "10")
                        .header("Referer", "/somepage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/somepage"));

        verify(demandeAmiRepository).save(demande);
        verify(demandeAmiRepository).ajouterLienAmitie(1, 2);
    }

    @Test
    void refuserDemande_userNotLoggedIn_redirectToLogin() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);

        mockMvc.perform(post("/demande/refuser").param("idDemande", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void refuserDemande_validRequest_saveAndRedirect() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setIdUti(2);

        DemandeAmi demande = new DemandeAmi();
        demande.setRecepteur(user);
        demande.setStatut("en attente");

        when(session.getAttribute("user")).thenReturn(user);
        when(demandeAmiRepository.findById(10)).thenReturn(Optional.of(demande));

        mockMvc.perform(post("/demande/refuser")
                        .param("idDemande", "10")
                        .header("Referer", "/somepage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/somepage"));

        verify(demandeAmiRepository).save(demande);
    }

    @Test
    void envoyerDemandeAmi_userNotLoggedIn_redirectToLogin() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);

        mockMvc.perform(post("/demande/ajouterAmi").param("idAmi", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void envoyerDemandeAmi_selfAdd_redirectError() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        when(session.getAttribute("user")).thenReturn(user);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "1")
                        .param("nom", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/user/rechercher?nom="));
    }

    @Test
    void envoyerDemandeAmi_userNotFound_redirectError() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(2)).thenReturn(null);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "2")
                        .param("nom", "toto"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/user/rechercher?nom=toto"));
    }

    @Test
    void envoyerDemandeAmi_alreadyExists_redirectError() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(2);

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(2)).thenReturn(recepteur);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(1, 2, List.of("en attente"))).thenReturn(true);
        when(demandeAmiRepository.sontDejaAmis(1, 2)).thenReturn(false);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "2")
                        .param("nom", "foo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/user/rechercher?nom=foo"));
    }

    @Test
    void envoyerDemandeAmi_alreadyFriends_redirectError() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(2);

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(2)).thenReturn(recepteur);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(1, 2, List.of("en attente"))).thenReturn(false);
        when(demandeAmiRepository.sontDejaAmis(1, 2)).thenReturn(true);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "2")
                        .param("nom", "bar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/user/rechercher?nom=bar"));
    }

    @Test
    void envoyerDemandeAmi_validRequest_saveAndRedirect() throws Exception {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);
        user.setNomU("Jean");

        Utilisateur recepteur = new Utilisateur();
        recepteur.setIdUti(2);
        recepteur.setNomU("Paul");

        when(session.getAttribute("user")).thenReturn(user);
        when(utilisateurRepository.findByidUti(2)).thenReturn(recepteur);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(1, 2, List.of("en attente"))).thenReturn(false);
        when(demandeAmiRepository.sontDejaAmis(1, 2)).thenReturn(false);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "2")
                        .param("nom", "toto"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"))
                .andExpect(redirectedUrl("/user/rechercher?nom=toto"));

        verify(demandeAmiRepository).save(any(DemandeAmi.class));
    }
}
