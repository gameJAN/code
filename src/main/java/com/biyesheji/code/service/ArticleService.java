package com.biyesheji.code.service;

import com.biyesheji.code.entity.Article;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

public interface ArticleService {
    /*根据分页条件查询资源信息列表
    *s_article 条件
    * nickname用户列表
    * s_bpublishDate发布开始时间
    * s_epublishDate发布结束时间
    * page当前页
    * pageSize每页记录数
    * direction排序规则
    * properties排序字段 */

    public List<Article> list(Article s_article, String nickname, String s_bpublishDate, String s_epublishDate, Integer page, Integer pageSize, Direction direction,String... properties );

    /*根据条件获取总记录数
    * *s_article 条件
     * nickname用户列表
     * s_bpublishDate发布开始时间
     * s_epublishDate发布结束时间*/
    public Long geCount(Article s_article, String nickname, String s_bpublishDate, String s_epublishDate);

    /*增加或者修改资源
    * article 资源实体*/
    public void save(Article article);

    /*资源id*/
    public void delete(Integer id);

    /*根据主键获取资源信息*/
    public Article getById(Integer id);
}
