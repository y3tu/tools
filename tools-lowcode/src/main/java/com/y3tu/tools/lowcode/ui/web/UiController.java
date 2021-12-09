package com.y3tu.tools.lowcode.ui.web;

import com.y3tu.tools.kit.lang.R;
import com.y3tu.tools.lowcode.exception.LowCodeException;
import com.y3tu.tools.lowcode.ui.configure.UiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

/**
 * @author y3tu
 */
@RestController
@RequestMapping("tools-lowcode/ui")
@Slf4j
public class UiController {

    @Autowired
    UiProperties uiProperties;

    @PostMapping("/login")
    public R login(@RequestBody Map params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String usernameP = uiProperties.getUsername();
        String passwordP = uiProperties.getPassword();
        if (usernameP.equals(username) && passwordP.equals(password)) {
            //登录成功！
            return R.success(UUID.randomUUID());
        } else {
            throw new LowCodeException("用户名或者密码错误！");
        }
    }

    @GetMapping("/getContextPath")
    public R getContextPath(HttpServletRequest request) {
        return R.success(request.getContextPath());
    }
}
