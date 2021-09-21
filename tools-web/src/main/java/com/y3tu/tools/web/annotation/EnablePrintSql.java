package com.y3tu.tools.web.annotation;

import com.y3tu.tools.web.interceptor.PrintSqlInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 是否开启{@link PrintSqlInterceptor}功能
 *
 * @author y3tu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(PrintSqlInterceptor.class)
public @interface EnablePrintSql {
}
