package com.lmxdawn.api.admin.entity.DataBase;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskDeatil {
    private Integer id;
    private String taskId;
    private String taskDetail;
    private String taskType;
    private String createTime;

}
