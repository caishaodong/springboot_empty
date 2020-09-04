package com.dong.empty.global;

import com.dong.empty.global.enums.BusinessEnum;
import org.springframework.validation.BindingResult;

import java.util.Objects;

/**
 * @Author caishaodong
 * @Date 2020-08-06 14:27
 * @Description
 **/
public class ResponseResult<T> {
    private Integer code;
    private String errorMsg;
    private T data;

    public ResponseResult(Integer code, String errorMsg, T data) {
        this.code = code;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public static ResponseResult success() {
        return new ResponseResult(200, "成功", null);
    }

    public static ResponseResult success(Object data) {
        return new ResponseResult(200, "成功", data);
    }

    public static ResponseResult error() {
        return new ResponseResult(500, "失败", null);
    }

    public static ResponseResult error(Integer code, String errorMsg) {
        return new ResponseResult(code, errorMsg, null);
    }

    public static ResponseResult error(BusinessEnum businessEnum) {
        return new ResponseResult(businessEnum.getCode(), businessEnum.getDesc(), null);
    }

    public static ResponseResult error(BindingResult bindingResult) {
        if (Objects.nonNull(bindingResult) && bindingResult.hasErrors()) {
            return new ResponseResult(BusinessEnum.PARAM_ERROR.getCode(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        } else {
            return error();
        }
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
