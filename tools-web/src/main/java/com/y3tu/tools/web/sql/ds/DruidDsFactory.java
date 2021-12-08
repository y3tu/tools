package com.y3tu.tools.web.sql.ds;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * Druid数据源工厂类
 *
 * @author y3tu
 */
public class DruidDsFactory extends DsFactory{
    @Override
    public DataSource getDataSource(String group) {
        return null;
    }

    @Override
    protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass) {
        final DruidDataSource ds = new DruidDataSource();
        // 基本信息
        ds.setUrl(jdbcUrl);
        ds.setDriverClassName(driver);
        ds.setUsername(user);
        ds.setPassword(pass);


        return null;
    }

    @Override
    public void close(String group) {

    }

    @Override
    public void destroy() {

    }
}
