package com.y3tu.tools.lowcode.common.entity.constant;

/**
 * 数据状态枚举
 *
 * @author y3tu
 */
public enum DataStatusEnum {

    /**
     * 正常数据
     */
    NORMAL("00A", "正常"),
    /**
     * 禁用数据
     */
    DISABLE("00X", "禁用");


    private String value;

    private String message;

    DataStatusEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
