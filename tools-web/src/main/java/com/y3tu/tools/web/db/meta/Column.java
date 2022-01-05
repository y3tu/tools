package com.y3tu.tools.web.db.meta;

import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.text.StrUtil;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库表的列信息
 *
 * @author y3tu
 */
@Data
public class Column {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 列名
     */
    private String name;
    /**
     * 类型，对应java.sql.Types中的类型
     */
    private int type;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 大小或数据长度
     */
    private int size;
    /**
     * 保留小数位数
     */
    private Integer digit;
    /**
     * 是否为可空
     */
    private boolean isNullable;
    /**
     * 注释
     */
    private String comment;
    /**
     * 是否自增
     */
    private boolean autoIncrement;
    /**
     * 字段默认值
     */
    private String columnDef;
    /**
     * 是否为主键
     */
    private boolean isPk;

    /**
     * 创建列对象
     *
     * @param columnMetaRs 列元信息的ResultSet
     * @param table        表信息
     * @return 列对象
     */
    public static Column create(Table table, ResultSet columnMetaRs) {
        return new Column(table, columnMetaRs);
    }

    /**
     * 构造
     *
     * @param table        表信息
     * @param columnMetaRs Meta信息的ResultSet
     */
    public Column(Table table, ResultSet columnMetaRs) {
        try {
            init(table, columnMetaRs);
        } catch (SQLException e) {
            throw new ToolException(StrUtil.format("Get table [{}] meta info error!", tableName));
        }
    }

    /**
     * 初始化
     *
     * @param table        表信息
     * @param columnMetaRs 列的meta ResultSet
     * @throws SQLException SQL执行异常
     */
    public void init(Table table, ResultSet columnMetaRs) throws SQLException {
        this.tableName = table.getTableName();

        this.name = columnMetaRs.getString("COLUMN_NAME");
        this.isPk = table.isPk(this.name);

        this.type = columnMetaRs.getInt("DATA_TYPE");
        this.typeName = columnMetaRs.getString("TYPE_NAME");
        this.size = columnMetaRs.getInt("COLUMN_SIZE");
        this.isNullable = columnMetaRs.getBoolean("NULLABLE");
        this.comment = columnMetaRs.getString("REMARKS");
        this.columnDef = columnMetaRs.getString("COLUMN_DEF");

        // 保留小数位数
        try {
            this.digit = columnMetaRs.getInt("DECIMAL_DIGITS");
        } catch (SQLException ignore) {
            //某些驱动可能不支持，跳过
        }

        // 是否自增
        try {
            String auto = columnMetaRs.getString("IS_AUTOINCREMENT");
            if (Boolean.parseBoolean(auto)) {
                this.autoIncrement = true;
            }
        } catch (SQLException ignore) {
            //某些驱动可能不支持，跳过
        }
    }

    @Override
    public String toString() {
        return "Column [tableName=" + tableName + ", name=" + name + ", type=" + type + ", size=" + size + ", isNullable=" + isNullable + "]";
    }
}
