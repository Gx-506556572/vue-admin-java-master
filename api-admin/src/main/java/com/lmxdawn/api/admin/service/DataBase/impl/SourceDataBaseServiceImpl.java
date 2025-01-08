package com.lmxdawn.api.admin.service.DataBase.impl;

import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.DataBase.SourceDataBaseDao;
import com.lmxdawn.api.admin.dao.DataBase.TargetDataBaseDao;
import com.lmxdawn.api.admin.entity.DataBase.SourceDataBase;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.SourceDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.service.DataBase.SourceDataBaseService;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class SourceDataBaseServiceImpl implements SourceDataBaseService {

    @Resource
    private SourceDataBaseDao sourceDao;
    @Override
    public List<SourceDataBase> listPage(SourceDataBaseQueryRequest target) {
        if (target == null) {
            return Collections.emptyList();
        }
        int offset = (target.getPage() - 1) * target.getLimit();
        PageHelper.offsetPage(offset, target.getLimit());
        List<SourceDataBase> sourceDataBaseList = sourceDao.listPage(target);
        return sourceDataBaseList;
    }

    @Override
    public boolean insertTargetDataBase(SourceDataBase dataBase) {
        return sourceDao.insertTargetDataBase(dataBase);
    }

    @Override
    public boolean updateTargetDataBase(SourceDataBase dataBase) {
        return sourceDao.updateTargetDataBase(dataBase);
    }

    @Override
    public boolean deleteById(Integer id) {
        return sourceDao.deleteById(id);
    }
}
