package miage.groupe2.reseausocial.Util;

import miage.groupe2.reseausocial.Model.Evenement;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


@Component("dateUtil")
public class DateUtil {

    private static final Locale FR = Locale.FRENCH;
    private static final DateTimeFormatter RANGE_FMT =
            DateTimeFormatter.ofPattern("d MMM 'AT' HH:mm", FR);


    /**
     * Formate un intervalle de dates en chaîne unique.
     *
     * <p>Par exemple, si startMillis correspond au 7 octobre à 07:00
     * et endMillis au 11 octobre à 12:00, renvoie :
     * <pre>
     * 7 oct. À 07:00 – 11 oct. À 12:00
     * </pre>
     *
     * @param startMillis le timestamp de début (en millisecondes)
     * @param endMillis   le timestamp de fin   (en millisecondes)
     * @return une chaîne du type "7 oct. À 07:00 – 11 oct. À 12:00"
     */
    public static String formatRange(long startMillis, long endMillis) {
        return Instant.ofEpochMilli(startMillis)
                .atZone(ZoneId.systemDefault())
                .format(RANGE_FMT)
                + " – " +
                Instant.ofEpochMilli(endMillis)
                        .atZone(ZoneId.systemDefault())
                        .format(RANGE_FMT);
    }

    public static String formatRange(Evenement e) {
        long d = e.getDateDebutE();
        long f = e.getDateFinE();
        return formatRange(d, f);
    }

    /**
     * Calcule le compte-à-rebours entre « maintenant » et un timestamp futur.
     *
     * <p>Si le timestamp cible est déjà passé, renvoie [0,0,0,0].
     * Sinon renvoie successivement :
     * <ul>
     *   <li>nombre de jours restants</li>
     *   <li>nombre d’heures restantes (hors jours)</li>
     *   <li>nombre de minutes restantes (hors heures)</li>
     *   <li>nombre de secondes restantes (hors minutes)</li>
     * </ul>
     *
     * @param targetMillis le timestamp futur (en millisecondes)
     * @return une liste de 4 éléments : [jours, heures, minutes, secondes]
     */
    public static List<Long> countdown(long targetMillis) {
        long now  = System.currentTimeMillis();
        long diff = Math.max(0, targetMillis - now);

        long totalSec = diff / 1_000;
        long jours     = totalSec / (24 * 3600);
        totalSec %= (24 * 3600);
        long heures    = totalSec / 3600;
        totalSec %= 3600;
        long minutes   = totalSec / 60;
        long secondes  = totalSec % 60;

        return List.of(jours, heures, minutes, secondes);
    }

    public static List<Long> countdown (Evenement ev) {
        long i = ev.getDateDebutE();
        return countdown(i);
    }


    public String formatTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String formatString(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = timestamp - now;

        // —— Cas futur ——
        if (diff > 0) {
            long diffSec  = TimeUnit.MILLISECONDS.toSeconds(diff);
            long diffMin  = TimeUnit.MILLISECONDS.toMinutes(diff);
            long diffHour = TimeUnit.MILLISECONDS.toHours(diff);
            long diffDay  = TimeUnit.MILLISECONDS.toDays(diff);

            if (diffSec < 60) {
                return "dans un instant";
            } else if (diffMin < 60) {
                return "dans " + diffMin + (diffMin == 1 ? " minute" : " minutes");
            } else if (diffHour < 24) {
                return "dans " + diffHour + (diffHour == 1 ? " heure" : " heures");
            } else if (diffDay == 1) {
                return "demain";
            } else if (diffDay < 7) {
                return "dans " + diffDay + (diffDay == 1 ? " jour" : " jours");
            } else {
                // au-delà d’une semaine, on affiche la date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return "le " + sdf.format(new Date(timestamp));
            }
        }

        // —— Cas passé (votre code existant) ——
        long pastMillis = now - timestamp;
        if (pastMillis < 60 * 1000) {
            return "à l’instant";
        } else if (pastMillis < 60 * 60 * 1000) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(pastMillis);
            return minutes + (minutes == 1 ? " minute" : " minutes") + " avant";
        } else if (pastMillis < 24 * 60 * 60 * 1000) {
            long hours = TimeUnit.MILLISECONDS.toHours(pastMillis);
            return hours + (hours == 1 ? " heure" : " heures") + " avant";
        } else if (pastMillis < 7 * 24 * 60 * 60 * 1000) {
            long days = TimeUnit.MILLISECONDS.toDays(pastMillis);
            return days + (days == 1 ? " jour" : " jours") + " avant";
        } else {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
    }

    public String formatEvenement(Evenement evenement) {
        long start = evenement.getDateDebutE();
        long end = evenement.getDateFinE();
        if (start > end){
            return "";
        }
        long now = System.currentTimeMillis();
        if(start < now && end > now){
            return "en cours";
        }else if(start > now){
            return formatString(start);
        }else if(now > end){
            return formatString(end);
        }
        return "en cours";
    }


    public static long toEpochMilli(LocalDateTime ldt) {
        return ldt
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}