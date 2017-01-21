package org.jensens.service.account;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerAuthenticateTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void noParamAuthenticateShouldReturnFalse() throws Exception {

        this.mockMvc.perform(get("/v1/accounts/authenticate")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void GoodPasswordAuthenticate() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/v1/accounts/authenticate")
                .param("id", "1337")
                .param("password", "password"))
                    .andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals("true", result.getResponse().getContentAsString());
    }

    @Test
    public void BadPasswordAuthenticate() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/v1/accounts/authenticate")
                .param("id", "1337")
                .param("password", "password"))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals("true", result.getResponse().getContentAsString());
    }
}
