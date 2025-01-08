package com.lmxdawn.api.admin.dao.DataBase;

import com.lmxdawn.api.admin.entity.DataBase.CompareTask;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TaskQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface CompareTaskDao {

    List<CompareTask> listPage(TaskQueryRequest taskQueryRequest);

    boolean insertTask(CompareTask task);

    boolean updateTask(CompareTask task);

    boolean deleteById(Integer id);

    List<HashMap<Integer, String>> getSourceList();

    List<HashMap<Integer, String>> targetList();
}
