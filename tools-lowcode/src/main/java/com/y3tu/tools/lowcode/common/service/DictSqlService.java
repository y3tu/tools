package com.y3tu.tools.lowcode.common.service;

import com.y3tu.tools.lowcode.common.entity.domain.DictSql;
import com.y3tu.tools.lowcode.base.BaseService;

/**
 * @author y3tu
 */
public interface DictSqlService extends BaseService<DictSql> {
    /**
     * 根据dictId获取字典sql
     *
     * @param dictId
     * @return
     */
    DictSql getByDictId(int dictId);
}
