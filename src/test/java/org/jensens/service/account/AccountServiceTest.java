package org.jensens.service.account;

import org.jensens.service.account.storage.Accessor;
import org.jensens.service.account.storage.AccessorFactory;
import org.jensens.service.account.storage.Account;
import org.jensens.service.account.storage.DataAccessException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
public class AccountServiceTest {
    private static final String LOGIN_NAME = "i2i";
    private static final String FIRST_NAME = "Rumpelstiltskin";
    private static final String LAST_NAME = "Littleton";
    private static final String PASSWORD = "MyVoiceIsMyPassword";

    @Test
    public void createAccountTest() throws Exception {
        Accessor memAccessor = AccessorFactory.getMemoryAccessor();
        AccountService service = new AccountService(memAccessor);

        Account newAccount = service.createAccount(LOGIN_NAME, FIRST_NAME, LAST_NAME, PASSWORD);

        Assert.assertTrue(LOGIN_NAME.equals(newAccount.loginName));
        Assert.assertTrue(FIRST_NAME.equals(newAccount.firstName));
        Assert.assertTrue(LAST_NAME.equals(newAccount.lastName));
        Assert.assertFalse(PASSWORD.equals(newAccount.passwordHash));
        Assert.assertTrue(newAccount.id > 0);
    }

    @Test
    public void listAccountsTest() throws Exception {
        Accessor memAccessor = AccessorFactory.getMemoryAccessor();
        AccountService service = new AccountService(memAccessor);

        service.createAccount(LOGIN_NAME, FIRST_NAME, LAST_NAME, PASSWORD);
        service.createAccount(LOGIN_NAME, FIRST_NAME, LAST_NAME, PASSWORD);

        List<Account> accounts = service.getAccounts(Long.MAX_VALUE);

        for (Account account : accounts) {
            Assert.assertTrue(LOGIN_NAME.equals(account.loginName));
            Assert.assertTrue(FIRST_NAME.equals(account.firstName));
            Assert.assertTrue(LAST_NAME.equals(account.lastName));
            Assert.assertFalse(PASSWORD.equals(account.passwordHash));
            Assert.assertTrue(account.id > 0);
        }
    }

    @Test
    public void getAccountTest() throws Exception {
        Accessor memAccessor = AccessorFactory.getMemoryAccessor();
        AccountService service = new AccountService(memAccessor);

        Account newAccount = service.createAccount(LOGIN_NAME, FIRST_NAME, LAST_NAME, PASSWORD);
        Account returnedAccount = service.getAccount(newAccount.id);

        Assert.assertTrue(returnedAccount.loginName.equals(newAccount.loginName));
        Assert.assertTrue(returnedAccount.firstName.equals(newAccount.firstName));
        Assert.assertTrue(returnedAccount.lastName.equals(newAccount.lastName));
        Assert.assertTrue(returnedAccount.passwordHash.equals(newAccount.passwordHash));
        Assert.assertEquals(returnedAccount.id, newAccount.id);
    }

    @Test(expected=NotFoundException.class)
    public void getNonexistantAccountTest() throws Exception {
        Accessor memAccessor = AccessorFactory.getMemoryAccessor();
        AccountService service = new AccountService(memAccessor);

        service.getAccount(1337);
    }

    @Test
    public void authenticateTest() throws Exception {
        Accessor memAccessor = AccessorFactory.getMemoryAccessor();
        AccountService service = new AccountService(memAccessor);

        Account newAccount = service.createAccount(LOGIN_NAME, FIRST_NAME, LAST_NAME, PASSWORD);
        Assert.assertTrue(service.authenticatePassword(newAccount.id, PASSWORD));
    }

    @Test(expected=NotFoundException.class)
    public void authenticateNonExistantAccountTest() throws Exception {
        Accessor memAccessor = AccessorFactory.getMemoryAccessor();
        AccountService service = new AccountService(memAccessor);

        service.authenticatePassword(42, PASSWORD);
    }

    @Test(expected = DataAccessException.class)
    public void nullAccessorTest() {
        AccountService service = new AccountService(null);
        service.getAccount(1337);
    }
}
