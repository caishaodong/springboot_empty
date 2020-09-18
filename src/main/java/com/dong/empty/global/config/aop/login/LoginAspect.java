package com.dong.empty.global.config.aop.login;

import com.dong.empty.global.ResponseResult;
import com.dong.empty.global.enums.BusinessEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author caishaodong
 * @Date 2020-09-18 15:10
 * @Description 登录校验切面
 **/
@Component
@Aspect
public class LoginAspect {

    /**
     * 免登URI集合
     */
    private static final List<String> FREE_LOGIN_URI_LIST = new ArrayList<>();

    @Pointcut("execution(public * com.dong.empty.controller.*.*(..))")
    public void login() {
    }

    @Around("login()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 获取session中的用户信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (!FREE_LOGIN_URI_LIST.contains(requestURI) && Objects.isNull(userId)) {
            return ResponseResult.error(BusinessEnum.NOT_LOGIN);
        }
        return proceedingJoinPoint.proceed();
    }

    static {
        FREE_LOGIN_URI_LIST.add("/login");
    }
}
