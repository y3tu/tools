package com.y3tu.tools.web.db.ds;

import com.alibaba.druid.pool.DruidDataSource;
import com.y3tu.tools.kit.text.StrUtil;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Druid数据源工厂类
 *
 * @author y3tu
 */
public class DruidDsFactory extends DsFactory {
    /**
     * 构造
     *
     * @param dbConfig 数据库配置
     */
    public DruidDsFactory(DbConfig dbConfig) {
        super(dbConfig);
    }

    @Override
    protected DataSource createDataSource(DbConfig dbConfig) {
        final DruidDataSource ds = new DruidDataSource();
        // 基本信息
        ds.setUrl(dbConfig.getUrl());
        ds.setDriverClassName(dbConfig.getDriver());
        ds.setUsername(dbConfig.getUser());
        ds.setPassword(dbConfig.getPass());
        // remarks等特殊配置
        String connValue;
        for (String key : KEY_CONN_PROPS) {
            connValue = dbConfig.getConnProps().getProperty(key);
            if (StrUtil.isNotBlank(connValue)) {
                ds.addConnectionProperty(key, connValue);
            }
        }

        // Druid连接池配置信息，规范化属性名
        final Properties druidProps = new Properties();
        dbConfig.getConnProps().forEach((key, value) -> druidProps.put(StrUtil.prependIfMissing(key.toString(), "druid.", false), value));
        ds.configFromPropety(druidProps);

        // 检查关联配置，在用户未设置某项配置时

        if (null == ds.getValidationQuery()) {
            // 在validationQuery未设置的情况下，以下三项设置都将无效
            ds.setTestOnBorrow(false);
            ds.setTestOnReturn(false);
            ds.setTestWhileIdle(false);
        }

        return ds;
    }

}
