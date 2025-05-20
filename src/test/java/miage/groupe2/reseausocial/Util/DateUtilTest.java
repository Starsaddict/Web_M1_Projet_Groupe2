package miage.groupe2.reseausocial.Util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        dateUtil = new DateUtil();
    }

    @Test
    void testFormatTimestamp() {
        long timestamp = 1716105600000L; // 2024-05-19 00:00:00 (exemple fixe)
        String expected = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        assertEquals(expected, dateUtil.formatTimestamp(timestamp));
    }

    @Test
    void testFormatTimestampIso() {
        Long timestamp = 1716105600000L; // 2024-05-19 00:00
        String expected = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        assertEquals(expected, dateUtil.formatTimestampIso(timestamp));
    }

    @Test
    void testFormatTimestampIsoNull() {
        assertEquals("", dateUtil.formatTimestampIso(null));
    }

    @Test
    void testFormatString_A_LInstant() {
        long now = System.currentTimeMillis();
        long justNow = now - 10 * 1000; // 10 secondes avant
        assertEquals("à l’instant", DateUtil.formatString(justNow));
    }

    @Test
    void testFormatString_Minutes() {
        long now = System.currentTimeMillis();
        long tenMinutesAgo = now - 10 * 60 * 1000;
        String result = DateUtil.formatString(tenMinutesAgo);
        assertTrue(result.contains("10 minutes"));
    }

    @Test
    void testFormatString_Heures() {
        long now = System.currentTimeMillis();
        long twoHoursAgo = now - 2 * 60 * 60 * 1000;
        String result = DateUtil.formatString(twoHoursAgo);
        assertTrue(result.contains("2 heures"));
    }

    @Test
    void testFormatString_Jours() {
        long now = System.currentTimeMillis();
        long threeDaysAgo = now - 3 * 24 * 60 * 60 * 1000;
        String result = DateUtil.formatString(threeDaysAgo);
        assertTrue(result.contains("3 jours"));
    }

    @Test
    void testFormatString_AncienneDate() {
        long oldTimestamp = 1609459200000L; // 2021-01-01 00:00:00
        String result = DateUtil.formatString(oldTimestamp);
        assertEquals("2021-01-01", result);
    }
}
