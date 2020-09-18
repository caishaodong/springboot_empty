package com.dong.empty.controller;

import com.dong.empty.global.ResponseResult;
import com.dong.empty.global.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author caishaodong
 * @Date 2020-09-18 15:16
 * @Description 登入登出
 **/
@RestController
@RequestMapping("")
public class LoginController extends BaseController {

    @RequestMapping("/login")
    public ResponseResult login(HttpServletRequest request) {
        // 保存用户id至session
        request.getSession().setAttribute("userId", 0);
        return success();
    }

    @RequestMapping("/logout")
    public ResponseResult logout(HttpServletRequest request) {
        request.getSession().removeAttribute("userId");
        return success();
    }
}
