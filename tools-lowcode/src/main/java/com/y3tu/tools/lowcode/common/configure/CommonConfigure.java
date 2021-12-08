package com.y3tu.tools.lowcode.common.configure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 通用业务配置
 *
 * @author y3tu
 */
@Configuration
@EntityScan(basePackages = {"com.y3tu.tools.lowcode.common"})
@EnableJpaRepositories(basePackages = {"com.y3tu.tools.lowcode.common"})
public class CommonConfigure {
}
