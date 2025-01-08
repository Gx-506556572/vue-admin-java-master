package com.lmxdawn.api.admin.dao.DataBase;

import com.lmxdawn.api.admin.entity.DataBase.SourceDataBase;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.SourceDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SourceDataBaseDao {

    List<SourceDataBase> listPage(SourceDataBaseQueryRequest target);

    boolean insertTargetDataBase(SourceDataBase dataBase);

    boolean updateTargetDataBase(SourceDataBase dataBase);

    boolean deleteById(Integer id);
}
