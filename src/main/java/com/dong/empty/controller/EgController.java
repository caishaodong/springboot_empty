package com.dong.empty.controller;

import com.dong.empty.global.ResponseResult;
import com.dong.empty.global.base.BaseController;
import com.dong.empty.global.config.aop.permission.Permission;
import com.dong.empty.global.enums.RoleEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author caishaodong
 * @Date 2020-09-07 13:09
 * @Description
 **/
@RestController
@RequestMapping("/index")
public class EgController extends BaseController {

    @Permission(role = {RoleEnum.SUPER_ADMIN, RoleEnum.ADMIN})
    @GetMapping("/permission")
    public ResponseResult permission() {
        return success();
    }

    @GetMapping("/returnValue")
    public Long returnValue() {
        return 3L;
    }
}
