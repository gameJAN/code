package com.biyesheji.code.entity;
/*资源实体*/

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="article")
@JsonIgnoreProperties(value ={"hibernateLazyInitializer","hander","fieldHandler"})
public class Article implements Serializable {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer arcTypeId;


    @NotEmpty(message = "不能为空")
    @Column(length = 200)
    private String name; //资源名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate; // 发布时间

    @Transient
    private String publishDateStr; //发布时间字符串

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;          //所属用户

    @ManyToOne
    @JoinColumn(name = "arcTypeId")
    private ArcType arcType; //所属资源类型


    private boolean isFree; //是否免费资源

    private Integer points;     //积分

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;     //资源内容

    @Transient
    private String getContentNoTag;  //资源内容 网页标签 lucene

    @Column(length = 200)
    private String download;    //下载地址

    @Column(length = 10)
    private String password; //密码

    private boolean isHot;  //是否热门




}
