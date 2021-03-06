package com.y3tu.tools.lowcode.common.repository;

import com.y3tu.tools.lowcode.common.entity.domain.DictData;
import com.y3tu.tools.lowcode.base.BaseRepository;

import java.util.List;

/**
 * @author y3tu
 */
public interface DictDataRepository extends BaseRepository<DictData> {
    /**
     * 根据dictId获取有效的字典数据
     *
     * @param dictId
     * @param status
     * @return
     */
    List<DictData> findAllByDictIdAndStatus(int dictId, String status);
}
