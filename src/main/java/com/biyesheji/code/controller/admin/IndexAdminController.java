package com.biyesheji.code.controller.admin;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*管理员-首页或者跳转url控制器*/
@Controller
public class IndexAdminController {

    /*跳转刀管理员主页面*/
    @RequiresPermissions(value = "进入管理员主页")
    @RequestMapping("/toAdminUserCenterPage")
    public String toAdminUserCenterPage(){
        return "/admin/index";
    }
}
