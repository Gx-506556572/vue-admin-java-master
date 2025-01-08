package com.lmxdawn.api.admin.service.DataBase;

import com.lmxdawn.api.admin.entity.DataBase.SourceDataBase;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.SourceDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;

import java.util.List;

public interface SourceDataBaseService {
    List<SourceDataBase> listPage(SourceDataBaseQueryRequest request);

    boolean insertTargetDataBase(SourceDataBase dataBase);

    boolean updateTargetDataBase(SourceDataBase dataBase);

    boolean deleteById(Integer id);
}
