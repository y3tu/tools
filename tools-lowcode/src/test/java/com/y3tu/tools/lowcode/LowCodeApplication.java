package com.y3tu.tools.lowcode;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.y3tu.tools.web.annotation.EnableCors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动
 *
 * @author y3tu
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableCors
public class LowCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LowCodeApplication.class, args);
    }
}
