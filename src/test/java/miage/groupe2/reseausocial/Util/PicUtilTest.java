package miage.groupe2.reseausocial.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PicUtilTest {

    private final PicUtil picUtil = new PicUtil();

    @Test
    void testEncodeBase64WithNull() {
        assertEquals("", picUtil.encodeBase64(null));
    }

    @Test
    void testEncodeBase64WithEmptyArray() {
        assertEquals("", picUtil.encodeBase64(new byte[0]));
    }

    @Test
    void testEncodeBase64WithData() {
        byte[] data = {1, 2, 3, 4, 5};
        String encoded = picUtil.encodeBase64(data);
        assertEquals("AQIDBAU=", encoded);
    }
}
