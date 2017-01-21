package org.jensens.auth;

import org.jensens.auth.storage.AccessorInMemory;
import org.jensens.auth.storage.Account;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
