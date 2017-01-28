package org.jensens.service.account.storage;

import java.util.List;

public interface Accessor {
    long addAccount(Account account) throws DataAccessException;
    Account getAccount(long accountId) throws DataAccessException;
    List<Account> getAccounts(long limit) throws DataAccessException;
}
