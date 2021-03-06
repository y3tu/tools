package com.y3tu.tools.lowcode.report.rest;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.lang.R;
import com.y3tu.tools.kit.text.StrUtil;
import com.y3tu.tools.lowcode.base.PageInfo;
import com.y3tu.tools.lowcode.report.entity.domain.Report;
import com.y3tu.tools.lowcode.report.entity.domain.ReportDownload;
import com.y3tu.tools.lowcode.report.entity.dto.ReportDto;
import com.y3tu.tools.lowcode.report.entity.dto.ReportParamDto;
import com.y3tu.tools.lowcode.report.service.ReportDownloadService;
import com.y3tu.tools.lowcode.report.service.ReportService;
import com.y3tu.tools.lowcode.websocket.MessageEndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 报表生成日志
 *
 * @author y3tu
 */
@RestController
@RequestMapping("tools-lowcode/reportDownload")
public class ReportDownloadController {
    @Autowired
    ReportDownloadService reportDownloadService;
    @Autowired
    ReportService reportService;
    @Autowired
    MessageEndPoint messageEndPoint;

    @PostMapping("page")
    public R page(@RequestBody PageInfo<ReportDownload> pageInfo) {
        String reportName = pageInfo.getParams().getOrDefault("reportName", "").toString();
        if (StrUtil.isNotEmpty(reportName)) {
            Report report = reportService.getByName(reportName);
            if (report != null) {
                if (pageInfo.getEntity() == null) {
                    pageInfo.setEntity(new ReportDownload());
                }
                pageInfo.getEntity().setReportId(report.getId());
            }
        }
        return R.success(reportDownloadService.page(pageInfo));
    }

    @PostMapping("create")
    public R create(@RequestBody ReportDto reportDto) {
        //首先判断此报表是否已经存在
        List<ReportDownload> reportDownloadList = reportDownloadService.getByReportId(reportDto.getId());
        boolean createFlag = false;
        if (reportDownloadList.isEmpty()) {
            //生成下载记录
            createFlag = true;
        } else {
            //如果存在此报表的下载记录，再判断查询参数是否相同
            ReportDownload reportDownload = reportDownloadList.get(0);
            String paramJson = reportDownload.getParamJson();
            List<ReportParamDto> params = reportDto.getParams();
            if (paramJson.equals(JsonUtil.toJson(params))) {
                //参数也相同，不需要再生成下载记录
                createFlag = false;
            } else {
                //参数不同，需要生成下载记录
                createFlag = true;
            }
        }
        if (createFlag) {
            ReportDownload reportDownload = new ReportDownload();
            reportDownload.setReportId(reportDto.getId());
            reportDownload.setDownloadTimes(0);
            reportDownload.setCreateTime(new Date());
            reportDownload.setParamJson(JsonUtil.toJson(reportDto.getParams()));
            reportDownload.setStatus("00W");
            reportDownloadService.create(reportDownload);
            return R.success(reportDownload);
        } else {
            return R.success(reportDownloadList.get(0));
        }
    }

    /**
     * 下载报表
     *
     * @param id
     * @param request
     * @param response
     */
    @GetMapping("download/{id}")
    public void download(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
        reportDownloadService.download(id, request, response);
    }

    /**
     * 重新处理报表
     * @param id
     */
    @GetMapping("handleAgain/{id}")
    public void handleAgain(@PathVariable int id){
        ReportDownload reportDownload = reportDownloadService.getById(id);
        reportDownload.setStatus(ReportDownload.STATUS_WAIT);
        reportDownload.setUpdateTime(new Date());
        reportDownload.setErrMsg("");
        reportDownloadService.update(reportDownload);
    }

}
