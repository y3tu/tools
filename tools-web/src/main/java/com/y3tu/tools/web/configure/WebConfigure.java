package com.y3tu.tools.web.configure;

import com.y3tu.tools.kit.reflect.ReflectUtil;
import com.y3tu.tools.web.sql.JdbcTemplateContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

/**
 * web工具配置
 *
 * @author y3tu
 */
@Configuration
@Slf4j
public class WebConfigure implements ApplicationRunner {

    @Autowired
    private ConfigurableApplicationContext context;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        //获取spring容器中所有的数据源bean
        String[] beanNames = context.getBeanNamesForType(DataSource.class);
        for (String name : beanNames) {
            DataSource dataSource = context.getBean(name, DataSource.class);
            //dataSourceMap是dynamic-datasource多数据源
            Map dataSourceMap = (Map) ReflectUtil.getFieldValue(dataSource, "dataSourceMap");
            if (dataSourceMap != null && dataSourceMap.keySet().size() > 0) {
                for (Object key : dataSourceMap.keySet()) {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource) dataSourceMap.get(key));
                    JdbcTemplateContainer.addJdbcTemplate(key.toString(), jdbcTemplate);
                    log.info(String.format("检查到数据源:%s,放入JdbcTemplateUtil工具类中", key.toString()));
                }
            } else {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                JdbcTemplateContainer.addJdbcTemplate(name, jdbcTemplate);
                log.info(String.format("检查到数据源:%s,放入JdbcTemplateUtil工具类中", name));
            }
            //设置默认
            JdbcTemplateContainer.setDefaultJdbcTemplate(new JdbcTemplate(dataSource));
        }
    }
}
