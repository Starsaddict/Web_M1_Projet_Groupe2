package miage.groupe2.reseausocial.Util;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ImageUtilTest {

    private byte[] createTestImage(int width, int height) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        return baos.toByteArray();
    }

    @Test
    void testCropCenterSquareReturnsSquareImage() throws IOException {
        byte[] input = createTestImage(200, 100);
        byte[] output = ImageUtil.cropCenterSquare(input);
        BufferedImage resultImage = ImageIO.read(new java.io.ByteArrayInputStream(output));
        assertNotNull(resultImage);
        assertEquals(resultImage.getWidth(), resultImage.getHeight());
        assertEquals(100, resultImage.getWidth());
    }
}
