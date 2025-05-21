package miage.groupe2.reseausocial.Util;

import miage.groupe2.reseausocial.Model.Evenement;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilTest {

    @Test
    void testFormatRange_withMillis() {
        long start = DateUtil.toEpochMilli(LocalDateTime.of(2025, 5, 21, 7, 0));
        long end = DateUtil.toEpochMilli(LocalDateTime.of(2025, 5, 21, 12, 0));
        String result = DateUtil.formatRange(start, end);
        assertTrue(result.contains("21 mai"));
        assertTrue(result.contains("07:00"));
        assertTrue(result.contains("12:00"));
    }

    @Test
    void testFormatRange_withEvenement() {
        Evenement e = new Evenement();
        e.setDateDebutE(DateUtil.toEpochMilli(LocalDateTime.of(2025, 5, 21, 8, 30)));
        e.setDateFinE(DateUtil.toEpochMilli(LocalDateTime.of(2025, 5, 21, 10, 45)));
        String result = DateUtil.formatRange(e);
        assertTrue(result.contains("21 mai"));
        assertTrue(result.contains("08:30"));
        assertTrue(result.contains("10:45"));
    }

    @Test
    void testCountdown_future() {
        long future = System.currentTimeMillis() + 90061_000; // 1 day, 1 hour, 1 minute, 1 second approx
        List<Long> res = DateUtil.countdown(future);
        assertEquals(4, res.size());
        assertTrue(res.get(0) >= 1);
    }

    @Test
    void testCountdown_past() {
        long past = System.currentTimeMillis() - 10000;
        List<Long> res = DateUtil.countdown(past);
        assertEquals(List.of(0L, 0L, 0L, 0L), res);
    }

    @Test
    void testCountdown_withEvenement() {
        Evenement e = new Evenement();
        e.setDateDebutE(System.currentTimeMillis() + 60_000);
        List<Long> res = DateUtil.countdown(e);
        assertEquals(4, res.size());
    }

    @Test
    void testFormatString_future() {
        long future = System.currentTimeMillis() + 2 * 60_000; // 2 minutes in future
        String s = DateUtil.formatString(future);
        assertTrue(s.contains("dans"));
    }

    @Test
    void testFormatString_past() {
        long past = System.currentTimeMillis() - 2 * 60_000; // 2 minutes ago
        String s = DateUtil.formatString(past);
        assertTrue(s.contains("avant"));
    }

    @Test
    void testFormatEvenement_enCours() {
        long now = System.currentTimeMillis();
        Evenement e = new Evenement();
        e.setDateDebutE(now - 1000);
        e.setDateFinE(now + 1000);
        String s = new DateUtil().formatEvenement(e);
        assertEquals("en cours", s);
    }

    @Test
    void testFormatEvenement_futur() {
        long now = System.currentTimeMillis();
        Evenement e = new Evenement();
        e.setDateDebutE(now + 100000);
        e.setDateFinE(now + 200000);
        String s = new DateUtil().formatEvenement(e);
        assertTrue(s.startsWith("dans") || s.startsWith("le"));
    }

    @Test
    void testFormatEvenement_passe() {
        long now = System.currentTimeMillis();
        Evenement e = new Evenement();
        e.setDateDebutE(now - 200000);
        e.setDateFinE(now - 100000);
        String s = new DateUtil().formatEvenement(e);
        assertTrue(s.contains("avant") || s.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    void testToEpochMilli() {
        LocalDateTime ldt = LocalDateTime.of(2025, 5, 21, 14, 0);
        long millis = DateUtil.toEpochMilli(ldt);
        assertTrue(millis > 0);
    }
}
