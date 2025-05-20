package miage.groupe2.reseausocial.App;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import miage.groupe2.reseausocial.ReseauSocialApplication;

@SpringBootTest
class ReseauSocialApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void testMain() {
        String[] args = {};
        ReseauSocialApplication.main(args);
    }

}

