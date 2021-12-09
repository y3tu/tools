package com.y3tu.tools.lowcode.ui.configure;

import com.y3tu.tools.lowcode.exception.LowCodeException;
import com.y3tu.tools.lowcode.ui.web.UiFilter;
import com.y3tu.tools.lowcode.ui.web.UiViewServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * UI自动配置
 *
 * @author y3tu
 */
@Configuration
@EnableConfigurationProperties(UiProperties.class)
@ConditionalOnWebApplication
public class UiAutoConfigure {

    /**
     * 程序真正对外的URL前缀
     */
    private final String lowCodeUrlPattern = "/tools-lowcode/*";

    /**
     * 用户自定义的URL前缀
     */
    @Value("${tools.lowcode.ui.url-pattern:/tools-lowcode-ui/*}")
    private String uiUrlPattern;


    /**
     * 配置url拦截
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean uiViewServletRegistrationBean(UiProperties properties) {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        //设置需要拦截的url
        registrationBean.setServlet(new UiViewServlet());
        registrationBean.addUrlMappings(properties.getUrlPattern() != null ? properties.getUrlPattern() : "/tool-server-ui/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean cacheFilterRegistration() {

        if (uiUrlPattern.equals(lowCodeUrlPattern)) {
            //如果用户配置的url和服务url相同，会影响url解析，应限制用户配置的url不能和lowCodeUrlPattern相同
            throw new LowCodeException("用户配置的url不能为tools-lowcode,请更换！");
        }

        FilterRegistrationBean registration = new FilterRegistrationBean();
        UiFilter uiFilter = new UiFilter();
        uiFilter.setUiUrlPattern(uiUrlPattern);
        uiFilter.setLowCodeUrlPattern(lowCodeUrlPattern);
        registration.setFilter(uiFilter);
        registration.addUrlPatterns(uiUrlPattern);
        registration.setName("uiFilter");
        registration.setOrder(1);
        return registration;
    }

}
