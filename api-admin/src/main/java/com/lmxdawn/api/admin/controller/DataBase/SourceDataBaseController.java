package com.lmxdawn.api.admin.controller.DataBase;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.entity.DataBase.SourceDataBase;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.req.DataBase.SourceDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.res.PageSimpleResponse;
import com.lmxdawn.api.admin.service.DataBase.SourceDataBaseService;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import com.lmxdawn.api.common.enums.ResultEnum;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/sourceBase")
public class SourceDataBaseController {

    @Resource
    private SourceDataBaseService sourceDataBaseService;

    @AuthRuleAnnotation("/admin/sourceBase/list")
    @GetMapping("/list")
    public BaseResponse listPage(SourceDataBaseQueryRequest request)
    {
        List<SourceDataBase> sourceDataBaseList = sourceDataBaseService.listPage(request);
        PageInfo<SourceDataBase> pageInfo = new PageInfo<>(sourceDataBaseList);
        PageSimpleResponse<SourceDataBase> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(sourceDataBaseList);
        return ResultVOUtils.success(pageSimpleResponse);
    }

    /**
     * 新增
     *
     * @return
     */
    @AuthRuleAnnotation("/admin/sourceBase/save")
    @PostMapping("/save")
    public BaseResponse save(@RequestBody SourceDataBase dataBase) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        dataBase.setCreateTime(formattedDate);
        boolean b = sourceDataBaseService.insertTargetDataBase(dataBase);
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("id", dataBase.getId());
        return ResultVOUtils.success(res);
    }

    @AuthRuleAnnotation("/admin/sourceBase/update")
    @PostMapping("/update")
    public BaseResponse update(@RequestBody SourceDataBase  dataBase) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        dataBase.setCreateTime(formattedDate);
        boolean b = sourceDataBaseService.updateTargetDataBase(dataBase);
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }
        return ResultVOUtils.success();
    }

    @AuthRuleAnnotation("/admin/sourceBase/delete")
    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody SourceDataBase dataBase) {


        boolean b = sourceDataBaseService.deleteById(dataBase.getId());
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }
        return ResultVOUtils.success();
    }
}
