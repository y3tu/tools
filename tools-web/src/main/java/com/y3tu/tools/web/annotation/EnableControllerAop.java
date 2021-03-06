package com.y3tu.tools.web.annotation;

import com.y3tu.tools.web.aop.ControllerAop;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否开启{@link ControllerAop}功能
 *
 * @author y3tu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ControllerAop.class)
public @interface EnableControllerAop {
}
