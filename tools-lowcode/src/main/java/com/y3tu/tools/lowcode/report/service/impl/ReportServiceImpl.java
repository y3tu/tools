package com.y3tu.tools.lowcode.report.service.impl;

import com.y3tu.tools.kit.io.FileUtil;
import com.y3tu.tools.kit.lang.R;
import com.y3tu.tools.kit.text.StrUtil;
import com.y3tu.tools.lowcode.base.BaseServiceImpl;
import com.y3tu.tools.lowcode.base.PageInfo;
import com.y3tu.tools.lowcode.common.service.DataSourceService;
import com.y3tu.tools.lowcode.common.util.DataSourceUtil;
import com.y3tu.tools.lowcode.exception.LowCodeException;
import com.y3tu.tools.lowcode.report.configure.ReportProperties;
import com.y3tu.tools.lowcode.report.entity.domain.Report;
import com.y3tu.tools.lowcode.report.entity.domain.ReportAttachment;
import com.y3tu.tools.lowcode.report.entity.domain.ReportParam;
import com.y3tu.tools.lowcode.report.entity.dto.ReportDto;
import com.y3tu.tools.lowcode.report.entity.dto.ReportParamDto;
import com.y3tu.tools.lowcode.report.repository.ReportRepository;
import com.y3tu.tools.lowcode.report.service.CommonReportService;
import com.y3tu.tools.lowcode.report.service.JasperReportService;
import com.y3tu.tools.lowcode.report.service.ReportAttachmentService;
import com.y3tu.tools.lowcode.report.service.ReportParamService;
import com.y3tu.tools.lowcode.report.service.ReportService;
import com.y3tu.tools.lowcode.report.util.JasperReportUtil;
import com.y3tu.tools.web.db.SqlUtil;
import com.y3tu.tools.web.storge.RemoteFileHelper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author y3tu
 */
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Service
public class ReportServiceImpl extends BaseServiceImpl<ReportRepository, Report> implements ReportService {

    @Autowired
    ReportParamService reportParamService;
    @Autowired
    ReportAttachmentService reportAttachmentService;
    @Autowired
    CommonReportService commonReportService;
    @Autowired
    JasperReportService jasperReportService;
    @Autowired
    DataSourceService dataSourceService;
    @Autowired
    RemoteFileHelper remoteFileHelper;
    @Autowired
    ReportProperties properties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReport(ReportDto reportDto) {
        //????????????
        Report report = new Report();
        BeanUtils.copyProperties(reportDto, report);
        report.setCreateTime(new Date());
        report = this.create(report);
        //??????????????????
        createReportParam(reportDto, report.getId());
        //????????????????????????
        if (StrUtil.isNotEmpty(reportDto.getFileName())) {
            //??????????????????????????????
            String tempFileName = reportDto.getFileTempPrefix() + ".jrxml";
            String tempFilePath = FileUtil.SYS_TEM_DIR + tempFileName;
            boolean flag = remoteFileHelper.upload(properties.getTemplateRemotePath(), tempFileName, tempFilePath);
            //????????????????????????
            FileUtil.del(new File(tempFilePath));
            if (flag) {
                //??????????????????
                ReportAttachment reportAttachment = new ReportAttachment();
                reportAttachment.setReportId(report.getId());
                reportAttachment.setStatus("00A");
                reportAttachment.setName(reportDto.getFileName());
                reportAttachment.setRealFileName(tempFileName);
                reportAttachment.setRemoteFilePath(properties.getTemplateRemotePath() + tempFileName);
                reportAttachment.setCreateTime(new Date());
                reportAttachmentService.create(reportAttachment);
            } else {
                throw new LowCodeException("????????????????????????????????????????????????");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReport(ReportDto reportDto) {
        //????????????
        Report report = new Report();
        BeanUtils.copyProperties(reportDto, report);
        report.setUpdateTime(new Date());
        this.update(report);
        //???????????? ?????????????????????????????????
        reportParamService.deleteByReportId(reportDto.getId());
        createReportParam(reportDto, reportDto.getId());
        //???????????????????????? ???????????????????????????????????????????????????FileTempPrefix????????????
        if (StrUtil.isNotEmpty(reportDto.getFileName())
                && StrUtil.isNotEmpty(reportDto.getFileTempPrefix())) {
            //??????????????????????????????
            String tempFileName = reportDto.getFileTempPrefix() + ".jrxml";
            String tempFilePath = FileUtil.SYS_TEM_DIR + tempFileName;
            boolean flag = remoteFileHelper.upload(properties.getTemplateRemotePath(), tempFileName, tempFilePath);
            //????????????????????????
            FileUtil.del(new File(tempFilePath));
            if (flag) {
                //?????????????????? ????????????
                List<ReportAttachment> reportAttachmentList = reportAttachmentService.getByReportId(report.getId());
                if (reportAttachmentList.isEmpty()) {
                    //?????????????????????????????????
                    ReportAttachment reportAttachment = new ReportAttachment();
                    reportAttachment.setReportId(report.getId());
                    reportAttachment.setStatus("00A");
                    reportAttachment.setName(reportDto.getFileName());
                    reportAttachment.setRealFileName(tempFileName);
                    reportAttachment.setRemoteFilePath(properties.getTemplateRemotePath() + tempFileName);
                    reportAttachment.setCreateTime(new Date());
                    reportAttachmentService.create(reportAttachment);
                } else {
                    for (ReportAttachment reportAttachment : reportAttachmentList) {
                        String oldPath = reportAttachment.getRemoteFilePath();
                        reportAttachment.setName(reportDto.getFileName());
                        reportAttachment.setRealFileName(tempFileName);
                        reportAttachment.setRemoteFilePath(properties.getTemplateRemotePath() + tempFileName);
                        reportAttachment.setUpdateTime(new Date());
                        reportAttachmentService.update(reportAttachment);
                        //???????????????
                        if (!remoteFileHelper.remove(oldPath)) {
                            throw new LowCodeException("??????????????????????????????????????????");
                        }
                    }
                }
            } else {
                throw new LowCodeException("????????????????????????????????????????????????");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReport(int reportId) {
        //????????????????????????????????????
        reportParamService.deleteByReportId(reportId);
        this.delete(reportId);
        //??????????????????????????????????????????????????????
        List<ReportAttachment> reportAttachmentList = reportAttachmentService.getByReportId(reportId);
        reportAttachmentService.deleteByReportId(reportId);
        for (ReportAttachment reportAttachment : reportAttachmentList) {
            if (!remoteFileHelper.remove(reportAttachment.getRemoteFilePath())) {
                throw new LowCodeException("??????????????????????????????????????????");
            }
        }
    }

    @Override
    public Report getByName(String name) {
        return this.repository.getByName(name);
    }

    @Override
    public void download(int reportId, HttpServletRequest request, HttpServletResponse response) {
        List<ReportAttachment> reportAttachmentList = reportAttachmentService.getByReportId(reportId);
        for (ReportAttachment reportAttachment : reportAttachmentList) {
            remoteFileHelper.download(reportAttachment.getRemoteFilePath(), reportAttachment.getName(), false, request, response);
        }
    }

    @Override
    public R reportHtml(ReportDto reportDto, HttpServletResponse response) {
        try {
            if (Report.TYPE_COMMON.equals(reportDto.getType())) {
                //????????????
                //??????sql??????
                reportDto.setQuerySql(replaceParamSql(reportDto.getQuerySql(), reportDto.getParams()));
                PageInfo pageInfo = commonReportService.reportHtml(reportDto);
                return R.success(pageInfo);
            } else if (Report.TYPE_JASPER.equals(reportDto.getType())) {
                //Jasper??????
                //??????jasper??????????????????
                Map<String, Object> filePathResult = jasperReportService.getJasperTemplate(reportDto.getId());
                String jrxmlFilePath = filePathResult.get("jrxmlFilePath").toString();
                JasperReport jasperReport = JasperReportUtil.getJasperReport(jrxmlFilePath);
                String querySql = jasperReport.getQuery().getText();
                String html = "";
                PageInfo pageInfo = null;
                if (StrUtil.isNotEmpty(querySql)) {
                    reportDto.setQuerySql(replaceParamSql(querySql, reportDto.getParams()));
                    pageInfo = commonReportService.reportHtml(reportDto);
                    html = jasperReportService.reportHtml(reportDto, pageInfo.getRecords(), jasperReport);
                } else {
                    html = jasperReportService.reportHtml(reportDto, null, jasperReport);
                }
                Map result = new HashMap();
                result.put("html", html);
                result.put("pageInfo", pageInfo);
                return R.success(result);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LowCodeException("??????????????????:" + e.getMessage());
        }
        return R.success();
    }

    @Override
    public boolean isBigData(ReportDto reportDto) {
        handleSql(reportDto);
        int dsId = reportDto.getDsId();
        javax.sql.DataSource ds = DataSourceUtil.getDataSourceByDsId(dsId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        int count = SqlUtil.count(reportDto.getQuerySql(), jdbcTemplate);
        if (count > 100000) {
            //???????????????????????????10w???????????????????????????????????????
            return true;
        }
        return false;
    }

    @Override
    public void exportExcel(ReportDto reportDto, HttpServletResponse response) {
        try {
            handleSql(reportDto);
            //??????sql??????
            if (Report.TYPE_COMMON.equals(reportDto.getType())) {
                commonReportService.exportExcel(reportDto, response);
            } else if (Report.TYPE_JASPER.equals(reportDto.getType())) {
                //??????jasper??????????????????
                jasperReportService.exportExcel(reportDto, response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LowCodeException("????????????excel??????:" + e.getMessage());
        }
    }

    @Override
    public void exportExcel(ReportDto reportDto, OutputStream outputStream) {
        try {
            handleSql(reportDto);
            //??????sql??????
            if (Report.TYPE_COMMON.equals(reportDto.getType())) {
                commonReportService.exportExcel(reportDto, outputStream);
            } else if (Report.TYPE_JASPER.equals(reportDto.getType())) {
                //??????jasper??????????????????
                jasperReportService.exportExcel(reportDto, outputStream);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LowCodeException("????????????excel??????:" + e.getMessage());
        }
    }

    private void createReportParam(ReportDto reportDto, int reportId) {
        for (int i = 0; i < reportDto.getParams().size(); i++) {
            ReportParam param = new ReportParam();
            BeanUtils.copyProperties(reportDto.getParams().get(i), param);
            param.setReportId(reportId);
            param.setSeq(i);
            param.setCreateTime(new Date());
            reportParamService.create(param);
        }
    }


    private void handleSql(ReportDto reportDto) {
        try {
            if (Report.TYPE_COMMON.equals(reportDto.getType())) {
                reportDto.setQuerySql(replaceParamSql(reportDto.getQuerySql(), reportDto.getParams()));
            } else if (Report.TYPE_JASPER.equals(reportDto.getType())) {
                //??????jasper??????????????????
                Map<String, Object> filePathResult = jasperReportService.getJasperTemplate(reportDto.getId());
                String jrxmlFilePath = filePathResult.get("jrxmlFilePath").toString();
                JasperReport jasperReport = JasperReportUtil.getJasperReport(jrxmlFilePath);
                String querySql = jasperReport.getQuery().getText();
                reportDto.setQuerySql(replaceParamSql(querySql, reportDto.getParams()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LowCodeException("????????????sql??????:" + e.getMessage());
        }
    }

    /**
     * ?????????????????????sql???????????????????????????????????????sql
     *
     * @param sql
     * @param params
     * @return
     */
    public static String replaceParamSql(String sql, List<ReportParamDto> params) {
        //??????sql??????
        for (ReportParamDto param : params) {
            String field = param.getField();
            String $field = "${" + field + "}";
            Object value = param.getValue();
            //???????????????
            String result = "";
            if (value instanceof List) {
                //???????????????????????????????????????????????????????????????????????????
                List<String> valueList = (List<String>) value;
                if (valueList.size() > 0) {
                    result = result + "(";
                    for (String val : valueList) {
                        result = result + val + ",";
                    }
                    result = result.substring(0, result.length() - 1);
                    result = result + ")";
                }
            } else {
                if (value != null) {
                    result = value.toString();
                }
            }

            //??????$ifnull[]?????????????????????????????????$ifnull[]???????????????
            String[] ifnulls = StrUtil.subBetweenAll(sql, "$ifnull[", "]");
            for (String ifnull : ifnulls) {
                if (StrUtil.containsAnyIgnoreCase(ifnull, $field)) {
                    if (StrUtil.isEmpty(result)) {
                        //?????????????????????,??????$ifnull[]???????????????
                        sql = sql.replace("$ifnull[" + ifnull + "]", "");
                    } else {
                        sql = sql.replace("$ifnull[" + ifnull + "]", ifnull);
                    }
                }
            }

            sql = sql.replace("${" + field + "}", result);

            parseParam(sql, param.getField(), result);
        }
        return sql;
    }

    /**
     * ??????jasper????????????
     *
     * @param text
     * @param key
     * @param value
     * @return
     */
    public static String parseParam(String text, String key, String value) {
        String param = "$P!{" + key + "}";
        text = StringUtils.replace(text, param, value);
        String paramLower = "$p!{" + key + "}";
        text = StringUtils.replace(text, paramLower, value);
        String paramNo = "$P{" + key + "}";
        text = StringUtils.replace(text, paramNo, value);
        String paramNOLower = "$p{" + key + "}";
        text = StringUtils.replace(text, paramNOLower, value);
        return text;
    }
}
