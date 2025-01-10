package com.lmxdawn.api.admin.service.DataBase.impl;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.DataBase.CompareTaskDao;
import com.lmxdawn.api.admin.dao.DataBase.TargetDataBaseDao;
import com.lmxdawn.api.admin.entity.DataBase.CompareTask;
import com.lmxdawn.api.admin.entity.DataBase.DataBaseEntity;
import com.lmxdawn.api.admin.entity.DataBase.SourceDataBase;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TaskQueryRequest;
import com.lmxdawn.api.admin.service.DataBase.CompareTaskService;
import com.lmxdawn.api.admin.service.DataBase.MyDataSourceManagement;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
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
    public String  startTask(CompareTask task) {
        DataBaseEntity sourceDataBase = getSourceDataBase(task.getSourceId());
        DataBaseEntity targetDataBase = getTargetDataBase(task.getTargetId());
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
        try {
            //key为表名，value为表结构
            Map<String, Map<String, String>> sourceStructures = myDataSourceManagement.getTableStructures(sourceBase, tableNames);
            Map<String, Map<String, String>> targetStructures = myDataSourceManagement.getTableStructures(targetBase, tableNames);
            //比对表结构
          String d = myDataSourceManagement.compareTableStructures(sourceStructures, targetStructures);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String s = compareTable(sourceMap, targetMap);
        return s;
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

    public String compareTable(HashMap<String, Integer>sourceMap, HashMap<String, Integer>targetMap){
        StringBuilder stringBuilder = new StringBuilder();
        // 找出仅存在于 源数据库 中的表名
        for (String key : sourceMap.keySet()) {
            if (!targetMap.containsKey(key)) {
                stringBuilder.append("仅存在于 源数据库 中的表: ").append(key).append("\n");
            }
        }

        // 找出仅存在于 目标数据库 中的表名
        for (String key : targetMap.keySet()) {
            if (!sourceMap.containsKey(key)) {
                stringBuilder.append("仅存在于 目标数据库 中的表: ").append(key).append("\n");
            }
        }

        // 找出表名一致但数量不一致的表
        for (String key : sourceMap.keySet()) {
            if (targetMap.containsKey(key) && !sourceMap.get(key).equals(targetMap.get(key))) {
                stringBuilder.append("表名一致但数量不一致的表: ").append(key)
                        .append(" (源数据库: ").append(sourceMap.get(key))
                        .append(", 目标数据库: ").append(targetMap.get(key)).append(")\n");
            }
        }
        return stringBuilder.toString();
    }


}
