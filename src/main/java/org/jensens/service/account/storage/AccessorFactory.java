package org.jensens.service.account.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccessorFactory {
    public static Accessor getAccountAccessor() {
        Accessor accessor = getFileBasedAccessor();
        if (accessor == null) {
            accessor = getMemoryAccessor();  // Failsafe database
        }

        return accessor;
    }

    public static Accessor getFileBasedAccessor() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File dbFile = new File(tempDir, "Accounts");
        String connUrl = String.format("jdbc:derby:%s;create=true", dbFile.getPath());
        try {
            return getAccessorFromConnectionUrl(connUrl);
        } catch (SQLException|DataAccessException ex) {
            Logger log = LoggerFactory.getLogger(AccessorFactory.class);
            log.error("Could not instantiate in memory DB:" + ex);

            return null;
        }
    }

    public static Accessor getMemoryAccessor() {
        try {
            return getAccessorFromConnectionUrl("jdbc:derby:memory:accounts;create=true");
        } catch (SQLException|DataAccessException ex) {
            Logger log = LoggerFactory.getLogger(AccessorFactory.class);
            log.error("Could not instantiate in memory DB:" + ex);

            return null;
        }
    }

    private static Accessor getAccessorFromConnectionUrl(String connUrl) throws SQLException, DataAccessException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File dbFile = new File(tempDir, "Accounts");

        Connection conn = DriverManager.getConnection(connUrl);
        return new SqlAccessor(conn);
    }
}
