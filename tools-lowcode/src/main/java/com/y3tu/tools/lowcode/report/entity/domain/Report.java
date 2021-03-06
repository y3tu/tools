package com.y3tu.tools.lowcode.report.entity.domain;

import com.y3tu.tools.lowcode.annotation.Query;
import com.y3tu.tools.lowcode.base.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 报表
 *
 * @author y3tu
 */
@Entity
@Table(name = "report")
@Data
public class Report extends BaseEntity {
    /**
     * 通用报表
     */
    public static final String TYPE_COMMON = "common";
    /**
     * Jasper报表
     */
    public static final String TYPE_JASPER = "jasper";

    public static final String STATUS_NORMAL = "00A";
    public static final String STATUS_DISABLE= "00X";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true, columnDefinition = "varchar(50) comment '名称'")
    @Query(type = Query.Type.INNER_LIKE)
    String name;

    @Column(columnDefinition = "varchar(1000) comment '备注'")
    String remarks;

    @Column(columnDefinition = "varchar(10) comment '状态 00A:正常 00X：禁用'")
    String status;

    @Column(columnDefinition = "varchar(10) comment '类型 common:通用报表 jasper:Jasper报表 '")
    String type;

    @Column(name = "ds_id", columnDefinition = "int(10) comment '数据源ID'")
    Integer dsId;

    @Column(name = "table_header", columnDefinition = "varchar(5000) comment '表格头'")
    String tableHeader;

    @Column(name = "query_sql", columnDefinition = "varchar(5000) comment '报表查询sql'")
    String querySql;

    @Column(name = "view_count", columnDefinition = "int(15) default 0 comment '浏览次数' ")
    Integer viewCount;


    /**
     * 数据库中不存在此字段用@Transient注解
     */
    @Transient
    String test;

}
