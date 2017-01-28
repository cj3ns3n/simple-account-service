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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerManagementTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String CREATE_SUCCESS_MESSAGE = "{\"status\":\"success\", \"accountId:\"%d";

    @Test
    public void CreateAndGetAccount() throws Exception {
        Account testAccount = TestUtils.createAndPostAccount("MyVoiceIsMyPassword", this.mockMvc);
        Account respAccount = TestUtils.getAccount(testAccount.id, this.mockMvc);

        Assert.assertTrue(testAccount.equals(respAccount));
    }
}
