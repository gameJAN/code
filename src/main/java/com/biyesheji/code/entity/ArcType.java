package com.biyesheji.code.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
/*
* 资源类型实体*/
@Entity
@Table(name = "arcType")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer","hander","fieldHandler"})
public class ArcType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer artTypeId;      //资源类型ID

    @NotEmpty(message = "资源类型名称不能为空")
    @Column(length = 200)
    private String arcTypeName;   //资源类型名称

    @Column(length = 1000)
    private String remark;     //描述

    private Integer sort;         //排序

    public Integer getArtTypeId() {
        return artTypeId;
    }

    public void setArtTypeId(Integer artTypeId) {
        this.artTypeId = artTypeId;
    }

    public String getArcTypeName() {
        return arcTypeName;
    }

    public void setArcTypeName(String arcTypeName) {
        this.arcTypeName = arcTypeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
