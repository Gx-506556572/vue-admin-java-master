package com.lmxdawn.api.admin.controller.DataBase;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.entity.DataBase.CompareTask;
import com.lmxdawn.api.admin.entity.DataBase.DataBaseEntity;
import com.lmxdawn.api.admin.entity.DataBase.TargetDataBase;
import com.lmxdawn.api.admin.entity.DataBase.TaskDeatil;
import com.lmxdawn.api.admin.req.DataBase.TargetDataBaseQueryRequest;
import com.lmxdawn.api.admin.req.DataBase.TaskDetailQuery;
import com.lmxdawn.api.admin.req.DataBase.TaskQueryRequest;
import com.lmxdawn.api.admin.res.PageSimpleResponse;
import com.lmxdawn.api.admin.service.DataBase.CompareTaskService;
import com.lmxdawn.api.admin.service.DataBase.TargetDataBaseService;
import com.lmxdawn.api.common.enums.ResultEnum;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/compareTask")
public class CompareTaskController {

    @Resource
    private CompareTaskService taskService;

    @AuthRuleAnnotation("/admin/compareTask/list")
    @GetMapping("/list")
    public BaseResponse listPage(TaskQueryRequest taskQueryRequest)
    {
        List<CompareTask> compareTaskList = taskService.listPage(taskQueryRequest);
        PageInfo<CompareTask> pageInfo = new PageInfo<>(compareTaskList);
        PageSimpleResponse<CompareTask> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(compareTaskList);
        return ResultVOUtils.success(pageSimpleResponse);
    }

    /**
     * 新增
     *
     * @return
     */
    @AuthRuleAnnotation("/admin/compareTask/save")
    @PostMapping("/save")
    public BaseResponse save(@RequestBody CompareTask  task) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        task.setCreateTime(formattedDate);
        boolean b = taskService.insertTask(task);
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("id", task.getId());
        return ResultVOUtils.success(res);
    }

    @AuthRuleAnnotation("/admin/compareTask/update")
    @PostMapping("/update")
    public BaseResponse update(@RequestBody CompareTask  task) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        task.setCreateTime(formattedDate);
        boolean b = taskService.updateTask(task);
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }
        return ResultVOUtils.success();
    }

    @AuthRuleAnnotation("/admin/compareTask/delete")
    @PostMapping("/delete")
    public BaseResponse delete(@RequestBody CompareTask  task) {
        boolean b = taskService.deleteById(task.getId());
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }
        return ResultVOUtils.success();
    }

    //获取源数据库列表
    @AuthRuleAnnotation("/admin/compareTask/sourceList")
    @GetMapping("/sourceList")
    public BaseResponse sourceList() {
       List<HashMap<Integer,String>> sourceList = taskService.sourceList();
        return ResultVOUtils.success(sourceList);
    }
    //获取目标数据库列表
    @AuthRuleAnnotation("/admin/compareTask/targetList")
    @GetMapping("/targetList")
    public BaseResponse targetList() {
        List<HashMap<Integer,String>> sourceList = taskService.targetList();
        return ResultVOUtils.success(sourceList);
    }

    //查看比对结果
    @AuthRuleAnnotation("/admin/compareTask/viewResult")
    @PostMapping("/viewResult")
    public BaseResponse viewResult(@RequestBody TaskDetailQuery task) {
         List<TaskDeatil> list  =  taskService.viewResult(task);
        PageInfo<TaskDeatil> pageInfo = new PageInfo<>(list);
        PageSimpleResponse<TaskDeatil> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(list);
        return ResultVOUtils.success(pageSimpleResponse);
    }


    //启动
    @AuthRuleAnnotation("/admin/compareTask/startTask")
    @PostMapping("/startTask")
    public BaseResponse startTask(@RequestBody CompareTask  task) {
        //修改状态
      String s =  taskService.updateTaskStatus(task);
        return ResultVOUtils.success(s);
    }

    //测试连接
    @AuthRuleAnnotation("/admin/compareTask/connectTest")
    @PostMapping("/connectTest")
    public BaseResponse connectTest(@RequestBody DataBaseEntity entity) {
        //修改状态
        String s =  taskService.connectTest(entity);
        return ResultVOUtils.success(s);
    }
}
