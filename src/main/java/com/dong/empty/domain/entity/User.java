package com.dong.empty.domain.entity;

import lombok.Data;

/**
 * @Author caishaodong
 * @Date 2020-09-18 16:25
 * @Description
 **/
@Data
public class User {
    private Long id;
    private String userName;
    private String name;
    private String password;
    private String salt;
}
