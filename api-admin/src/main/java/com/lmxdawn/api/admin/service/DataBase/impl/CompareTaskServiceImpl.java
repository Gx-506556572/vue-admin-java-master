package com.lmxdawn.api.admin.service.DataBase.impl;

import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.DataBase.CompareTaskDao;
import com.lmxdawn.api.admin.dao.DataBase.TargetDataBaseDao;
import com.lmxdawn.api.admin.entity.DataBase.CompareTask;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TaskQueryRequest;
import com.lmxdawn.api.admin.service.DataBase.CompareTaskService;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class CompareTaskServiceImpl implements CompareTaskService {

    @Resource
    private CompareTaskDao taskDao;
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
}
