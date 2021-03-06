package com.y3tu.tools.web.db;

import com.y3tu.tools.kit.concurrent.thread.ThreadUtil;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.web.db.page.DataHandler;
import com.y3tu.tools.web.db.page.DataPageHandlerRunnable;
import com.y3tu.tools.web.db.page.ThreadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 封装常用sql操作
 *
 * @author y3tu
 */
@Slf4j
public class SqlUtil {


    /**
     * 多线程处理大数据分页查询操作
     * <p>
     * 要求 sql居中需要包含$MOD('取模字段',?)=?
     *
     * @param size         线程数
     * @param pageSize     分页每页数据数量
     * @param selectSql    查询数据语句
     * @param selectDsName 查询数据数据源名称
     * @param clazz        查询结果类型
     * @param params       查询参数 格式为 key:1 value:"servId"； key值代表查询sql语句中?占位符的位置
     * @param dataHandler  对查询结果进行处理
     * @return true: 正常处理 false:处理失败或某个线程出现异常
     * @throws Exception
     */
    public static boolean dataPageHandler(int size, int pageSize, String selectSql, String selectDsName, Class clazz, Map<Integer, Object> params, DataHandler dataHandler) {
        try {
            if (!selectSql.contains("$MOD")) {
                throw new ToolException("多线程处理查询数据sql语句必须包含$MOD");
            }

            CountDownLatch cdl = ThreadUtil.newCountDownLatch(size);

            ExecutorService executor = ThreadUtil.newFixedExecutor(size, "dataHandler多线程");
            Map<String, ThreadResult> threadResultMap = new HashMap<>();

            for (int i = 0; i < size; i++) {
                executor.execute(new DataPageHandlerRunnable(selectSql, selectDsName, clazz, params, dataHandler, size, i, pageSize, cdl, threadResultMap));
            }
            cdl.await();
            log.info("------多线程执行完成------");
            executor.shutdown();

            List<Boolean> resultList = threadResultMap.keySet().stream().map(key -> threadResultMap.get(key).isSuccess()).collect(Collectors.toList());

            return !resultList.contains(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public static boolean dataPageHandler(int size, int pageSize, String selectSql, String selectDsName, Map<Integer, Object> params, DataHandler dataHandler) {
        return SqlUtil.dataPageHandler(size, pageSize, selectSql, selectDsName, null, params, dataHandler);
    }

    public static boolean dataPageHandler(int size, int pageSize, String selectSql, String selectDsName, DataHandler dataHandler) {
        return SqlUtil.dataPageHandler(size, pageSize, selectSql, selectDsName, null, null, dataHandler);
    }

    /**
     * 单线程处理数据分页查询操作
     *
     * @param block        是否阻塞调用线程
     * @param pageSize     分页每页数据数量
     * @param selectSql    查询数据语句
     * @param selectDsName 查询数据数据源名称
     * @param clazz        查询结果类型
     * @param params       查询参数 格式为 key:1 value:"servId"； key值代表查询sql语句中?占位符的位置
     * @param dataHandler  对查询结果进行处理
     * @return true: 正常处理 false:处理失败或某个线程出现异常
     * @throws Exception
     */
    public static boolean dataPageHandler(boolean block, int pageSize, String selectSql, String selectDsName, Class clazz, Map<Integer, Object> params, DataHandler dataHandler) throws Exception {
        CountDownLatch cdl = ThreadUtil.newCountDownLatch(1);
        Map<String, ThreadResult> threadResultMap = new HashMap<>();
        ThreadUtil.execute(new DataPageHandlerRunnable(selectSql, selectDsName, clazz, params, dataHandler, 1, 1, pageSize, cdl, threadResultMap));
        if (block) {
            cdl.await();
            log.info("------线程执行完成------");
        }
        List<Boolean> resultList = threadResultMap.keySet().stream().map(key -> threadResultMap.get(key).isSuccess()).collect(Collectors.toList());

        return !resultList.contains(false);
    }


    /**
     * 大数据插入更新操作
     *
     * @param sql      插入sql
     * @param dsName   插入数据源名称
     * @param dataList 待插入或者更新的数据
     * @return
     */
    public static int batchUpdate(String sql, String dsName, List<Map<String, Object>> dataList) {
        List<Object[]> argsList = new ArrayList<>();
        for (Map<String, Object> dto : dataList) {
            List<Object> insertObjList = new ArrayList<>();
            for (String key : dto.keySet()) {
                insertObjList.add(dto.get(key));
            }
            argsList.add(insertObjList.toArray());
        }
        int count = JdbcTemplateContainer.getJdbcTemplate(dsName).batchUpdate(sql, argsList).length;
        log.info(String.format("执行SQL语句:%s 操作数据量:%s", sql, count));
        return count;
    }

    /**
     * 清空表数据
     *
     * @param tableName 表名
     * @param dsName    表数据源名
     */
    public static void truncate(String tableName, String dsName) {
        StringBuilder sql = new StringBuilder();
        sql.append(" truncate table ").append(tableName);
        log.info("执行SQL语句:" + sql.toString());
        JdbcTemplateContainer.getJdbcTemplate(dsName).execute(sql.toString());
    }

    /**
     * 更新sql语句
     *
     * @param sql    sql语句
     * @param dsName 数据源名
     * @return
     */
    public static int update(String sql, String dsName) {
        log.info("执行SQL语句:" + sql);
        return JdbcTemplateContainer.getJdbcTemplate(dsName).update(sql);
    }

    /**
     * 执行sql语句
     *
     * @param sql    sql语句
     * @param dsName 数据源名
     */
    public static void execute(String sql, String dsName) {
        log.info("执行SQL语句:" + sql);
        JdbcTemplateContainer.getJdbcTemplate(dsName).execute(sql);
    }


    /**
     * 计算数据量
     *
     * @param sql
     * @param dsName
     * @return
     */
    public static int count(String sql, String dsName) {
        return count(sql, JdbcTemplateContainer.getJdbcTemplate(dsName));
    }

    /**
     * 计算数据量
     *
     * @param sql
     * @param jdbcTemplate
     * @return
     */
    public static int count(String sql, JdbcTemplate jdbcTemplate) {
        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(*) as count from (").append(sql).append(") countTable ");
        Map<String, Object> data = jdbcTemplate.queryForMap(countSql.toString());
        log.info(countSql.toString());
        if (data.get("count") != null) {
            int count = Integer.parseInt(data.get("count").toString());
            return count;
        }
        return 0;
    }

    /**
     * 查询数据集合
     *
     * @param sql    查询sql
     * @param params 参数参数
     * @param dsName 数据源名
     * @return
     */
    public static List<Map<String, Object>> queryList(String sql, Map<String, Object> params, String dsName) {
        return queryList(sql, params, JdbcTemplateContainer.getJdbcTemplate(dsName));
    }

    /**
     * 查询数据集合
     *
     * @param sql          查询sql
     * @param params       参数参数
     * @param jdbcTemplate
     * @return
     */
    public static List<Map<String, Object>> queryList(String sql, Map<String, Object> params, JdbcTemplate jdbcTemplate) {
        List<Object> paramList = new ArrayList<>();
        if (params != null) {
            for (String key : params.keySet()) {
                paramList.add(params.get(key));
            }
        }
        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql, paramList.toArray());
        return dataList;
    }

    /**
     * 查询数据集合
     *
     * @param sql    查询sql
     * @param params 参数参数
     * @param clazz  数据对应实体
     * @param dsName 数据源名
     * @return
     */
    public static List queryList(String sql, Map<String, Object> params, Class clazz, String dsName) {
        return queryList(sql, params, clazz, JdbcTemplateContainer.getJdbcTemplate(dsName));
    }

    /**
     * 查询数据集合
     *
     * @param sql          查询sql
     * @param params       参数参数
     * @param clazz        数据对应实体
     * @param jdbcTemplate
     * @return
     */
    public static List queryList(String sql, Map<String, Object> params, Class clazz, JdbcTemplate jdbcTemplate) {
        List<Object> paramList = new ArrayList<>();
        if (params != null) {
            for (String key : params.keySet()) {
                paramList.add(params.get(key));
            }
        }
        List dataList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(clazz), paramList.toArray());
        return dataList;
    }

    /**
     * 拼接sql分页语句
     * current从0开始
     *
     * @param type 数据源类型
     * @param sql  查询sql
     * @return
     */
    public static String buildPageSql(DataSourceType type, String sql, int current, int pageSize) {
        StringBuilder pageSql = new StringBuilder();
        int startIndex = current * pageSize;
        int endIndex = current * pageSize + pageSize;
        if (type == DataSourceType.MYSQL) {
            //mysql
            pageSql.append(sql).append(" limit ").append(startIndex).append(",").append(pageSize);
        } else if (type == DataSourceType.ORACLE) {
            //oracle
            pageSql.append("SELECT * FROM ( SELECT row_.*, rownum rownum_ from (").append(sql)
                    .append(" ) row_ where rownum <=").append(endIndex).append(") table_alias where table_alias.rownum_ >")
                    .append(startIndex);
        }
        return pageSql.toString();
    }


}
