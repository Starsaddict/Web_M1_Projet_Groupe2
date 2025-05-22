package miage.groupe2.reseausocial.Service;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.service.UtilisateurService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository repo;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUtilisateurFromSession_shouldReturnUtilisateur_whenSessionContainsUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUti(123);

        when(session.getAttribute("user")).thenReturn(utilisateur);
        when(repo.findByidUti(123)).thenReturn(utilisateur);

        Utilisateur result = utilisateurService.getUtilisateurFromSession(session);
        assertNotNull(result);
        assertEquals(123, result.getIdUti());
        verify(repo).findByidUti(123);
    }

    @Test
    void getUtilisateurFromSession_shouldReturnNull_whenSessionUserIsNull() {
        when(session.getAttribute("user")).thenReturn(null);
        Utilisateur result = utilisateurService.getUtilisateurFromSession(session);
        assertNull(result);
        verify(repo, never()).findByidUti(anyInt());
    }

    @Test
    void getUtilisateurFromSession_shouldReturnNull_whenSessionUserIsNotUtilisateur() {
        when(session.getAttribute("user")).thenReturn("not a user");
        Utilisateur result = utilisateurService.getUtilisateurFromSession(session);
        assertNull(result);
        verify(repo, never()).findByidUti(anyInt());
    }
}
