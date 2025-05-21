package miage.groupe2.reseausocial.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedirectUtilTest {

    @Test
    void testGetSafeRedirectUrl_withValidReferer() {
        String referer = "http://localhost:8080/somepage";
        String fallback = "/default";
        String result = RedirectUtil.getSafeRedirectUrl(referer, fallback);
        assertEquals("redirect:http://localhost:8080/somepage", result);
    }

    @Test
    void testGetSafeRedirectUrl_withInvalidReferer() {
        String referer = "http://malicious.com";
        String fallback = "/default";
        String result = RedirectUtil.getSafeRedirectUrl(referer, fallback);
        assertEquals("redirect:/default", result);
    }

    @Test
    void testGetSafeRedirectUrl_withNullReferer() {
        String referer = null;
        String fallback = "/default";
        String result = RedirectUtil.getSafeRedirectUrl(referer, fallback);
        assertEquals("redirect:/default", result);
    }
}
