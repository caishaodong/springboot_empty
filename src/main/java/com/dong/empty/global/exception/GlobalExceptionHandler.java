package com.dong.empty.global.exception;

import com.dong.empty.global.ResponseResult;
import com.dong.empty.global.enums.BusinessEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * @Author caishaodong
 * @Date 2020-08-06 14:39
 * @Description
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(BusinessException.class)
    public ResponseResult businessException(BusinessException e) {
        return ResponseResult.error(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult methodArgumentNotValidException(MethodArgumentNotValidException e, BindingResult bindingResult) {
        if (Objects.nonNull(bindingResult) && bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {
                System.out.println(objectError.getDefaultMessage());
            });
        }
        return ResponseResult.error(500, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult exception(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseResult.error(BusinessEnum.FAIL.getCode(), e.getMessage());
    }
}
