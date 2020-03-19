package com.biyesheji.code.controller.admin;

import com.biyesheji.code.entity.ArcType;
import com.biyesheji.code.service.ArcTypeService;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/*管理员资源类型控制器*/
@RestController
@RequestMapping("/admin/arcType")
public class ArcTypeAdminController {

    @Autowired
    private ArcTypeService arcTypeService;

    /*带条件的分页查询，查询资源类型列表*/
    @RequestMapping("/list")
    public Map<String,Object> list(@RequestParam(value = "page",required = false) Integer page, @RequestParam(value = "pageSize",required = false)Integer pageSize){
        Map<String,Object> resultMap = new HashMap<>();
        int count = arcTypeService.getCount().intValue();
        if (page == null && pageSize == null){
            page = 1;
            pageSize = count>0?count:1;
        }
        resultMap.put("data",arcTypeService.list(page,pageSize, Sort.Direction.ASC,"sort"));
        resultMap.put("total",count);
        resultMap.put("errorNo",0);
        return resultMap;
    }

    /*根据主键id查询资源类型实体*/
    @RequestMapping("/findById")
    @RequiresPermissions(value = "根据id查询资源类型实体")
    public Map<String,Object> findById(Integer arcTypeId){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("data",arcTypeService.getById(arcTypeId));
        resultMap.put("errorNo",0);
        return resultMap;

    }
    /*添加或修改资源类型信息*/
    @RequestMapping("/save")
    @RequiresPermissions(value = "添加或修改资源类型信息")
    public Map<String,Object> save(ArcType arcType){
        Map<String,Object> resultMap = new HashMap<>();
        arcTypeService.save(arcType);
        resultMap.put("errorNo",0);
        return resultMap;
    }
}
