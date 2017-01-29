package org.jensens.service.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jensens.service.account.storage.Accessor;
import org.jensens.service.account.storage.AccessorFactory;
import org.jensens.service.account.storage.Account;
import org.jensens.service.account.storage.DataAccessException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.List;

@Service
public class AccountService {
    private ObjectMapper jsonMapper = new ObjectMapper();
    private Accessor accountAccessor;

    private static final String GLOBAL_SALT = "Salty";

    public AccountService() {
        accountAccessor = AccessorFactory.getAccountAccessor();
    }

    public boolean authenticatePassword(long accountId, String password) throws DataAccessException, PasswordException {
        Account account = accountAccessor.getAccount(accountId);
        return account.passwordHash.equals(hashPassword(password, GLOBAL_SALT));
    }

    public Account getAccount(long accountId) throws DataAccessException {
        return accountAccessor.getAccount(accountId);
    }

    public String getAccountJson(long accountId) throws JsonProcessingException, DataAccessException {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getAccount(accountId));
    }

    public List<Account> getAccounts(long limit) throws DataAccessException {
        return accountAccessor.getAccounts(limit);
    }

    public String getAccountsAsJson(long limit) throws DataAccessException, JsonProcessingException {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getAccounts(limit));
    }

    public Account createAccount(String loginName, String firstName, String lastName, String password) throws DataAccessException, PasswordException {
        Account newAccount = new Account();
        newAccount.loginName = loginName;
        newAccount.firstName = firstName;
        newAccount.lastName = lastName;
        newAccount.passwordHash = hashPassword(password, GLOBAL_SALT);
        newAccount.id = accountAccessor.addAccount(newAccount);

        // TODO parameter rules validation

        return newAccount;
    }

    private String hashPassword(String password, String salt) throws PasswordException {
        final int iterations = 133700;
        final int desiredKeyLen = 256;

        SecretKey key = null;
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            key = f.generateSecret(new PBEKeySpec(
                    password.toCharArray(), salt.getBytes(), iterations, desiredKeyLen)
            );
        } catch (Exception ex) {
            throw new PasswordException(ex);
        }

        return Base64.encodeBase64String(key.getEncoded());
    }
}
