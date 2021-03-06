package com.y3tu.tools.lowcode.common.service.impl;

import com.y3tu.tools.lowcode.common.entity.domain.DataSource;
import com.y3tu.tools.lowcode.common.repository.DataSourceRepository;
import com.y3tu.tools.lowcode.common.service.DataSourceService;
import com.y3tu.tools.lowcode.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

/**
 * @author y3tu
 */
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Service
public class DataSourceServiceImpl extends BaseServiceImpl<DataSourceRepository, DataSource> implements DataSourceService {

    @Override
    public List<DataSource> getByName(String name) {
        return repository.getByNameLike("%" + name + "%");
    }

    @Override
    public boolean testConnection(DataSource dataSource) {
        try {
            Class.forName(dataSource.getDbDriver());
            //获得数据库链接
            Connection conn = DriverManager.getConnection(dataSource.getDbUrl(), dataSource.getDbUsername(), dataSource.getDbPassword());
            Statement st = conn.createStatement();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
