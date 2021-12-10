package com.y3tu.tools.lowcode.exception;

import com.y3tu.tools.kit.exception.ErrorEnum;
import com.y3tu.tools.kit.exception.ExceptionUtil;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

/**
 * Controller统一异常advice
 * 在Application启动类上使用@EnableDefaultExceptionAAdivce注解启用统一异常配置
 *
 * @author y3tu
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class DefaultExceptionAdvice {

    /**
     * 处理sql异常
     *
     * @return ResponseEntity
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({SQLException.class})
    public R handleSQLException(SQLException e) {
        log.error("服务运行SQLException异常", e);
        R r = R.error(ErrorEnum.SQL_ERROR.getCode(), e.getMessage());
        return r;
    }

    /**
     * 处理抛出的Throwable
     *
     * @return ResponseEntity
     */
    @ExceptionHandler(value = {Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handle(Throwable throwable) {
        RuntimeException runtimeException = ExceptionUtil.wrapRuntime(throwable);
        R r = R.error(ErrorEnum.SYSTEM_INTERNAL_ERROR.getCode(), runtimeException.getMessage());
        return r;
    }

    /**
     * 所有异常统一处理
     *
     * @return ResponseEntity
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error("异常", e);
        String code = "";
        String message = "";
        if (e instanceof ToolException) {
            code = ErrorEnum.TOOLS_ERROR.getCode();
            message = e.getMessage();
        } else if (e instanceof LowCodeException) {
            code = "Low-Code-Error";
            message = e.getMessage();
        } else {
            code = ErrorEnum.SYSTEM_INTERNAL_ERROR.getCode();
            message = e.getMessage();
        }
        R r = R.error(code, message);
        return r;
    }

}
