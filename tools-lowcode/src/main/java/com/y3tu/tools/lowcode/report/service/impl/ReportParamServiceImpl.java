package com.y3tu.tools.lowcode.report.service.impl;

import com.y3tu.tools.lowcode.base.BaseServiceImpl;
import com.y3tu.tools.lowcode.report.entity.domain.ReportParam;
import com.y3tu.tools.lowcode.report.repository.ReportParamRepository;
import com.y3tu.tools.lowcode.report.service.ReportParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author y3tu
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ReportParamServiceImpl extends BaseServiceImpl<ReportParamRepository, ReportParam> implements ReportParamService {

    @Override
    public List<ReportParam> getByReportId(int reportId) {
        return repository.getByReportId(reportId);
    }

    @Override
    public void deleteByReportId(int reportId) {
        repository.deleteByReportId(reportId);
    }
}
