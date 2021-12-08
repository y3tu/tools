package com.y3tu.tools.lowcode.common.repository;

import com.y3tu.tools.lowcode.common.entity.domain.Dict;
import com.y3tu.tools.lowcode.base.BaseRepository;

import java.util.List;

/**
 * @author y3tu
 */
public interface DictRepository extends BaseRepository<Dict> {

    /**
     * 根据字典编码查询字典
     *
     * @param code
     * @return
     */
    Dict findByCode(String code);

    /**
     * 根据名称获取字典
     *
     * @param name
     * @return
     */
    List<Dict> getByNameLike(String name);

    /**
     * 根据字典名称或编码获取字典
     *
     * @param name 名称
     * @param code 编码
     * @return
     */
    List<Dict> getByNameLikeOrCodeLike(String name, String code);
}
