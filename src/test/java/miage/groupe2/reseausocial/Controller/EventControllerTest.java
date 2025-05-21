package miage.groupe2.reseausocial.Controller;

import jakarta.servlet.http.HttpSession;
import miage.groupe2.reseausocial.Model.Evenement;
import miage.groupe2.reseausocial.Model.Utilisateur;
import miage.groupe2.reseausocial.Repository.EvenementRepository;
import miage.groupe2.reseausocial.Repository.UtilisateurRepository;
import miage.groupe2.reseausocial.Util.DateUtil;
import miage.groupe2.reseausocial.Util.RedirectUtil;
import miage.groupe2.reseausocial.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class EventControllerTest {

    @Mock
    private EvenementRepository evenementRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreerEvenement_shouldSaveAndRedirect() {
        Utilisateur user = mock(Utilisateur.class);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Evenement evenement = new Evenement();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime fin = start.plusHours(2);
        String referer = "/previousPage";

        String result = eventController.creerEvenement(evenement, session, start, fin, referer);

        assertEquals(RedirectUtil.getSafeRedirectUrl(referer, EventController.EVENEMENT_TOUS), result);
        assertEquals(user, evenement.getCreateur());
        assertEquals(DateUtil.toEpochMilli(start), evenement.getDateDebutE());
        assertEquals(DateUtil.toEpochMilli(fin), evenement.getDateFinE());
        verify(evenementRepository).save(evenement);
    }

    @Test
    public void testSupprimerEvenement_shouldDeleteIfCreator() {
        Utilisateur user = mock(Utilisateur.class);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Evenement evenement = mock(Evenement.class);
        when(evenementRepository.findById(1)).thenReturn(Optional.of(evenement));
        when(evenement.getCreateur()).thenReturn(user);
        when(user.getIdUti()).thenReturn(10);
        when(evenement.getCreateur().getIdUti()).thenReturn(10);

        String referer = "/somePage";

        String result = eventController.supprimerEvenement(1, session, referer);

        verify(evenementRepository).delete(evenement);
        assertEquals(RedirectUtil.getSafeRedirectUrl(referer, "EVENEMENT_TOUS"), result);
    }

    @Test
    public void testModifierEvenement_shouldUpdateFieldsAndSave() {
        Evenement evenement = new Evenement();
        evenement.setNomE("Old name");
        evenement.setDescription("Old desc");
        evenement.setAdresseE("Old addr");

        when(evenementRepository.findByIdEve(1)).thenReturn(evenement);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime fin = start.plusHours(1);
        String referer = "/prev";

        String newName = "New name";
        String newDesc = "New desc";
        String newAddr = "New addr";

        String result = eventController.modifierEvenement(1, newName, newDesc, newAddr, start, fin, referer);

        assertEquals(newName, evenement.getNomE());
        assertEquals(newDesc, evenement.getDescription());
        assertEquals(newAddr, evenement.getAdresseE());
        assertEquals(DateUtil.toEpochMilli(start), evenement.getDateDebutE());
        assertEquals(DateUtil.toEpochMilli(fin), evenement.getDateFinE());
        verify(evenementRepository).save(evenement);
        assertEquals(RedirectUtil.getSafeRedirectUrl(referer, EventController.EVENEMENT_TOUS), result);
    }

    @Test
    public void testAfficherTousLesEvenements_shouldAddAttributesAndReturnView() {
        Utilisateur user = mock(Utilisateur.class);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        long now = System.currentTimeMillis();

        Evenement futureEvent1 = mock(Evenement.class);
        when(futureEvent1.getDateFinE()).thenReturn(now + 10000L);
        when(futureEvent1.getDateDebutE()).thenReturn(now + 5000L);

        Evenement futureEvent2 = mock(Evenement.class);
        when(futureEvent2.getDateFinE()).thenReturn(now + 20000L);
        when(futureEvent2.getDateDebutE()).thenReturn(now + 15000L);

        List<Evenement> assists = new ArrayList<>(List.of(futureEvent1));
        List<Evenement> created = new ArrayList<>(List.of(futureEvent2));

        when(user.getEvenementsAssistes()).thenReturn(assists);
        when(user.getEvenements()).thenReturn(created);

        Evenement otherEvent = mock(Evenement.class);
        when(otherEvent.getDateFinE()).thenReturn(now + 30000L);
        when(otherEvent.getParticipants()).thenReturn(Collections.emptyList());

        when(evenementRepository.findAll()).thenReturn(Collections.singletonList(otherEvent));

        String view = eventController.afficherTousLesEvenements(model, session);

        verify(model).addAttribute(eq("monEvenements"), eq(assists));
        verify(model).addAttribute(eq("evenementCree"), eq(created));
        verify(model).addAttribute(eq("upcoming"), anyList());
        verify(model).addAttribute("user", user);

        assertEquals("events", view);
    }

    @Test
    public void testRejoindreEvenement_shouldAddParticipantAndRedirect() {
        Utilisateur user = mock(Utilisateur.class);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Evenement event = mock(Evenement.class);
        when(evenementRepository.findByIdEve(1)).thenReturn(event);

        when(user.getEvenementsAssistes()).thenReturn(new ArrayList<>());
        when(event.getParticipants()).thenReturn(new ArrayList<>());

        String referer = "/somePage";

        String result = eventController.rejoindreEvenement(1, session, referer);

        verify(utilisateurRepository).save(user);
        verify(session).setAttribute(eq("user"), eq(user));
        assertEquals(RedirectUtil.getSafeRedirectUrl(referer, EventController.EVENEMENT_TOUS), result);
    }

    @Test
    public void testQuitterEvenement_shouldRemoveParticipantAndRedirect() {
        Utilisateur user = mock(Utilisateur.class);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Evenement event = mock(Evenement.class);
        when(evenementRepository.findByIdEve(1)).thenReturn(event);

        List<Evenement> assists = new ArrayList<>();
        assists.add(event);
        when(user.getEvenementsAssistes()).thenReturn(assists);

        List<Utilisateur> participants = new ArrayList<>();
        participants.add(user);
        when(event.getParticipants()).thenReturn(participants);

        String referer = "/prevPage";

        String result = eventController.quitterEvenement(1, session, referer);

        verify(utilisateurRepository).save(user);
        verify(session).setAttribute(eq("user"), eq(user));
        assertEquals(RedirectUtil.getSafeRedirectUrl(referer, EventController.EVENEMENT_TOUS), result);
    }

    @Test
    public void testRedirectToEvenement_shouldAddAttributesAndReturnEventView() {
        Utilisateur user = mock(Utilisateur.class);
        when(utilisateurService.getUtilisateurFromSession(session)).thenReturn(user);

        Evenement event = new Evenement();
        event.setDateDebutE(System.currentTimeMillis());
        List<Utilisateur> participants = new ArrayList<>();
        participants.add(user);
        event.setParticipants(participants);

        when(evenementRepository.findByIdEve(5)).thenReturn(event);

        Model model = mock(Model.class);
        String view = eventController.redirectToEvenement(5, model, session);

        verify(model).addAttribute(eq("estParticipants"), eq(true));
        verify(model).addAttribute(eq("eventDate"), anyString());
        verify(model).addAttribute(eq("event"), eq(event));

        assertEquals("event", view);
    }

}
