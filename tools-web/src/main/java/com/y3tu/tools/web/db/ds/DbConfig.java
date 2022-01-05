package com.y3tu.tools.web.db.ds;

import lombok.Data;

import java.util.Properties;

/**
 * 数据库配置
 *
 * @author y3tu
 */
@Data
public class DbConfig {
    /**
     * 数据源名称
     */
    private String dataSourceName;
    /**
     * 数据库驱动
     */
    private String driver;
    /**
     * JDBC Url
     */
    private String url;
    /**
     * 用户名
     */
    private String user;
    /**
     * 密码
     */
    private String pass;

    /**
     * 初始连接数
     */
    private int initialSize;
    /**
     * 最小闲置连接数
     */
    private int minIdle;
    /**
     * 最大活跃连接数
     */
    private int maxActive;
    /**
     * 获取连接的超时等待
     */
    private long maxWait;
    /**
     * 连接配置
     */
    private Properties connProps;

}
