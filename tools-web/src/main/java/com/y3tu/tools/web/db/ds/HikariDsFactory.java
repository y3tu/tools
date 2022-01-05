package com.y3tu.tools.web.db.ds;

import com.y3tu.tools.kit.text.StrUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Hikari数据源
 *
 * @author y3tu
 */
public class HikariDsFactory extends DsFactory {

    public HikariDsFactory(DbConfig dbConfig) {
        super(dbConfig);
    }

    @Override
    protected DataSource createDataSource(DbConfig dbConfig) {
        final Properties properties = new Properties();
        final Properties connProperties = new Properties();
        // 基本信息
        properties.put("jdbcUrl", dbConfig.getUrl());
        properties.put("driverClassName", dbConfig.getDriver());
        properties.put("username", dbConfig.getUser());
        properties.put("password", dbConfig.getPass());

        // remarks等特殊配置
        String connValue;
        for (String key : KEY_CONN_PROPS) {
            connValue = dbConfig.getConnProps().getProperty(key);
            if (StrUtil.isNotBlank(connValue)) {
                connProperties.put(key, connValue);
            }
        }

        final HikariConfig hikariConfig = new HikariConfig(properties);
        hikariConfig.setDataSourceProperties(connProperties);
        return new HikariDataSource(hikariConfig);
    }

}
