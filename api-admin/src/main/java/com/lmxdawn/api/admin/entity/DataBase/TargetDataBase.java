package com.lmxdawn.api.admin.entity.DataBase;

import lombok.Data;

import java.util.Date;

@Data
public class TargetDataBase {
    private Integer id;
    //数据库连接名称
    private String connectName;
    private String dataBaseType;
    private String dataBaseVersion;
    private String serverUrl;
    private String serverPort;
    private String username;
    private String password;
    private String dataBaseName;
    private String dataBaseDriver;
    private String dataBaseUrl;
    private String dataBaseArgument;
    private Date createTime;
    private Integer delFlag;
}
