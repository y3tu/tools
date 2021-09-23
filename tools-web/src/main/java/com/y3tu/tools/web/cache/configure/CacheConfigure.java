package com.y3tu.tools.web.cache.configure;

import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.text.StrUtil;
import com.y3tu.tools.web.annotation.EnableCache;
import com.y3tu.tools.web.cache.aspect.CacheAspect;
import com.y3tu.tools.web.cache.manager.CacheManager;
import com.y3tu.tools.web.cache.manager.LayerCacheManager;
import com.y3tu.tools.web.cache.manager.LocalCacheManager;
import com.y3tu.tools.web.cache.manager.RedisCacheManager;
import com.y3tu.tools.web.cache.setting.CacheMode;
import com.y3tu.tools.web.cache.staticdata.StaticDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 缓存配置
 *
 * @author y3tu
 */
@Configuration
@EnableAspectJAutoProxy
@Slf4j
@ComponentScan("com.y3tu.tools.web.cache.rest")
public class CacheConfigure implements ImportAware, ApplicationRunner {

    /**
     * 缓存模式
     */
    CacheMode cacheMode;
    /**
     * 是否开启统计
     */
    boolean stats;
    /**
     * 是否允许缓存为空值
     */
    boolean allowNullValue;
    /**
     * redisTemplate的bean名称
     */
    String redisTemplateBeanName;
    /**
     * 是否开启静态数据处理
     */
    boolean staticData;
    /**
     * 静态数据需要扫描的类包
     */
    String[] staticDataPackage;
    /**
     * 提供给外部方法的api
     */
    String api;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private ConfigurableEnvironment environment;


    @Bean
    public CacheManager cacheManager() {
        CacheManager cacheManager;
        try {
            if (cacheMode == CacheMode.ONLY_LOCAL) {
                //只开启本地缓存
                cacheManager = new LocalCacheManager(stats, allowNullValue);
            } else if (cacheMode == CacheMode.ONLY_REDIS) {
                //只开启Redis缓存
                RedisTemplate<String, Object> redisTemplate = null;
                if (StrUtil.isEmpty(redisTemplateBeanName)) {
                    redisTemplate = applicationContext.getBean(redisTemplateBeanName, RedisTemplate.class);
                } else {
                    redisTemplate = applicationContext.getBean(RedisTemplate.class);
                }
                cacheManager = new RedisCacheManager(stats, allowNullValue, redisTemplate);
            } else {
                //开启多级缓存
                RedisTemplate<String, Object> redisTemplate = null;
                if (StrUtil.isEmpty(redisTemplateBeanName)) {
                    redisTemplate = applicationContext.getBean(redisTemplateBeanName, RedisTemplate.class);
                } else {
                    redisTemplate = applicationContext.getBean(RedisTemplate.class);
                }
                cacheManager = new LayerCacheManager(stats, allowNullValue, redisTemplate);
            }
        } catch (NoClassDefFoundError e) {
            throw new ToolException("缓存需要添加Redis依赖！");
        }

        return cacheManager;
    }

    /**
     * 开启缓存切面操作
     */
    @Bean
    public CacheAspect layeringAspect() {
        return new CacheAspect();
    }

    @Bean
    public StaticDataService staticDataService() {
        if (staticData) {
            return new StaticDataService(staticDataPackage);
        } else {
            return null;
        }
    }

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        //获取注解的值
        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableCache.class.getName());
        cacheMode = (CacheMode) annotationAttributes.get("cacheMode");
        stats = (boolean) annotationAttributes.get("stats");
        allowNullValue = (boolean) annotationAttributes.get("allowNullValue");
        redisTemplateBeanName = (String) annotationAttributes.get("redisTemplateBeanName");
        staticData = (boolean) annotationAttributes.get("staticData");
        staticDataPackage = (String[]) annotationAttributes.get("staticDataPackage");
        api = (String) annotationAttributes.get("api");

        // 获取系统属性集合
        MutablePropertySources propertySources = environment.getPropertySources();
        // 将转换后的列表加入属性中
        Properties properties = new Properties();
        properties.put("tools-cache-api",api);

        // 将属性转换为属性集合，并指定名称
        PropertiesPropertySource constants = new PropertiesPropertySource("system-config", properties);
        propertySources.addLast(constants);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (context.isActive()) {
            if(staticData){
                //加载配置isStartUp=true的静态数据
                staticDataService().dataCollect(staticDataService().readHandler().stream().filter(staticDataConfig -> staticDataConfig.isStartUp()).collect(Collectors.toList()));
                log.info("  _   _   _   _   _   _   _   _");
                log.info(" / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\");
                log.info("( c | o | m | p | l | e | t | e )");
                log.info(" \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/");
                log.info("静态数据加载完毕，时间：{}", LocalDateTime.now());
            }
        }
    }
}
