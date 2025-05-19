package miage.groupe2.reseausocial.Util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Component("dateUtil")
public class DateUtil {
    public String formatTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String formatTimestampIso(Long timestamp) {
        if (timestamp == null)
            return "";
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    public static String formatString(long timestamp) {
        long now = System.currentTimeMillis();
        long diffMillis = now - timestamp;

        if (diffMillis < 60 * 1000) {
            return "à l’instant";
        } else if (diffMillis < 60 * 60 * 1000) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
            return minutes + (minutes == 1 ? " minute" : " minutes") + " avant";
        } else if (diffMillis < 24 * 60 * 60 * 1000) {
            long hours = TimeUnit.MILLISECONDS.toHours(diffMillis);
            return hours + (hours == 1 ? " heure" : " heures") + " avant";
        } else if (diffMillis < 7 * 24 * 60 * 60 * 1000) {
            long days = TimeUnit.MILLISECONDS.toDays(diffMillis);
            return days + (days == 1 ? " jour" : " jours") + " avant";
        } else {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
    }
}