package org.jensens.auth;

import org.jensens.auth.storage.AccessorInMemory;
import org.jensens.auth.storage.Account;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class AuthenticateController {
    private AccessorInMemory accessor = new AccessorInMemory();

    @RequestMapping("v1/authenticate")
    public boolean authenticate(@RequestParam(value="id") long accountId) {
        try {
            Account account = accessor.getAccount(accountId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "v1/create", method = RequestMethod.POST)
    public long create(@RequestParam(value="loginName") String loginName,
                          @RequestParam(value="firstName") String firstName,
                          @RequestParam(value="lastName") String lastName,
                          @RequestParam(value="password") String password) {
        Account newAccount = new Account();
        newAccount.loginName = loginName;
        newAccount.firstName = firstName;
        newAccount.lastName = lastName;
        newAccount.passwordHash = hashPassword(password);

        try {
            accessor.addAccount(newAccount);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String hashPassword(String password) {
        return password;
    }
}
