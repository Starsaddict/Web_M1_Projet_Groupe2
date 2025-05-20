package miage.groupe2.reseausocial.Service;

import miage.groupe2.reseausocial.Model.DemandeAmi;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.DemandeAmiRepository;
import miage.groupe2.reseausocial.service.DemandeAmiService;
import miage.groupe2.reseausocial.service.UtilisateurService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DemandeAmiServiceTest {

    @Mock
    private DemandeAmiRepository demandeAmiRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private DemandeAmiService demandeAmiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDemandeMessages_ShouldReturnOnlyPendingRequests() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        DemandeAmi demande1 = new DemandeAmi();
        demande1.setStatut("en attente");
        demande1.setRecepteur(user);

        DemandeAmi demande2 = new DemandeAmi();
        demande2.setStatut("acceptée");
        demande2.setRecepteur(user);

        when(demandeAmiRepository.findByRecepteur(user)).thenReturn(Arrays.asList(demande1, demande2));

        List<DemandeAmi> result = demandeAmiService.getDemandeMessages(user);

        assertEquals(1, result.size());
        assertEquals("en attente", result.get(0).getStatut());
        verify(demandeAmiRepository, times(1)).findByRecepteur(user);
    }
}
