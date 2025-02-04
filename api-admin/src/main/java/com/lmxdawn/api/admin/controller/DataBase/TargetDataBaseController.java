package com.lmxdawn.api.admin.controller.DataBase;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.converter.AdSaveForm2AdConverter;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.entity.ad.Ad;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.ad.AdSaveRequest;
import com.lmxdawn.api.admin.res.PageSimpleResponse;
import com.lmxdawn.api.admin.res.ad.AdResponse;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import com.lmxdawn.api.common.enums.ResultEnum;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 新增
     *
     * @return
     */
    @AuthRuleAnnotation("/admin/targetBase/save")
    @PostMapping("/save")
    public BaseResponse save(@RequestBody TargetDataBase  dataBase) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        dataBase.setCreateTime(formattedDate);
        boolean b = targetDataBaseService.insertTargetDataBase(dataBase);
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("id", dataBase.getId());
        return ResultVOUtils.success(res);
    }

    @AuthRuleAnnotation("/admin/targetBase/update")
    @PostMapping("/update")
    public BaseResponse update(@RequestBody TargetDataBase  dataBase) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        dataBase.setCreateTime(formattedDate);
        boolean b = targetDataBaseService.updateTargetDataBase(dataBase);
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }
        return ResultVOUtils.success();
    }

    @AuthRuleAnnotation("/admin/targetBase/delete")
    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody TargetDataBase dataBase) {


        boolean b = targetDataBaseService.deleteById(dataBase.getId());
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }
        return ResultVOUtils.success();
    }
}
