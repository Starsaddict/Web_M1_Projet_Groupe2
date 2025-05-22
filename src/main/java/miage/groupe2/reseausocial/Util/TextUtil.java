package miage.groupe2.reseausocial.Util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class TextUtil {
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");

    public static String stripAccents(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return DIACRITICS.matcher(normalized).replaceAll("");
    }

    public static boolean containsIgnoreAccent(String haystack, String needle) {
        String h = stripAccents(haystack).toLowerCase();
        String n = stripAccents(needle).toLowerCase();
        return h.contains(n);
    }
}
