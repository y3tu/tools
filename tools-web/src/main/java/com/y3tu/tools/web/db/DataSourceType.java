package com.y3tu.tools.web.db;

/**
 * 数据源类型枚举
 *
 * @author y3tu
 */
public enum DataSourceType {
    /**
     * mysql
     */
    MYSQL("mysql"),
    /**
     * oracle
     */
    ORACLE("oracle");

    private String value;

    DataSourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
