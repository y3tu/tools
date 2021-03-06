package com.y3tu.tools.web.cache.aspect;

import com.y3tu.tools.kit.base.ObjectUtil;
import com.y3tu.tools.web.cache.CacheUtil;
import com.y3tu.tools.web.cache.annotation.CacheEvict;
import com.y3tu.tools.web.cache.annotation.CachePut;
import com.y3tu.tools.web.cache.annotation.Cacheable;
import com.y3tu.tools.web.cache.annotation.LocalCache;
import com.y3tu.tools.web.cache.annotation.RedisCache;
import com.y3tu.tools.web.cache.core.Cache;
import com.y3tu.tools.web.cache.manager.CacheManager;
import com.y3tu.tools.web.cache.setting.LayerCacheSetting;
import com.y3tu.tools.web.cache.setting.LocalCacheSetting;
import com.y3tu.tools.web.cache.setting.RedisCacheSetting;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * 缓存切面拦截，用于注册方法信息
 *
 * @author y3tu
 */
@Aspect
@Slf4j
public class CacheAspect {

    private static final String CACHE_KEY_ERROR_MESSAGE = "缓存Key %s 不能为NULL";
    private static final String CACHE_NAME_ERROR_MESSAGE = "缓存名称不能为NULL";

    /**
     * 缓存管理
     */
    @Autowired
    private CacheManager cacheManager;

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();


    @Pointcut("@annotation(com.y3tu.tools.web.cache.annotation.Cacheable)")
    public void cacheablePointcut() {
    }

    @Pointcut("@annotation(com.y3tu.tools.web.cache.annotation.CacheEvict)")
    public void cacheEvictPointcut() {
    }

    @Pointcut("@annotation(com.y3tu.tools.web.cache.annotation.CachePut)")
    public void cachePutPointcut() {
    }

    @Around("cacheablePointcut()")
    public Object cacheablePointcut(ProceedingJoinPoint joinPoint) {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);
        // 获取method
        Method method = this.getSpecificMethod(joinPoint);
        // 获取注解
        Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);

        try {
            // 执行查询缓存方法
            return executeCacheable(aopAllianceInvoker, cacheable, method, joinPoint.getArgs());
        } catch (SerializationException e) {
            // 如果是序列化异常需要先删除原有缓存
            String cacheName = cacheable.cacheName();
            // 删除缓存
            delete(cacheName, cacheable.key(), method, joinPoint.getArgs());

            // 忽略操作缓存过程中遇到的异常
            if (cacheable.ignoreException()) {
                log.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheable.ignoreException()) {
                log.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }

    @Around("cacheEvictPointcut()")
    public Object cacheEvictPointcut(ProceedingJoinPoint joinPoint) {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificMethod(joinPoint);
        // 获取注解
        CacheEvict cacheEvict = AnnotationUtils.findAnnotation(method, CacheEvict.class);

        try {
            // 执行查询缓存方法
            return executeEvict(aopAllianceInvoker, cacheEvict, method, joinPoint.getArgs());
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheEvict.ignoreException()) {
                log.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }

    @Around("cachePutPointcut()")
    public Object cachePutPointcut(ProceedingJoinPoint joinPoint) {
        CacheOperationInvoker aopAllianceInvoker = getCacheOperationInvoker(joinPoint);

        // 获取method
        Method method = this.getSpecificMethod(joinPoint);
        // 获取注解
        CachePut cacheEvict = AnnotationUtils.findAnnotation(method, CachePut.class);

        try {
            // 执行查询缓存方法
            return executePut(aopAllianceInvoker, cacheEvict, method, joinPoint.getArgs(), joinPoint.getTarget());
        } catch (Exception e) {
            // 忽略操作缓存过程中遇到的异常
            if (cacheEvict.ignoreException()) {
                log.warn(e.getMessage(), e);
                return aopAllianceInvoker.invoke();
            }
            throw e;
        }
    }

    /**
     * 执行Cacheable切面
     *
     * @param invoker   缓存注解的回调方法
     * @param cacheable {@link Cacheable}
     * @param method    {@link Method}
     * @param args      注解方法参数
     * @return {@link Object}
     */
    private Object executeCacheable(CacheOperationInvoker invoker, Cacheable cacheable, Method method, Object[] args) {
        // 解析SpEL表达式获取cacheName和key
        String cacheName = cacheable.cacheName();
        Assert.notNull(cacheName, CACHE_NAME_ERROR_MESSAGE);
        Object key = generateKeyBySpEL(cacheable.key(), method, args);
        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE, cacheable.key()));

        // 从注解中获取缓存配置
        LocalCache localCache = cacheable.localCache();
        RedisCache redisCache = cacheable.redisCache();
        String depict = cacheable.depict();
        // 通过cacheName和缓存配置获取Cache
        Cache cache = cacheManager.getCache(cacheName, CacheUtil.buildCacheSetting(localCache, redisCache, depict));

        // 通Cache获取值
        return cache.get(key, () -> invoker.invoke());
    }

    /**
     * 执行 CachePut 切面
     *
     * @param invoker  缓存注解的回调方法
     * @param cachePut {@link CachePut}
     * @param method   {@link Method}
     * @param args     注解方法参数
     * @param target   target
     * @return {@link Object}
     */
    private Object executePut(CacheOperationInvoker invoker, CachePut cachePut, Method method, Object[] args, Object target) {
        String cacheName = cachePut.cacheName();
        Assert.notNull(cacheName, CACHE_NAME_ERROR_MESSAGE);
        // 解析SpEL表达式获取 key
        Object key = generateKeyBySpEL(cachePut.key(), method, args);
        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE, cachePut.key()));
        // 从解决中获取缓存配置
        LocalCache localCache = cachePut.localCache();
        RedisCache redisCache = cachePut.redisCache();
        String description = cachePut.description();
        // 指定调用方法获取缓存值
        Object result = invoker.invoke();
        // 通过cacheName和缓存配置获取Cache
        Cache cache = cacheManager.getCache(cacheName, CacheUtil.buildCacheSetting(localCache, redisCache, description));
        cache.put(key, result);
        return result;
    }

    /**
     * 执行 CacheEvict 切面
     *
     * @param invoker    缓存注解的回调方法
     * @param cacheEvict {@link CacheEvict}
     * @param method     {@link Method}
     * @param args       注解方法参数
     * @return {@link Object}
     */
    private Object executeEvict(CacheOperationInvoker invoker, CacheEvict cacheEvict, Method method, Object[] args) {
        // 解析SpEL表达式获取cacheName和key
        String cacheName = cacheEvict.cacheName();
        Assert.notNull(cacheName, CACHE_NAME_ERROR_MESSAGE);
        // 判断是否删除所有缓存数据
        if (cacheEvict.allEntries()) {
            // 删除所有缓存数据（清空）
            Cache cache = cacheManager.getCache(cacheName);
            if (ObjectUtil.isNull(cache)) {
                // 如果没有找到Cache就新建一个默认的
                cache = cacheManager.getCache(cacheName, new LayerCacheSetting(true, new LocalCacheSetting(), new RedisCacheSetting(),"默认缓存配置（删除时生成）"));
                cache.clear();
            } else {
                cache.clear();
            }
        } else {
            // 删除指定key
            delete(cacheName, cacheEvict.key(), method, args);
        }

        // 执行方法
        return invoker.invoke();
    }

    /**
     * 删除执行缓存名称上的指定key
     *
     * @param cacheName 缓存名称
     * @param keySpEL   key的SpEL表达式
     * @param method    {@link Method}
     * @param args      参数列表
     */
    private void delete(String cacheName, String keySpEL, Method method, Object[] args) {
        Object key = generateKeyBySpEL(keySpEL, method, args);
        Assert.notNull(key, String.format(CACHE_KEY_ERROR_MESSAGE, keySpEL));
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            // 如果没有找到Cache就新建一个默认的
            cache = cacheManager.getCache(cacheName, new LayerCacheSetting(true, new LocalCacheSetting(), new RedisCacheSetting(),"默认缓存配置（删除时生成）"));
            cache.evict(key);
        } else {
            cache.evict(key);
        }
    }


    private CacheOperationInvoker getCacheOperationInvoker(ProceedingJoinPoint joinPoint) {
        return () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapperException(ex);
            }
        };
    }

    /**
     * 解析SpEl表达式
     *
     * @param spELString spEL表达式
     * @param method     被注解的方法
     * @param args       被注解方法的形参
     * @return key值
     */
    public Object generateKeyBySpEL(String spELString, Method method, Object[] args) {

        // 使用spring的DefaultParameterNameDiscoverer获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        // 解析过后的Spring表达式对象
        Expression expression = parser.parseExpression(spELString);
        // spring的表达式上下文对象
        EvaluationContext context = new StandardEvaluationContext();
        // 给上下文赋值
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        // 表达式从上下文中计算出实际参数值
        return expression.getValue(context);
    }

    /**
     * 获取Method
     *
     * @param pjp ProceedingJoinPoint
     * @return {@link Method}
     */
    private Method getSpecificMethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(pjp.getTarget());
        if (pjp.getTarget() != null) {
            targetClass = pjp.getTarget().getClass();
        }
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        return specificMethod;
    }
}