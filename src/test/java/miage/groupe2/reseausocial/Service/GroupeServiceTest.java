package miage.groupe2.reseausocial.Service;

import miage.groupe2.reseausocial.Model.Groupe;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.GroupeRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.GroupeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupeServiceTest {

    @InjectMocks
    private GroupeService groupeService;

    @Mock
    private GroupeRepository groupeRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSupprimerGroupe_WhenUserIsCreateur_ShouldDeleteAndReturnTrue() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        Groupe groupe = new Groupe();
        groupe.setCreateur(user);
        groupe.setIdGrp(1);
        groupe.setMembres(new ArrayList<>());

        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);

        boolean result = groupeService.supprimerGroupe(user, 1);

        assertTrue(result);
        verify(groupeRepository).delete(groupe);
    }

    @Test
    void testSupprimerGroupe_WhenUserIsNotCreateur_ShouldReturnFalse() {
        Utilisateur user = new Utilisateur();
        user.setIdUti(1);

        Utilisateur autre = new Utilisateur();
        autre.setIdUti(2);

        Groupe groupe = new Groupe();
        groupe.setCreateur(autre);
        groupe.setIdGrp(1);
        groupe.setMembres(new ArrayList<>());

        when(groupeRepository.findGroupeByidGrp(1)).thenReturn(groupe);

        boolean result = groupeService.supprimerGroupe(user, 1);

        assertFalse(result);
        verify(groupeRepository, never()).delete(any());
    }
}
