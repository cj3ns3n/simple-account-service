package org.jensens.service.account;

import org.apache.tomcat.util.codec.binary.Base64;
import org.jensens.service.account.storage.AccessorInMemory;
import org.jensens.service.account.storage.Account;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountController {
    private AccessorInMemory accessor = new AccessorInMemory();

    @RequestMapping(value = "v1/accounts/authenticate", method = RequestMethod.POST)
    public boolean authenticate(@RequestParam(value="id") long accountId, @RequestParam(value="password") String password) {
        try {
            Account account = accessor.getAccount(accountId);

            try {
                if (account.passwordHash.equals(hashPassword(password))) {
                    return true;
                }
                else {
                    return false;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return false;
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "v1/accounts/{id}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable(value="id") long accountId) {
        try {
            return accessor.getAccount(accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "v1/accounts/list", method = RequestMethod.GET)
    public List<Account> create(@RequestParam(value="limit", required=false) String limitStr) {
        long limit = Long.MAX_VALUE;

        if (limitStr != null) {
            try {
                limit = Long.parseLong(limitStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        try {
            return accessor.getAccounts(limit);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<Account>();
        }
    }


    @RequestMapping(value = "v1/accounts/create", method = RequestMethod.POST)
    public long create(@RequestParam(value="loginName") String loginName,
                          @RequestParam(value="firstName") String firstName,
                          @RequestParam(value="lastName") String lastName,
                          @RequestParam(value="password") String password) {
        Account newAccount = new Account();
        newAccount.loginName = loginName;
        newAccount.firstName = firstName;
        newAccount.lastName = lastName;
        try {
            newAccount.passwordHash = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return -1;
        }

        try {
            accessor.addAccount(newAccount);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final int iterations = 20*1000;
        final int desiredKeyLen = 256;
        final byte[] salt = "Basic Salt".getBytes();

        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, iterations, desiredKeyLen)
        );
        return Base64.encodeBase64String(key.getEncoded());
    }
}
