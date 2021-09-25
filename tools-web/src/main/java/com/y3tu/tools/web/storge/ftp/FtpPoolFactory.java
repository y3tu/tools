package com.y3tu.tools.web.storge.ftp;

import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.pool.intf.PoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.SocketException;

/**
 * ftp客户端对象池
 *
 * @author y3tu
 */
@Slf4j
public class FtpPoolFactory implements PoolFactory<FTPClient> {
    /**
     * 主机ip
     */
    String host;
    /**
     * 端口
     */
    int port;
    /**
     * 用户名
     */
    String username;
    /**
     * 密码
     */
    String password;
    /**
     * 文件路径
     */
    String filePath;

    private static int BUFF_SIZE = 256000;

    public FtpPoolFactory(String host, int port, String username, String password, String filePath) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.filePath = filePath;
    }

    @Override
    public FTPClient createObject() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setDefaultPort(port);
        ftpClient.setConnectTimeout(30000);
        ftpClient.setDataTimeout(180000);
        ftpClient.setControlKeepAliveTimeout(60);
        ftpClient.setControlKeepAliveReplyTimeout(60);
        ftpClient.setControlEncoding("UTF-8");

        FTPClientConfig clientConfig = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        clientConfig.setServerLanguageCode("UTF-8");
        ftpClient.configure(clientConfig);

        try {
            ftpClient.connect(host);
        } catch (SocketException exp) {
            log.error("连接FTP服务超时,IP" + host);
            throw new ToolException(exp.getMessage());
        } catch (IOException exp) {
            log.warn("连接FTP服务:" + host + " 异常:" + exp.getMessage());
            throw new ToolException(exp.getMessage());
        }

        try {
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("FTP服务拒绝连接!");
                return null;
            }
            boolean result = ftpClient.login(username, password);
            if (!result) {
                log.error("FTP服务拒绝连接!");
                throw new ToolException("FTP服务登录失败:" + host + " 用户名密码有误!");
            }
            ftpClient.setBufferSize(BUFF_SIZE);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.COMPRESSED_TRANSFER_MODE);
            ftpClient.changeWorkingDirectory(filePath);
            return ftpClient;
        } catch (Exception e) {
            throw new ToolException("创建ftp对象异常", e);
        }
    }

    @Override
    public boolean isValid(FTPClient ftpClient) {
        try {
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            log.error("验证FTPClient失败:" + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void invalidate(FTPClient ftpClient) {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ToolException("不能断开FTP服务", e);
        } finally {
            // 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new ToolException("不能断开FTP服务", e);
            }
        }
    }
}
