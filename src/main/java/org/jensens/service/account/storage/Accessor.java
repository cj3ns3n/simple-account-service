package org.jensens.service.account.storage;

import java.sql.SQLException;
import java.util.List;

public interface Accessor {
    long addAccount(Account account) throws SQLException ;
    Account getAccount(long accountId) throws SQLException;
    List<Account> getAccounts(long limit) throws SQLException;
}
