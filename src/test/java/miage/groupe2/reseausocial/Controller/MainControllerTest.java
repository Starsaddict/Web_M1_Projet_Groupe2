package miage.groupe2.reseausocial.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MainController.class)
 class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHomepageRedirect() throws Exception {
        mockMvc.perform(get("/home"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/home.html"));
    }

    @Test
    void testRootRedirect() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/home.html"));
    }
}
