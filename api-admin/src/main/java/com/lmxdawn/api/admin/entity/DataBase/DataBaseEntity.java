package com.lmxdawn.api.admin.entity.DataBase;

import lombok.Data;

@Data
public class DataBaseEntity {
    private Integer id;
    //数据库连接名称
    private String connectName;
    private String databaseType;
    private String databaseVersion;
    private String serverUrl;
    private String serverPort;
    private String username;
    private String password;
    private String databaseName;
    private String databaseDriver;
    private String databaseUrl;
    private String databaseArgument;
    private String createTime;
    private Integer delFlag;
}
