package com.y3tu.tools.web.db.page;

import lombok.Data;

import java.io.Serializable;

/**
 * 线程执行结果
 *
 * @author y3tu
 */
@Data
public class ThreadResult implements Serializable {

    /**
     * 线程是否执行成功
     */
    private boolean isSuccess;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 返回信息
     */
    private Object data;

}
