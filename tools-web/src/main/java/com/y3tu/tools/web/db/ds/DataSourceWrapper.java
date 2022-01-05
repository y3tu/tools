package com.y3tu.tools.web.db.ds;

import com.y3tu.tools.kit.io.IoUtil;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 数据源实现包装
 *
 * @author y3tu
 */
public class DataSourceWrapper implements DataSource, Closeable, Cloneable {
    /**
     * 原始数据源
     */
    private final DataSource ds;
    /**
     * 数据库驱动类名
     */
    private final String driver;

    /**
     * 包装指定的DataSource
     *
     * @param ds     原始数据源
     * @param driver 数据库驱动类名
     * @return {@link DataSourceWrapper}
     */
    public static DataSourceWrapper wrap(DataSource ds, String driver) {
        return new DataSourceWrapper(ds, driver);
    }

    /**
     * 构造方法
     *
     * @param ds     原始数据源
     * @param driver 数据库驱动类名
     */
    public DataSourceWrapper(DataSource ds, String driver) {
        this.ds = ds;
        this.driver = driver;
    }

    /**
     * 获取驱动名
     *
     * @return 驱动名
     */
    public String getDriver() {
        return this.driver;
    }

    /**
     * 获取原始的数据源
     *
     * @return 原始数据源
     */
    public DataSource getRaw() {
        return this.ds;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return ds.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        ds.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        ds.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return ds.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return ds.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return ds.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return ds.isWrapperFor(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return ds.getConnection(username, password);
    }

    @Override
    public void close() {
        if (this.ds instanceof AutoCloseable) {
            IoUtil.close((AutoCloseable) this.ds);
        }
    }
}
