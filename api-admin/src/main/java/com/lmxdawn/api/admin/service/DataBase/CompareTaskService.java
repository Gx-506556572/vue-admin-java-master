package com.lmxdawn.api.admin.service.DataBase;

import com.lmxdawn.api.admin.entity.DataBase.CompareTask;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.entity.DataBase.TaskDeatil;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TaskDetailQuery;
import com.lmxdawn.api.admin.req.DataBase.TaskQueryRequest;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface CompareTaskService {
    List<CompareTask> listPage(TaskQueryRequest taskQueryRequest);

    boolean deleteById(Integer id);

    boolean insertTask(CompareTask task);

    boolean updateTask(CompareTask task);

    List<HashMap<Integer, String>> sourceList();

    List<HashMap<Integer, String>> targetList();

    String startTask(CompareTask task);

    String updateTaskStatus(CompareTask task);

    List<TaskDeatil> viewResult(TaskDetailQuery task);
}
