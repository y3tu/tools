package com.y3tu.tools.lowcode.common.service;

import com.y3tu.tools.lowcode.common.entity.domain.DataSource;
import com.y3tu.tools.lowcode.base.BaseService;

import java.util.List;

/**
 * @author y3tu
 */
public interface DataSourceService extends BaseService<DataSource> {

    /**
     * 根据名称获取数据源
     *
     * @param name
     * @return
     */
    List<DataSource> getByName(String name);

    /**
     * 测试数据源连接
     *
     * @param dataSource 数据源配置
     * @return
     */
    boolean testConnection(DataSource dataSource);
}
