package com.y3tu.tools.web.db.page;

import java.util.List;

/**
 * 数据处理接口
 *
 * @author y3tu
 */
@FunctionalInterface
public interface DataHandler<T> {

    /**
     * 处理数据
     *
     * @param dataList
     */
    void handle(List<T> dataList);

}
