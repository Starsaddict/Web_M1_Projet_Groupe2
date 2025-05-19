package miage.groupe2.reseausocial.Util;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("picUtil")
public class PicUtil {
    public String encodeBase64(byte[] data) {
        if (data == null || data.length == 0) return "";
        return Base64.getEncoder().encodeToString(data);
    }
}
