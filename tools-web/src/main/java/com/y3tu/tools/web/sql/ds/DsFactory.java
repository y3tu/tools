package com.y3tu.tools.web.sql.ds;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * 数据源工厂
 *
 * @author y3tu
 */
public abstract class DsFactory implements Cloneable, Serializable {

    /**
     * 数据源名称
     */
    protected final String dataSourceName;

    public DsFactory(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    /**
     * 获得分组对应数据源
     *
     * @param group 分组名
     * @return 数据源
     */
    public abstract DataSource getDataSource(String group);

    /**
     * 创建新的{@link DataSource}<br>
     *
     * @param jdbcUrl JDBC连接字符串
     * @param driver 数据库驱动类名
     * @param user 用户名
     * @param pass 密码
     * @return {@link DataSource}
     */
    protected abstract DataSource createDataSource(String jdbcUrl, String driver, String user, String pass);


    /**
     * 关闭对应数据源
     *
     * @param group 分组
     */
    public abstract void close(String group);
    /**
     * 销毁工厂类，关闭所有数据源
     */
    public abstract void destroy();


}
