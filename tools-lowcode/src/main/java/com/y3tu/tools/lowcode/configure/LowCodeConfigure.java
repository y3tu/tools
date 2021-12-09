package com.y3tu.tools.lowcode.configure;

import com.y3tu.tools.kit.pool.support.BoundedBlockingPool;
import com.y3tu.tools.kit.pool.support.PoolFactoryUtil;
import com.y3tu.tools.kit.time.DateUnit;
import com.y3tu.tools.lowcode.exception.LowCodeException;
import com.y3tu.tools.lowcode.report.entity.constant.RemoteMode;
import com.y3tu.tools.web.storge.RemoteFileHelper;
import com.y3tu.tools.web.storge.ftp.FtpHelper;
import com.y3tu.tools.web.storge.ftp.FtpPoolFactory;
import com.y3tu.tools.web.storge.sftp.SftpHelper;
import com.y3tu.tools.web.storge.sftp.SftpPoolFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 低代码服务配置
 *
 * @author y3tu
 */
@Configuration
@ComponentScan("com.y3tu.tools.lowcode")
@EnableConfigurationProperties(RemoteProperties.class)
public class LowCodeConfigure {

    @Autowired
    RemoteProperties remoteProperties;

    /**
     * 开启WebSocket支持
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    @ConditionalOnMissingBean(RemoteFileHelper.class)
    RemoteFileHelper remoteFileHelper() {
        if (remoteProperties.getRemoteMode() != RemoteMode.CUSTOMIZE) {
            if (remoteProperties.getHost() == null || remoteProperties.getPort() == 0 || remoteProperties.getUsername() == null || remoteProperties.getPassword() == null) {
                throw new LowCodeException("请检查远程存储服务配置!");
            }
        }
        if (remoteProperties.getRemoteMode() == RemoteMode.SFTP) {
            //创建SFTP连接池和sftpHelper
            SftpPoolFactory sftpPoolFactory = new SftpPoolFactory(remoteProperties.getHost(),
                    remoteProperties.getPort(), remoteProperties.getUsername(), remoteProperties.getPassword(), null);
            BoundedBlockingPool boundedBlockingPool = PoolFactoryUtil.newBoundedBlockingPool(20, 50, 2, DateUnit.MINUTE, sftpPoolFactory);
            return new SftpHelper(boundedBlockingPool);
        } else if (remoteProperties.getRemoteMode() == RemoteMode.FTP) {
            //创建FTP连接池和ftpHelper
            FtpPoolFactory ftpPoolFactory = new FtpPoolFactory(remoteProperties.getHost(),
                    remoteProperties.getPort(), remoteProperties.getUsername(), remoteProperties.getPassword(), null);
            BoundedBlockingPool boundedBlockingPool = PoolFactoryUtil.newBoundedBlockingPool(20, 50, 2, DateUnit.MINUTE, ftpPoolFactory);
            return new FtpHelper(boundedBlockingPool);
        } else {
            //自定义模式 如果是自定义模式，用户需要实现RemoteFileHelper接口,并注入到spring容器中
            throw new LowCodeException("自定义远程存取服务需要实现RemoteFileHelper接口，并注入到spring容器中");
        }

    }

}
