package org.jensens.service.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jensens.service.account.storage.Account;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

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
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        String locationUrl = result.getResponse().getHeader("Location");
        String idStr = locationUrl.substring(locationUrl.lastIndexOf("/") + 1);
        return Long.valueOf(idStr);
    }

    public static Account getAccount(long accountId, MockMvc mockMvc) throws Exception {
        String urlPath = String.format("/v1/accounts/%d", accountId);
        MvcResult result = mockMvc.perform(get(urlPath))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result.getResponse().getContentAsString(), Account.class);
    }

    private static ResultActions authenticate(long accountId, String password, MockMvc mockMvc) throws Exception {
        return mockMvc.perform(post("/v1/accounts/authenticate")
                .param("id", String.valueOf(accountId))
                .param("password", password))
                .andDo(print());
    }

    public static void authenticateValid(long accountId, String password, MockMvc mockMvc) throws Exception {
        authenticate(accountId, password, mockMvc).andExpect(status().isOk());
    }

    public static void authenticateInvalid(long accountId, String password, MockMvc mockMvc) throws Exception {
        authenticate(accountId, password, mockMvc).andExpect(status().is4xxClientError());
    }
}
