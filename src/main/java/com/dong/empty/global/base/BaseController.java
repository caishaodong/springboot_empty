package com.dong.empty.global.base;


import com.dong.empty.global.ResponseResult;
import com.dong.empty.global.enums.BusinessEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author caishaodong
 * @Date 2020-09-07 12:21
 * @Description 基础类
 **/
public class BaseController {
    public static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    public ResponseResult success() {
        return ResponseResult.success();
    }

    public <T> ResponseResult<T> success(T t) {
        return ResponseResult.success(t);
    }

    public ResponseResult error() {
        return ResponseResult.error();
    }

    public ResponseResult error(BusinessEnum businessEnum) {
        return ResponseResult.error(businessEnum);
    }
}
