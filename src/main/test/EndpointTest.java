import com.n26.Application;
import com.n26.statistics.TransactionStatistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class EndpointTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private TransactionStatistics transactionStatistics;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        transactionStatistics.reset();
    }

    @Test
    public void contextLoads() {
        assertNotNull(wac);
    }

    @Test
    public void shouldReturnTransactionStatisticsReturnEmptyResult() throws Exception {
        this.mockMvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.count", is(0)));
    }

    @Test
    public void shouldReturnTransactionStatisticsReturnNotEmptyResult() throws Exception {

        String jsonTask = Utils.getJsonTransaction(Instant.now().minusSeconds(1));

        this.mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonTask));

        this.mockMvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.count", is(1)));
    }

    @Test
    public void shouldReturnTransactionStatisticsEmptyResultAfterDelete() throws Exception {

        String jsonTask = Utils.getJsonTransaction(Instant.now().minusSeconds(1));

        this.mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonTask));

        this.mockMvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.count", is(1)));

        this.mockMvc.perform(delete("/transactions")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.count", is(0)));

    }

}
