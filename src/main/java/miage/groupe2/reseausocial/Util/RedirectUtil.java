package miage.groupe2.reseausocial.Util;

public class RedirectUtil {
    private static final String BASE_URL = "http://localhost:8080";

    public static String getSafeRedirectUrl(String referer, String fallback) {
        if (referer != null && referer.startsWith(BASE_URL)) {
            return "redirect:" + referer;
        }
        return "redirect:" + fallback;
    }
}
