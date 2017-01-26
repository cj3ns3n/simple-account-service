package org.jensens.service.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jensens.service.account.storage.AccessorInMemory;
import org.jensens.service.account.storage.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

@RestController
public class AccountController {
    private AccessorInMemory accessor = new AccessorInMemory();
    private ObjectMapper jsonMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String AUTH_SUCCESS_MESSAGE = "{\"status\":\"success\", \"roles\":\"\"}";
    private static final String AUTH_FAIL_MESSAGE = "{\"status\":\"fail\"}";

    private static final String CREATE_SUCCESS_MESSAGE = "{\"status\":\"success\", \"accountId\":%d}";
    private static final String CREATE_FAIL_MESSAGE = "{\"status\":\"fail\"}";

    private static final String TEMP_SALT = "Salty";

    @RequestMapping(value = "v1/accounts/authenticate", method = RequestMethod.POST)
    public ResponseEntity<String> authenticate(@RequestParam(value="id") long accountId, @RequestParam(value="password") String password, HttpServletRequest request) {
        try {
            Account account = accessor.getAccount(accountId);

            try {
                if (account.passwordHash.equals(hashPassword(password, TEMP_SALT))) {
                    return ResponseEntity.ok(AUTH_SUCCESS_MESSAGE);
                }
                else {
                    return ResponseEntity.ok(AUTH_FAIL_MESSAGE);
                }
            } catch (Exception ex) {
                log.error("Request: " + request.getRequestURL() + " raised " + ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @RequestMapping(value = "v1/accounts/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getAccount(@PathVariable(value="id") long accountId) {
        try {
            String accountJson = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(accessor.getAccount(accountId));
            return ResponseEntity.ok(accountJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @RequestMapping(value = "v1/accounts/list", method = RequestMethod.GET)
    public ResponseEntity<String> create(@RequestParam(value="limit", required=false) String limitStr) {
        long limit = Long.MAX_VALUE;

        if (limitStr != null) {
            try {
                limit = Long.parseLong(limitStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        
        try {
            String accountJson = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(accessor.getAccounts(limit));
            return ResponseEntity.ok(accountJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @RequestMapping(value = "v1/accounts/create", method = RequestMethod.POST)
    public ResponseEntity<String> create(@RequestParam(value="loginName") String loginName,
                          @RequestParam(value="firstName") String firstName,
                          @RequestParam(value="lastName") String lastName,
                          @RequestParam(value="password") String password) {
        Account newAccount = new Account();
        newAccount.loginName = loginName;
        newAccount.firstName = firstName;
        newAccount.lastName = lastName;

        // TODO Password rules validation

        try {
            newAccount.passwordHash = hashPassword(password, TEMP_SALT);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        try {
            long accountId = accessor.addAccount(newAccount);
            return ResponseEntity.ok(String.format(CREATE_SUCCESS_MESSAGE, accountId));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok(CREATE_FAIL_MESSAGE);
        }
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
