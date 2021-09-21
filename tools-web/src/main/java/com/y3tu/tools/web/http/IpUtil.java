package com.y3tu.tools.web.http;

import com.y3tu.tools.kit.exception.ExceptionUtil;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.io.FileUtil;
import com.y3tu.tools.kit.io.ResourceUtil;
import com.y3tu.tools.kit.lang.Validator;
import com.y3tu.tools.kit.regex.RegexUtil;
import com.y3tu.tools.kit.system.SystemUtil;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;

/**
 * ip工具类
 *
 * @author y3tu
 */
public class IpUtil {

    /**
     * 临时存放ip配置文件路径
     */
    public static String ipFileTempPath = SystemUtil.get(SystemUtil.TMPDIR) + "ip2region.db";


    /**
     * 获取客户端IP地址
     *
     * @param request 请求
     * @return ip地址
     */
    public static String getIpAddr(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 根据ip信息获取地理信息
     *
     * @param ip ip
     * @return ip所在区域
     */
    public static String getRegion(String ip) {
        try {
            File file = new File(ipFileTempPath);
            if (!file.exists()) {
                //如果文件不存在
                InputStream inputStream = ResourceUtil.getStream("ip2region/ip2region.db");
                file = FileUtil.writeFromStream(file, inputStream, true);
            }
            DbConfig config = new DbConfig();
            DbSearcher searcher = new DbSearcher(config, file.getPath());
            Method method = searcher.getClass().getMethod("btreeSearch", String.class);

            DataBlock dataBlock = null;
            if (!Validator.isMatchRegex(RegexUtil.IPV4, ip)) {
                throw new ToolException("ip地址异常");
            }
            dataBlock = (DataBlock) method.invoke(searcher, ip);
            return dataBlock.getRegion();
        } catch (Exception e) {
            throw new ToolException("获取ip归属区域异常," + ExceptionUtil.getFormatMessage(e), e);
        }
    }
}
