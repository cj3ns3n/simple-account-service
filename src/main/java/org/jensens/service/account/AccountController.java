package org.jensens.service.account;

import org.jensens.service.account.storage.Account;
import org.jensens.service.account.storage.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
import java.net.URI;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String AUTH_SUCCESS_MESSAGE = "{\"status\":\"success\", \"roles\":\"\"}";
    private static final String AUTH_FAIL_MESSAGE = "{\"status\":\"fail\"}";

    private static final String CREATE_SUCCESS_MESSAGE = "{\"status\":\"success\", \"accountId\":%d}";
    private static final String CREATE_FAIL_MESSAGE = "{\"status\":\"fail\"}";

    private static final String GET_ACCOUNT_URL_FORMAT = "v1/accounts/{id}";

    @RequestMapping(value = "v1/accounts/authenticate", method = RequestMethod.POST)
    public ResponseEntity<String> authenticate(@RequestParam(value="id") long accountId, @RequestParam(value="password") String password, HttpServletRequest request) {
        try {
            if (accountService.authenticatePassword(accountId, password)) {
                return ResponseEntity.ok(AUTH_SUCCESS_MESSAGE);
            }
            else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (DataAccessException dax) {
            log.error("Request: " + request.getRequestURL() + " raised " + dax);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data Access Error");
        } catch (PasswordException px) {
            log.error("Request: " + request.getRequestURL() + " raised " + px);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Password Processing Error");
        } catch (Exception ex) {
            log.error("Request: " + request.getRequestURL() + " raised " + ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @RequestMapping(value = GET_ACCOUNT_URL_FORMAT, method = RequestMethod.GET)
    public ResponseEntity<String> getAccount(@PathVariable(value="id") long accountId, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(accountService.getAccountJson(accountId));
        } catch (NotFoundException nfx) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (DataAccessException dax) {
            log.error("Request: " + request.getRequestURL() + " raised " + dax);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data Access Error");
        } catch (Exception e) {
            log.error("Request: " + request.getRequestURL() + " raised " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @RequestMapping(value = "v1/accounts/list", method = RequestMethod.GET)
    public ResponseEntity<String> listAccounts(@RequestParam(value="limit", required=false) Long limitValue, HttpServletRequest request) {
        long limit = Long.MAX_VALUE;

        if (limitValue != null) {
            limit = limitValue;
        }

        try {
            return ResponseEntity.ok(accountService.getAccountsAsJson(limit));
        } catch (DataAccessException dax) {
            log.error("Request: " + request.getRequestURL() + " raised " + dax);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data Access Error");
        } catch (Exception e) {
            log.error("Request: " + request.getRequestURL() + " raised " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @RequestMapping(value = "v1/accounts/create", method = RequestMethod.POST)
    public ResponseEntity<String> create(@RequestParam(value="loginName") String loginName,
                          @RequestParam(value="firstName") String firstName,
                          @RequestParam(value="lastName") String lastName,
                          @RequestParam(value="password") String password,
                                                          HttpServletRequest request) {
        try {
            Account newAccount = accountService.createAccount(loginName, firstName, lastName, password);

            // construct the location of the created resource
            String uri = request.getRequestURI();
            String url = String.valueOf(request.getRequestURL());
            String host = url.substring(0, url.indexOf(uri) + 1);
            String accountResourceUrl = host + GET_ACCOUNT_URL_FORMAT.replace("{id}", String.valueOf(newAccount.id));

            HttpHeaders locationHeaders = new HttpHeaders();
            locationHeaders.set("Location", accountResourceUrl);

            return ResponseEntity.status(HttpStatus.CREATED).headers(locationHeaders).body(null);
        } catch (DataAccessException dax) {
            log.error("Request: " + request.getRequestURL() + " raised " + dax);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data Access Error");
        } catch (PasswordException px) {
            log.error("Request: " + request.getRequestURL() + " raised " + px);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Password Processing Error");
        } catch (Exception e) {
            log.error("Request: " + request.getRequestURL() + " raised " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CREATE_FAIL_MESSAGE);
        }
    }
}
