package miage.groupe2.reseausocial.Controller;

import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.UtilisateurService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class DemandeAmiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DemandeAmiRepository demandeAmiRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private DemandeAmiController demandeAmiController;

    private Utilisateur user;
    private Utilisateur autre;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(demandeAmiController)
                .setViewResolvers(viewResolver)
                .build();

        user = new Utilisateur();
        user.setIdUti(1);
        user.setNomU("Moi");

        autre = new Utilisateur();
        autre.setIdUti(2);
        autre.setNomU("Toi");
    }

    @Test
    void testAccepterDemande() throws Exception {
        DemandeAmi demande = new DemandeAmi();
        demande.setIdDA(5);
        demande.setRecepteur(user);
        demande.setDemandeur(autre);
        demande.setStatut("en attente");

        when(utilisateurService.getUtilisateurFromSession(any())).thenReturn(user);
        when(demandeAmiRepository.findByIdDA(5)).thenReturn(demande);

        mockMvc.perform(post("/demande/accepter")
                        .param("idDemande", "5")
                        .header("Referer", "/profil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(demandeAmiRepository).save(demande);
        verify(demandeAmiRepository).ajouterLienAmitie(2, 1);
    }

    @Test
    void testRefuserDemande() throws Exception {
        DemandeAmi demande = new DemandeAmi();
        demande.setIdDA(10);
        demande.setRecepteur(user);
        demande.setStatut("en attente");

        when(utilisateurService.getUtilisateurFromSession(any())).thenReturn(user);
        when(demandeAmiRepository.findById(10)).thenReturn(Optional.of(demande));

        mockMvc.perform(post("/demande/refuser")
                        .param("idDemande", "10")
                        .header("Referer", "/accueil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(demandeAmiRepository).save(demande);
    }

    @Test
    void testEnvoyerDemandeAmi_Succes() throws Exception {
        when(utilisateurService.getUtilisateurFromSession(any())).thenReturn(user);
        when(utilisateurRepository.findByidUti(2)).thenReturn(autre);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(eq(1), eq(2), any()))
                .thenReturn(false);
        when(demandeAmiRepository.sontDejaAmis(1, 2)).thenReturn(false);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "2")
                        .param("nom", "toto")
                        .header("Referer", "/user/rechercher?nom=toto"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(demandeAmiRepository).save(any(DemandeAmi.class));
    }

    @Test
    void testEnvoyerDemandeAmi_SeAjouterSoiMeme() throws Exception {
        when(utilisateurService.getUtilisateurFromSession(any())).thenReturn(user);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "1")
                        .param("nom", "Moi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/rechercher?nom=Moi"));
    }

    @Test
    void testEnvoyerDemandeAmi_UtilisateurInexistant() throws Exception {
        when(utilisateurService.getUtilisateurFromSession(any())).thenReturn(user);
        when(utilisateurRepository.findByidUti(3)).thenReturn(null);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "3")
                        .param("nom", "Inconnu"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/rechercher?nom=Inconnu"));
    }

    @Test
    void testEnvoyerDemandeAmi_DejaEnvoyeeOuAmi() throws Exception {
        when(utilisateurService.getUtilisateurFromSession(any())).thenReturn(user);
        when(utilisateurRepository.findByidUti(2)).thenReturn(autre);
        when(demandeAmiRepository.existsByDemandeurIdUtiAndRecepteurIdUtiAndStatutIn(eq(1), eq(2), any()))
                .thenReturn(true);

        mockMvc.perform(post("/demande/ajouterAmi")
                        .param("idAmi", "2")
                        .param("nom", "Toi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/rechercher?nom=Toi"));
    }
}
