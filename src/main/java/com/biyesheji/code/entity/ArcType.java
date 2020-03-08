package com.biyesheji.code.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

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


}
