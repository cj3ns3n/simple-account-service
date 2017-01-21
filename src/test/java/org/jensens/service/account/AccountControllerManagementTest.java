package org.jensens.service.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void CreateAccountShouldReturnTrue() throws Exception {

        MvcResult result = this.mockMvc.perform((post("/v1/accounts/create")
                .param("loginName", "jdoe")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("password", "MyVoiceIsMyPassword")))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals("1", result.getResponse().getContentAsString());
    }

    @Test
    public void GetAccountShoultReturnTrue() throws Exception {
        long accountId = 1337L;
        String urlPath = String.format("/v1/accounts/%d", accountId);
        
        MvcResult result = this.mockMvc.perform(get(urlPath)
                .param("id", String.valueOf(accountId)))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        //Assert.assertEquals("true", result.getResponse().getContentAsString());
    }
}
