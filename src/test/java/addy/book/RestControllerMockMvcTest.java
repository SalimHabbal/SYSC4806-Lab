package addy.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestControllerMockMvcTest {

    @Autowired
    MockMvc mvc;

    @Test
    void addBuddy_missingName_returnsBadRequest() throws Exception {
        mvc.perform(post("/api/addressbooks")).andExpect(status().isCreated());

        mvc.perform(post("/api/addressbooks/1/buddies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"phone":"555-0000"}"))
           .andExpect(status().isBadRequest());
    }
}
