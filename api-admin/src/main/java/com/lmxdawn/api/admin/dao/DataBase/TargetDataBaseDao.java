package com.lmxdawn.api.admin.dao.DataBase;

import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.entity.ad.Ad;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.ad.AdQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TargetDataBaseDao {

    List<TargetDataBase> listPage(TargetDataBaseQueryRequest target);
}
