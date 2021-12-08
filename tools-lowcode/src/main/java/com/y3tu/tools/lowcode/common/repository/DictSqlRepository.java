package com.y3tu.tools.lowcode.common.repository;

import com.y3tu.tools.lowcode.common.entity.domain.DictSql;
import com.y3tu.tools.lowcode.base.BaseRepository;

/**
 * @author y3tu
 */
public interface DictSqlRepository extends BaseRepository<DictSql> {

    /**
     * 根据dictId查询dictSql
     *
     * @param dictId
     * @return
     */
    DictSql findByDictId(int dictId);
}
