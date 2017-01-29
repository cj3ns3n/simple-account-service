package org.jensens.service.account;

import org.jensens.service.account.storage.Account;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerAuthenticateTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String PASSWORD = "password";

    @Test
    public void noParamAuthenticateShouldReturnFalse() throws Exception {
        this.mockMvc.perform(get("/v1/accounts/authenticate")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void GoodPasswordAuthenticate() throws Exception {
        Account account = TestUtils.createAndPostAccount(PASSWORD, this.mockMvc);
        TestUtils.authenticateValid(account.id, PASSWORD, this.mockMvc);
    }

    @Test
    public void BadPasswordAuthenticate() throws Exception {
        Account account = TestUtils.createAndPostAccount(PASSWORD, this.mockMvc);
        TestUtils.authenticateInvalid(account.id, "WrongPassword", this.mockMvc);
    }

    @Test
    public void EmptyPasswordAuthenticate() throws Exception {
        Account account = TestUtils.createAndPostAccount(PASSWORD, this.mockMvc);
        TestUtils.authenticateInvalid(account.id, "", this.mockMvc);
    }
}
