package com.y3tu.tools.web.storge.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.pool.intf.PoolFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * sftp对象池工厂
 *
 * @author y3tu
 */
@Slf4j
public class SftpPoolFactory implements PoolFactory<ChannelSftp> {
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

    public SftpPoolFactory(String host, int port, String username, String password, String filePath) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.filePath = filePath;
    }

    @Override
    public ChannelSftp createObject() {
        try {
            JSch jsch = new JSch();
            Session sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
            channelSftp.connect();
            log.info("新建sftp连接！");
            return channelSftp;
        } catch (JSchException e) {
            throw new ToolException("连接sftp失败", e);
        }
    }

    @Override
    public boolean isValid(ChannelSftp channelSftp) {
        try {
            channelSftp.cd("/");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void invalidate(ChannelSftp channelSftp) {
        try {
            Session session = channelSftp.getSession();
            channelSftp.disconnect();
            //断开sftp连接之后，再断开session连接
            if (session != null) {
                session.disconnect();
            }
        } catch (Exception e) {
            throw new ToolException("作废连接sftp失败", e);
        }
    }
}
