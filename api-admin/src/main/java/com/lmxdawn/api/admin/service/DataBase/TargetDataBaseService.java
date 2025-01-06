package com.lmxdawn.api.admin.service.DataBase;

import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.common.res.BaseResponse;

import java.util.List;

public interface TargetDataBaseService {
    List<TargetDataBase> listPage(TargetDataBaseQueryRequest targetDataBaseQueryRequest);
}
