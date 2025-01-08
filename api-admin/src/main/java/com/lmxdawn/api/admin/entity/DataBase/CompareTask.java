package com.lmxdawn.api.admin.entity.DataBase;

import lombok.Data;

@Data
public class CompareTask {
    private Integer id;
    private Integer sourceId;
    private String sourceDatabaseName;
    private String targetDatabaseName;
    private Integer targetId;
    private String taskName;
    private String taskStatusDesc;
    private Integer taskStatus;
    private String createTime;
    private String startTime;
    private String endTime;
    private Integer delFlag;
}
