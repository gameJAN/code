package com.biyesheji.code.service;

import com.biyesheji.code.entity.ArcType;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

/*资源类型service*/
public interface ArcTypeService {

    /*分页查询资源类型
    * page 当前页
    * pageSize 每页记录数
    * direction 排序规则
    * properties 排序字段*/

    public List<ArcType> list(Integer page, Integer pageSize, Direction direction, String... properties);

    /*分页查询资源类型

     * direction 排序规则
     * properties 排序字段*/

    public List listAll( Direction direction,String... properties);

    /*获取总记录数*/
    public Long getCount();

    /*添加或修改资源类型*/
    public void save(ArcType arcType);

    /*根据ID删除一条资源类型*/
    public void delete(Integer id);

    /*根据id查询一条类型*/
    public ArcType getById(Integer id);
}
