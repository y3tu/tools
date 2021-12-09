package com.y3tu.tools.web.sql.ds;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * 某些数据库需要的特殊配置项需要的配置项
     */
    public static final String[] KEY_CONN_PROPS = {"remarks", "useInformationSchema"};

    /**
     * 别名字段名：URL
     */
    public static final String[] KEY_ALIAS_URL = {"url", "jdbcUrl"};
    /**
     * 别名字段名：驱动名
     */
    public static final String[] KEY_ALIAS_DRIVER = {"driver", "driverClassName"};
    /**
     * 别名字段名：用户名
     */
    public static final String[] KEY_ALIAS_USER = {"user", "username"};
    /**
     * 别名字段名：密码
     */
    public static final String[] KEY_ALIAS_PASSWORD = {"pass", "password"};

    /**
     * 数据源池
     */
    private final Map<String, DataSourceWrapper> dsMap;

    /**
     * 数据源配置
     */
    private final DbConfig dbConfig;

    public DsFactory(String dataSourceName, DbConfig dbConfig) {
        this.dataSourceName = dataSourceName;
        this.dsMap = new ConcurrentHashMap<>();
        this.dbConfig = dbConfig;
    }

    /**
     * 获取数据库连接
     *
     * @return {@link DataSource}
     */
    synchronized public DataSource getDataSource() {
        // 如果已经存在已有数据源（连接池）直接返回
        final DataSourceWrapper existedDataSource = dsMap.get(dataSourceName);
        if (existedDataSource != null) {
            return existedDataSource;
        }
        final DataSourceWrapper ds = DataSourceWrapper.wrap(createDataSource(dbConfig), dbConfig.getDriver());
        // 添加到数据源池中，以备下次使用
        dsMap.put(dataSourceName, ds);
        return ds;
    }

    /**
     * 创建新的{@link DataSource}<br>
     *
     * @param dbConfig 数据库连接配置
     * @return {@link DataSource}
     */
    protected abstract DataSource createDataSource(DbConfig dbConfig);


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

    public static DsFactory create(DbConfig dbConfig) {
        return doCreate(dbConfig);
    }

    /**
     * 创建数据源实现工厂
     * 此方法通过“试错”方式查找引入项目的连接池库，按照优先级寻找，一旦寻找到则创建对应的数据源工厂<br>
     * 连接池优先级：Hikari > Druid
     *
     * @param dbConfig 数据库配置项
     * @return 数据源工厂
     */
    private static DsFactory doCreate(DbConfig dbConfig) {
        try {
            return new DruidDsFactory(dbConfig);
        } catch (NoClassDefFoundError e) {
            // ignore

        }
        return null;
    }
}
