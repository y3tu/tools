package com.y3tu.tools.lowcode.common.util;


import com.y3tu.tools.lowcode.common.entity.domain.DataSource;
import com.y3tu.tools.lowcode.common.service.DataSourceService;
import com.y3tu.tools.web.db.ds.DataSourceWrapper;
import com.y3tu.tools.web.db.ds.DbConfig;
import com.y3tu.tools.web.db.ds.DsFactory;
import com.y3tu.tools.web.util.SpringContextUtil;

/**
 * 数据源工具类
 *
 * @author y3tu
 */
public class DataSourceUtil {

    /**
     * 根据数据源ID获取数据源
     *
     * @param dsId
     * @return
     */
    public static javax.sql.DataSource getDataSourceByDsId(int dsId) {
        DataSourceService dataSourceService = (DataSourceService) SpringContextUtil.getBean(DataSourceService.class);
        DataSource dataSource = (DataSource) dataSourceService.getById(dsId);
        return getDataSource(dataSource);
    }


    /**
     * 根据数据源配置获取数据源
     *
     * @param dataSource 数据源配置
     * @return 数据源连接
     */
    public static javax.sql.DataSource getDataSource(DataSource dataSource) {

        DbConfig dbConfig = new DbConfig();

        dbConfig.setUrl(dataSource.getDbUrl());
        dbConfig.setDriver(dataSource.getDbDriver());
        dbConfig.setUser(dataSource.getDbUsername());
        dbConfig.setPass(dataSource.getDbPassword());


        DsFactory dsFactory = DsFactory.create(dbConfig);
        DataSourceWrapper dataSourceWrapper = (DataSourceWrapper) dsFactory.getDataSource();
        javax.sql.DataSource ds = dataSourceWrapper.getRaw();
        return ds;
    }
}
