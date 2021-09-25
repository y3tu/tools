package com.y3tu.tools.kit.pool;

import com.y3tu.tools.kit.pool.intf.PoolFactory;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnectionFactory implements PoolFactory<Connection> {

    private String connectionURL;
    private String userName;
    private String password;

    public JDBCConnectionFactory(String driver, String connectionURL,
                                 String userName, String password) {
        super();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ce) {
            throw new IllegalArgumentException(
                    "Unable to find driver in classpath", ce);
        }
        this.connectionURL = connectionURL;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Connection createObject() {
        try {
            return DriverManager.getConnection(connectionURL, userName,
                    password);
        } catch (SQLException se) {
            throw new IllegalArgumentException(
                    "Unable to create new connection", se);
        }
    }

    @Override
    public boolean isValid(Connection con) {
        if (con == null) {
            return false;
        }
        try {
            return !con.isClosed();
        } catch (SQLException se) {
            return false;
        }
    }

    @Override
    public void invalidate(Connection con) {
        try {
            con.close();
        } catch (SQLException se) {
        }
    }
}
