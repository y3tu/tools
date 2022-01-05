package com.y3tu.tools.web.db.meta;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 数据库表信息
 *
 * @author y3tu
 */
@Data
public class Table {
    /**
     * table所在schema
     */
    private String schema;
    /**
     * table所在catalog
     */
    private String catalog;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 注释
     */
    private String comment;
    /**
     * 主键字段列表
     */
    private Set<String> pkNames = new LinkedHashSet<>();

    /**
     * 表字段
     */
    private final Map<String, Column> columns = new LinkedHashMap<>();

    /**
     * 给定列名是否为主键
     *
     * @param columnName 列名
     * @return 是否为主键
     */
    public boolean isPk(String columnName) {
        return getPkNames().contains(columnName);
    }

    /**
     * 添加主键
     *
     * @param pkColumnName 主键的列名
     * @return 自己
     */
    public Table addPk(String pkColumnName) {
        this.pkNames.add(pkColumnName);
        return this;
    }

    /**
     * 设置列对象
     *
     * @param column 列对象
     * @return 自己
     */
    public Table setColumn(Column column) {
        this.columns.put(column.getName(), column);
        return this;
    }
}
