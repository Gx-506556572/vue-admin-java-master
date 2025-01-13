package com.lmxdawn.api.admin.service.DataBase.impl;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.DataBase.CompareTaskDao;
import com.lmxdawn.api.admin.dao.DataBase.TargetDataBaseDao;
import com.lmxdawn.api.admin.entity.DataBase.*;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TaskDetailQuery;
import com.lmxdawn.api.admin.req.DataBase.TaskQueryRequest;
import com.lmxdawn.api.admin.service.DataBase.CompareTaskService;
import com.lmxdawn.api.admin.service.DataBase.MyDataSourceManagement;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CompareTaskServiceImpl implements CompareTaskService {

    @Resource
    private CompareTaskDao taskDao;

    @Autowired
    private MyDataSourceManagement myDataSourceManagement;
    @Override
    public List<CompareTask> listPage(TaskQueryRequest task) {
        if (task == null) {
            return Collections.emptyList();
        }
        int offset = (task.getPage() - 1) * task.getLimit();
        PageHelper.offsetPage(offset, task.getLimit());
        List<CompareTask> targetDataBaseList = taskDao.listPage(task);
        return targetDataBaseList;
    }



    @Override
    public boolean deleteById(Integer id) {
        return taskDao.deleteById(id);
    }

    @Override
    public boolean insertTask(CompareTask task) {
        return taskDao.insertTask(task);
    }

    @Override
    public boolean updateTask(CompareTask task) {
        return taskDao.updateTask(task);
    }

    @Override
    public List<HashMap<Integer, String>> sourceList() {
        List<HashMap<Integer, String>> list = taskDao.getSourceList();
        return list;
    }

    @Override
    public List<HashMap<Integer, String>> targetList() {
        List<HashMap<Integer, String>> list = taskDao.targetList();
        return list;
    }

    @Override
    public String updateTaskStatus(CompareTask task) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        task.setTaskStatus(1).setTaskStatusDesc("运行中").setStartTime(formattedDate);
        taskDao.updateTask(task);
        // 异步调用 startTask 方法
        CompletableFuture.runAsync(() -> startTask(task));
        return "success";
    }

    @Override
    public List<TaskDeatil> viewResult(TaskDetailQuery task) {
        if (task == null) {
            return Collections.emptyList();
        }
        int offset = (task.getPage() - 1) * task.getLimit();
        PageHelper.offsetPage(offset, task.getLimit());
        ArrayList<TaskDeatil> list = taskDao.viewResult(task);
        return list;
    }


    public String  startTask(CompareTask task) {
        DataBaseEntity sourceDataBase = getSourceDataBase(task.getSourceId());
        DataBaseEntity targetDataBase = getTargetDataBase(task.getTargetId());
        try {
        //源数据库连接
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setDriverClassName(sourceDataBase.getDatabaseDriver());
        dataSourceProperty.setUrl(sourceDataBase.getDatabaseUrl());
        dataSourceProperty.setUsername(sourceDataBase.getUsername());
        dataSourceProperty.setPassword(sourceDataBase.getPassword());
        DataSource sourceBase=myDataSourceManagement.createAndGetDS(dataSourceProperty,sourceDataBase.getDatabaseName());
        //所有源数据库的表数据
        HashMap<String, Integer> sourceMap = myDataSourceManagement.validateDataSource(sourceBase, sourceDataBase);
        //目标数据库连接
        DataSourceProperty targetProperty = new DataSourceProperty();
        targetProperty.setDriverClassName(targetDataBase.getDatabaseDriver());
        targetProperty.setUrl(targetDataBase.getDatabaseUrl());
        targetProperty.setUsername(targetDataBase.getUsername());
        targetProperty.setPassword(targetDataBase.getPassword());
        DataSource targetBase=myDataSourceManagement.createAndGetDS(targetProperty,targetDataBase.getDatabaseName());
        //所有目标数据库的表数据
        HashMap<String, Integer> targetMap = myDataSourceManagement.validateDataSource(targetBase,targetDataBase);
        Set<String> commonTableNames = new HashSet<>(sourceMap.keySet());
        commonTableNames.retainAll(targetMap.keySet());
        ArrayList<String> tableNames = new ArrayList<>(commonTableNames);

            //key为表名，value为表结构
            Map<String, Map<String, String>> sourceStructures = myDataSourceManagement.getTableStructures(sourceBase, tableNames);
            Map<String, Map<String, String>> targetStructures = myDataSourceManagement.getTableStructures(targetBase, tableNames);
            //比对表结构
            myDataSourceManagement.compareTableStructures(sourceStructures, targetStructures,task.getId());
            compareTable(sourceMap, targetMap, task.getId());
            //修改成功状态
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = now.format(formatter);
            task.setTaskStatus(2).setTaskStatusDesc("运行完成").setEndTime(formattedDate);
            taskDao.updateTask(task);
        } catch (Exception e) {
            task.setTaskStatus(3).setTaskStatusDesc("启动失败");
            taskDao.updateTask(task);
            throw new RuntimeException(e);
        }
        return "任务完成";
    }

    //获取源数据库相关数据
    public DataBaseEntity getSourceDataBase(Integer id){
        DataBaseEntity sourceDataBase = taskDao.getSourceDataBase(id);
        String argument = taskDao.getDatabaseArgument(sourceDataBase.getDatabaseType(), sourceDataBase.getDatabaseVersion());
        if (sourceDataBase.getDatabaseType().equals("KINGBASE")){
            argument = argument + sourceDataBase.getDatabaseName();
        }
        sourceDataBase.setDatabaseUrl(sourceDataBase.getDatabaseUrl() + argument);
        return sourceDataBase;
    }
    //获取目标数据库相关数据
    public DataBaseEntity getTargetDataBase(Integer id){
        DataBaseEntity targetDataBase = taskDao.getTargetDataBase(id);
        String argument = taskDao.getDatabaseArgument(targetDataBase.getDatabaseType(), targetDataBase.getDatabaseVersion());
        if (targetDataBase.getDatabaseType().equals("KINGBASE") ){
            argument = argument + targetDataBase.getDatabaseName();
        }
        targetDataBase.setDatabaseUrl(targetDataBase.getDatabaseUrl() + argument);
        return targetDataBase;
    }

    public void compareTable(HashMap<String, Integer>sourceMap, HashMap<String, Integer>targetMap,Integer taskId){
        ArrayList<String> details = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        // 找出仅存在于 源数据库 中的表名
        for (String key : sourceMap.keySet()) {
            if (!targetMap.containsKey(key)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("仅存在于 源数据库 中的表: ").append(key);
                details.add(stringBuilder.toString());
            }
        }
        // 找出仅存在于 目标数据库 中的表名
        for (String key : targetMap.keySet()) {
            if (!sourceMap.containsKey(key)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("仅存在于 目标数据库 中的表: ").append(key);
                details.add(stringBuilder.toString());
            }
        }

        // 找出表名一致但数量不一致的表
        for (String key : sourceMap.keySet()) {
            if (targetMap.containsKey(key) && !sourceMap.get(key).equals(targetMap.get(key))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("表名一致但数量不一致的表: ").append(key)
                        .append(" 源数据库: ").append(sourceMap.get(key))
                        .append(", 目标数据库: ").append(targetMap.get(key));
                System.out.println(stringBuilder.toString());
                details.add(stringBuilder.toString());
            }
        }
        taskDao.insetTaskDetail(taskId, details,formattedDate);
    }


}
