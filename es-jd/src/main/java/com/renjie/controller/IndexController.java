package com.renjie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Fan
 * @Date 2020/11/5
 * @Description:
 */
@Controller
public class IndexController {

    @RequestMapping({"/","/index"})
    public String index(){
        return "index";
    }

}
