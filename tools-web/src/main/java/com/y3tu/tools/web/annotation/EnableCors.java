package com.y3tu.tools.web.annotation;

import com.y3tu.tools.web.configure.CorsConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 允许跨域资源共享
 *
 * @author y3tu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(CorsConfigure.class)
public @interface EnableCors {

}
