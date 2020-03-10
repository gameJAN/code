package com.biyesheji.code.repository;

import com.biyesheji.code.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/*资源类型Repository*/
public interface ArticleRepository extends JpaRepository<Article,Integer>, JpaSpecificationExecutor<Article> {

}
