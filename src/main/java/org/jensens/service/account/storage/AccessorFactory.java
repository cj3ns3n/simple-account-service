package org.jensens.service.account.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccessorFactory {
    public static Accessor getAccountAccessor() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File dbFile = new File(tempDir, "Accounts");
        String connURL = String.format("jdbc:derby:%s;create=true", dbFile.getPath());

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connURL);
        } catch (SQLException sx) {
            Logger log = LoggerFactory.getLogger(AccessorFactory.class);
            log.error("Could not connect to file DB:" + sx);


            // Failed to make file based DB, use in memory DB.  Make it work no matter what.
            connURL = "jdbc:derby:memory:accounts;create=true";
            try {
                conn = DriverManager.getConnection(connURL);
            } catch (SQLException sx1) {
                log.error("Could not connect to in memory DB:" + sx1);
            }
        }

        try {
            return new SqlAccessor(conn);
        } catch (DataAccessException dax) {
            Logger log = LoggerFactory.getLogger(AccessorFactory.class);
            log.error("Could not instantiate SqlAccesor:" + dax);

            return null;
        }
    }
}
