package org.jensens.service.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jensens.service.account.storage.Account;
import org.junit.Assert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {
    private static final String AUTH_SUCCESS_MESSAGE = "{\"status\":\"success\", \"roles\":\"\"}";

    public static Account createTestAccount() {
        Account testAccount = new Account();
        testAccount.loginName = "jdoe";
        testAccount.firstName = "John";
        testAccount.lastName = "Doe";

        return testAccount;
    }

    public static Account createAndPostAccount(String password, MockMvc mockMvc) throws Exception {
        Account account = createTestAccount();
        account.id = postAccount(account, password, mockMvc);

        return account;
    }

    public static long postAccount(Account account, String password, MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform((post("/v1/accounts/create")
                .param("loginName", account.loginName)
                .param("firstName", account.firstName)
                .param("lastName", account.lastName)
                .param("password", password)))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> createRespMap = mapper.readValue(result.getResponse().getContentAsString(), Map.class);
        return (int)createRespMap.get("accountId");
    }

    public static Account getAccount(long accountId, MockMvc mockMvc) throws Exception {
        String urlPath = String.format("/v1/accounts/%d", accountId);
        MvcResult result = mockMvc.perform(get(urlPath))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result.getResponse().getContentAsString(), Account.class);
    }

    public static boolean authenticate(long accountId, String password, MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/accounts/authenticate")
                .param("id", String.valueOf(accountId))
                .param("password", password))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        return result.getResponse().getContentAsString().equals(AUTH_SUCCESS_MESSAGE);
    }
}
