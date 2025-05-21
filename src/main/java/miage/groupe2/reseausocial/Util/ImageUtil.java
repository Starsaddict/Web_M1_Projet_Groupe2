package miage.groupe2.reseausocial.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtil {

    public static byte[] cropCenterSquare(byte[] inputImageBytes) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(inputImageBytes));
        if (originalImage == null) {
            throw new IOException("Image format not supported or image corrupted");
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;

        BufferedImage cropped = originalImage.getSubimage(x, y, size, size);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(cropped, "jpg", baos);
        return baos.toByteArray();
    }
}
