package com.lmxdawn.api.admin.service.DataBase.impl;

import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.DataBase.TargetDataBaseDao;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import com.lmxdawn.api.common.res.BaseResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class TargetDataBaseServiceImpl implements TargetDataBaseService {

    @Resource
    private TargetDataBaseDao targetDao;
    @Override
    public List<TargetDataBase> listPage(TargetDataBaseQueryRequest target) {
        if (target == null) {
            return Collections.emptyList();
        }
        int offset = (target.getPage() - 1) * target.getLimit();
        PageHelper.offsetPage(offset, target.getLimit());
        List<TargetDataBase> targetDataBaseList = targetDao.listPage(target);
        return targetDataBaseList;
    }
}
