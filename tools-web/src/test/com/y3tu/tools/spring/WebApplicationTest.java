package com.y3tu.tools.spring;

import com.y3tu.tools.web.annotation.EnableCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableCache
public class WebApplicationTest {

    @Test
    public void test(){

    }
}