package com.y3tu.tools.lowcode.common.service.impl;

import com.y3tu.tools.lowcode.common.entity.domain.DictData;
import com.y3tu.tools.lowcode.common.repository.DictDataRepository;
import com.y3tu.tools.lowcode.common.service.DictDataService;
import com.y3tu.tools.lowcode.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author y3tu
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictDataServiceImpl extends BaseServiceImpl<DictDataRepository, DictData> implements DictDataService {

}
