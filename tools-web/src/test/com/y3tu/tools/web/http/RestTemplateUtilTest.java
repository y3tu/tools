package com.y3tu.tools.web.http;

import org.junit.Test;
import org.springframework.http.ResponseEntity;


public class RestTemplateUtilTest {

    @Test
    public void getInstance() {
        ResponseEntity<String> responseEntity =  RestTemplateUtil.getInstance().get("https://www.mcmssc.com/44_44965/42472791.html",String.class);
        System.out.println(responseEntity.getBody());
    }
}