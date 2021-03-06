package com.y3tu.tools.lowcode.report.service.impl;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.io.FileUtil;
import com.y3tu.tools.lowcode.base.BaseServiceImpl;
import com.y3tu.tools.lowcode.base.PageInfo;
import com.y3tu.tools.lowcode.exception.LowCodeException;
import com.y3tu.tools.lowcode.report.configure.ReportProperties;
import com.y3tu.tools.lowcode.report.entity.domain.Report;
import com.y3tu.tools.lowcode.report.entity.domain.ReportDownload;
import com.y3tu.tools.lowcode.report.entity.dto.ReportDto;
import com.y3tu.tools.lowcode.report.entity.dto.ReportParamDto;
import com.y3tu.tools.lowcode.report.repository.ReportDownloadRepository;
import com.y3tu.tools.lowcode.report.service.ReportDownloadService;
import com.y3tu.tools.lowcode.report.service.ReportService;
import com.y3tu.tools.lowcode.websocket.MessageEndPoint;
import com.y3tu.tools.web.storge.RemoteFileHelper;
import com.y3tu.tools.kit.io.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author y3tu
 */
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Service
public class ReportDownloadServiceImpl extends BaseServiceImpl<ReportDownloadRepository, ReportDownload> implements ReportDownloadService {

    @Autowired
    ReportService reportService;
    @Autowired
    RemoteFileHelper remoteFileHelper;
    @Autowired
    ReportProperties properties;
    @Autowired
    MessageEndPoint messageEndPoint;

    @Override
    public PageInfo page(PageInfo pageInfo) {
        String reportName = pageInfo.getParams().getOrDefault("reportName", "").toString();
        //??????
        List<String> ascArr = pageInfo.getAsc();
        List<String> descArr = pageInfo.getDesc();
        List<Sort.Order> orderList = new ArrayList<>();
        if (ascArr != null) {
            for (String asc : ascArr) {
                Sort.Order order = new Sort.Order(Sort.Direction.ASC, asc);
                orderList.add(order);
            }
        }
        if (descArr != null) {
            for (String desc : descArr) {
                Sort.Order order = new Sort.Order(Sort.Direction.DESC, desc);
                orderList.add(order);
            }
        }
        //????????????current??????1????????????????????????0?????????????????????1
        PageRequest pageable = PageRequest.of(pageInfo.getCurrent() - 1, pageInfo.getSize(), Sort.by(orderList));
        Page<List<Map<String, Object>>> page = repository.getReportDownloadByPage(reportName, pageable);
        pageInfo.setRecords(page.getContent());
        pageInfo.setTotal(page.getTotalElements());
        return pageInfo;
    }

    @Override
    public List<ReportDownload> getByReportId(int reportId) {
        List<String> statusList = new ArrayList<>();
        statusList.add(ReportDownload.STATUS_NORMAL);
        statusList.add(ReportDownload.STATUS_WAIT);
        return repository.getByReportIdAndStatusIsIn(reportId, statusList);
    }

    @Override
    public List<ReportDownload> getWaitData() {
        return repository.getWaitData();
    }

    @Override
    public void handleDownload(ReportDownload reportDownload) {
        try {
            int reportId = reportDownload.getReportId();
            Report report = reportService.getById(reportId);
            String paramJson = reportDownload.getParamJson();
            List<ReportParamDto> params = JsonUtil.parseList(paramJson, ReportParamDto.class);
            ReportDto reportDto = new ReportDto();
            BeanUtils.copyProperties(report, reportDto);
            reportDto.setParams(params);

            //?????????????????????????????????????????????
            String tempFileName = UUID.randomUUID().toString();
            String tempFileNameExcel = tempFileName + ".xlsx";
            String tempFileNameZip = tempFileName + ".zip";
            String tempFilePathExcel = FileUtil.SYS_TEM_DIR + File.separator + tempFileNameExcel;
            String tempFilePathZip = FileUtil.SYS_TEM_DIR + File.separator + tempFileNameZip;
            File file = new File(tempFilePathExcel);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            //??????????????????
            reportService.exportExcel(reportDto, fileOutputStream);
            //??????????????????
            ZipUtil.toZip(tempFilePathExcel, tempFilePathZip, false);
            //???????????????????????????
            boolean flag = remoteFileHelper.upload(properties.getReportRemotePath(), tempFileNameZip, tempFilePathZip);
            //??????report_download?????????
            if (flag) {
                //???????????????????????????????????????
                FileUtil.del(new File(tempFilePathZip));
                reportDownload.setRemoteFilePath(properties.getReportRemotePath() + tempFileNameZip);
                reportDownload.setRealFileName(tempFileName);
                reportDownload.setStatus(ReportDownload.STATUS_NORMAL);
                reportDownload.setUpdateTime(new Date());
                this.update(reportDownload);
                messageEndPoint.sendAllMessage("??????" + reportDto.getName() + "???????????????");
            } else {
                reportDownload.setStatus(ReportDownload.STATUS_ERROR);
                reportDownload.setErrMsg("??????????????????????????????");
                reportDownload.setUpdateTime(new Date());
                this.update(reportDownload);
            }
        } catch (Exception e) {
            reportDownload.setErrMsg(e.getMessage());
            reportDownload.setStatus(ReportDownload.STATUS_ERROR);
            reportDownload.setUpdateTime(new Date());
            this.update(reportDownload);
            log.error(e.getMessage(), e);
            throw new LowCodeException("??????????????????!:" + e.getMessage());
        }

    }

    @Override
    public void download(int id, HttpServletRequest request, HttpServletResponse response) {
        ReportDownload reportDownload = this.getById(id);
        Report report = reportService.getById(reportDownload.getReportId());
        if (!reportDownload.getStatus().equals(ReportDownload.STATUS_NORMAL)) {
            throw new LowCodeException("??????????????????????????????????????????");
        }
        //?????????????????????????????????????????????????????????
        String filePath = FileUtil.SYS_TEM_DIR + File.separator + reportDownload.getRealFileName() + ".xlsx";
        File file = new File(filePath);
        if (!FileUtil.exist(file)) {
            //???????????????????????????????????????????????????????????????
            String filePathZip = FileUtil.SYS_TEM_DIR + File.separator + reportDownload.getRealFileName() + ".zip";
            boolean flag = remoteFileHelper.download(reportDownload.getRemoteFilePath(), filePathZip);
            if (flag) {
                //??????????????????
                ZipUtil.unZip(new File(filePathZip), FileUtil.SYS_TEM_DIR + File.separator, StandardCharsets.UTF_8);
                //??????????????????
                FileUtil.del(new File(filePathZip));
                //??????????????????
                reportDownload.setDownloadTimes(reportDownload.getDownloadTimes() + 1);
                this.update(reportDownload);
                //????????????????????????
                FileUtil.downloadFile(file, report.getName(), false, request, response);
            } else {
                reportDownload.setErrMsg("??????????????????????????????!");
                reportDownload.setUpdateTime(new Date());
                reportDownload.setStatus(ReportDownload.STATUS_ERROR);
                this.update(reportDownload);
                throw new LowCodeException("??????????????????????????????!");
            }
        } else {
            reportDownload.setDownloadTimes(reportDownload.getDownloadTimes() + 1);
            this.update(reportDownload);
            FileUtil.downloadFile(file, report.getName(), false, request, response);
        }
    }
}
