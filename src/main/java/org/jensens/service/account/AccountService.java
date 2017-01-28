package org.jensens.service.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jensens.service.account.storage.Accessor;
import org.jensens.service.account.storage.AccessorFactory;
import org.jensens.service.account.storage.Account;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;

@Service
public class AccountService {
    private ObjectMapper jsonMapper = new ObjectMapper();
    private Accessor accountAccessor;

    private static final String GLOBAL_SALT = "Salty";

    public AccountService() {
        accountAccessor = AccessorFactory.getAccountAccessor();
    }

    public boolean authenticatePassword(long accountId, String password) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        Account account = accountAccessor.getAccount(accountId);
        return account.passwordHash.equals(hashPassword(password, GLOBAL_SALT));
    }

    public Account getAccount(long accountId) throws SQLException {
        return accountAccessor.getAccount(accountId);
    }

    public String getAccountJson(long accountId) throws JsonProcessingException, SQLException {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getAccount(accountId));
    }

    public List<Account> getAccounts(long limit) throws SQLException {
        return accountAccessor.getAccounts(limit);
    }

    public String getAccountsAsJson(long limit) throws SQLException, JsonProcessingException {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getAccounts(limit));
    }

    public Account createAccount(String loginName, String firstName, String lastName, String password) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        Account newAccount = new Account();
        newAccount.loginName = loginName;
        newAccount.firstName = firstName;
        newAccount.lastName = lastName;
        newAccount.passwordHash = hashPassword(password, GLOBAL_SALT);
        newAccount.id = accountAccessor.addAccount(newAccount);

        // TODO Password rules validation

        return newAccount;
    }

    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final int iterations = 20*1000;
        final int desiredKeyLen = 256;

        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt.getBytes(), iterations, desiredKeyLen)
        );

        String pwdhash = Base64.encodeBase64String(key.getEncoded());
        return pwdhash;
    }
}
