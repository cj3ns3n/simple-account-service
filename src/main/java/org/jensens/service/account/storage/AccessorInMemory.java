package org.jensens.service.account.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessorInMemory {
    private Connection conn;

    private static final String ACCOUNT_TABLE = "ACCOUNT";
    private static final String CREATE_ACCOUNT_TABLE = String.format("CREATE TABLE %s " +
            "(ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            " FIRST_NAME VARCHAR(256) NOT NULL," +
            " LAST_NAME VARCHAR(256) NOT NULL," +
            " LOGIN_NAME VARCHAR(256) NOT NULL," +
            " PASSWORD_HASH VARCHAR(512) NOT NULL)", ACCOUNT_TABLE);

    public AccessorInMemory() {
        final String connURL = "jdbc:derby:memory:accounts;create=true";

        try {
            conn = DriverManager.getConnection(connURL);
            initAccountTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initAccountTable() throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet res = meta.getTables(null, null, ACCOUNT_TABLE, new String[] {"TABLE"});
        while (res.next()) {
            System.out.println(
                    "   "+res.getString("TABLE_CAT")
                            + ", "+res.getString("TABLE_SCHEM")
                            + ", "+res.getString("TABLE_NAME")
                            + ", "+res.getString("TABLE_TYPE")
                            + ", "+res.getString("REMARKS"));

            if (res.getString("TABLE_NAME").equals(ACCOUNT_TABLE)) {
                return;
            }
        }

        Statement createStatement = conn.createStatement();
        try {
            createStatement.execute(CREATE_ACCOUNT_TABLE);
        } catch (Exception ex) {
            throw ex;
        }
        finally {
            createStatement.close();
        }
    }

    public Account getAccount(long accountId) throws SQLException {
        String selectStmtStr = String.format("SELECT * FROM %s WHERE ID = %d", ACCOUNT_TABLE, accountId);
        Account account = null;

        Statement createStatement = conn.createStatement();
        ResultSet acctResult = createStatement.executeQuery(selectStmtStr);
        if (acctResult.next()) {
            account = toAccount(acctResult);
        }
        createStatement.close();

        return account;
    }

    public List<Account> getAccounts(long limit) throws SQLException {
        String selectStmtStr = String.format("SELECT * FROM %s", ACCOUNT_TABLE);
        List<Account> accounts = new ArrayList<Account>();
        long count = 0;

        Statement createStatement = conn.createStatement();
        ResultSet acctResult = createStatement.executeQuery(selectStmtStr);
        while (acctResult.next() && count < limit) {
            accounts.add(toAccount(acctResult));
            count++;
        }

        return accounts;
    }

    private Account toAccount(ResultSet acctResult) throws SQLException {
        Account account = new Account();
        account.id = acctResult.getLong("ID");
        account.firstName = acctResult.getString("FIRST_NAME");
        account.lastName = acctResult.getString("LAST_NAME");
        account.loginName = acctResult.getString("LOGIN_NAME");
        account.passwordHash = acctResult.getString("PASSWORD_HASH");

        return account;
    }

    public long addAccount(Account account) throws SQLException {
        String insertStmtStr = String.format("INSERT INTO %s (FIRST_NAME, LAST_NAME, LOGIN_NAME, PASSWORD_HASH) VALUES (?, ?, ?, ?)",
                ACCOUNT_TABLE);
        PreparedStatement insertStatement = conn.prepareStatement(insertStmtStr, Statement.RETURN_GENERATED_KEYS);
        insertStatement.setString(1, account.firstName);
        insertStatement.setString(2, account.lastName);
        insertStatement.setString(3, account.loginName);
        insertStatement.setString(4, account.passwordHash);

        try {
            insertStatement.executeUpdate();

            ResultSet keysResult = insertStatement.getGeneratedKeys();
            if (keysResult != null && keysResult.next()) {
                return keysResult.getLong(1);
            }
            else {
                throw new RuntimeException("Failed to read account ID");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            insertStatement.close();
        }
    }
}
