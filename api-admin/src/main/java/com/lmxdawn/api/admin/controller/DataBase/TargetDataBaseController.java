package com.lmxdawn.api.admin.controller.DataBase;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.entity.ad.Ad;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.res.PageSimpleResponse;
import com.lmxdawn.api.admin.res.ad.AdResponse;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/targetBase")
public class TargetDataBaseController {

    @Resource
    private TargetDataBaseService targetDataBaseService;

    @AuthRuleAnnotation("/admin/targetBase/list")
    @GetMapping("/list")
    public BaseResponse listPage(TargetDataBaseQueryRequest targetDataBaseQueryRequest)
    {
        List<TargetDataBase> targetDataBaseList = targetDataBaseService.listPage(targetDataBaseQueryRequest);
        PageInfo<TargetDataBase> pageInfo = new PageInfo<>(targetDataBaseList);
        PageSimpleResponse<TargetDataBase> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(targetDataBaseList);
        return ResultVOUtils.success(pageSimpleResponse);
    }
}
