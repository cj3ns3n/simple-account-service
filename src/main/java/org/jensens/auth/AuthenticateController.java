package org.jensens.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class AuthenticateController {

    @RequestMapping("v1/authenticate")
    public boolean authenticate(@RequestParam(value="id") String accountId) {
        return false;
    }
}
