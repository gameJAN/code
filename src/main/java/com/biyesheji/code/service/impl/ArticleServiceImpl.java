package com.biyesheji.code.service.impl;

import com.biyesheji.code.entity.Article;
import com.biyesheji.code.repository.ArticleRepository;
import com.biyesheji.code.service.ArticleService;
import com.biyesheji.code.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
/*资源Service实现类*/
@Service("articleService")
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;


    private RedisSerializer redisSerializer = new StringRedisSerializer();

    @Override
    public List<Article> list(Article s_article, String nickname, String s_bpublishDate, String s_epublishDate, Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Page<Article> pageArticle = articleRepository.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                return getPredicate(root, cb, s_bpublishDate, s_epublishDate, nickname, s_article);
            }
        }, PageRequest.of(page-1,pageSize,direction,properties));
        return pageArticle.getContent();
    }

    @Override
    public Long geCount(Article s_article, String nickname, String s_bpublishDate, String s_epublishDate) {
        Long cout = articleRepository.count(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                return getPredicate(root, cb, s_bpublishDate, s_epublishDate, nickname, s_article);
            }
        });
        return null;
    }
    /*查询条件*/
    private Predicate getPredicate(Root<Article> root, CriteriaBuilder cb, String s_bpublishDate, String s_epublishDate, String nickname, Article s_article) {
        Predicate predicate = cb.conjunction();
        if (StringUtil.isNotEmpty(s_bpublishDate)) {
            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("publishDate").as(String.class), s_bpublishDate));

        }
        if (StringUtil.isNotEmpty(s_epublishDate)) {
            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("publishDate").as(String.class), s_epublishDate));

        }
        if (StringUtil.isNotEmpty(nickname)) {   //昵称
            predicate.getExpressions().add(cb.like(root.get("user").get("nickname"), "%" + nickname + "%"));

        }
        if (s_article == null) {
            if (StringUtil.isNotEmpty(s_article.getName())) {    //文章标题
                predicate.getExpressions().add(cb.like(root.get("user").get("name"), "%" + s_article.getName() + "%"));

            }
            if (s_article.isHot()) {     //是否热门
                predicate.getExpressions().add(cb.equal(root.get("isHot"), 1));
            }
            if (s_article.getArcType() != null && s_article.getArcType().getArtTypeId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("artType").get("artTypeId"), s_article.getArcType().getArtTypeId()));

            }
            if (s_article.getUser() != null && s_article.getUser().getUserId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("user").get("userId"), s_article.getUser().getUserId()));
            }
            if (s_article.getState() != null) {
                predicate.getExpressions().add(cb.equal(root.get("state"), s_article.getState()));
            }
            if (s_article.isUseful()) {
                predicate.getExpressions().add(cb.equal(root.get("isUseful"), false));
            }

        }
        return predicate;
    }

    @Override
    public void save(Article article) {
        if (article.getState()==2){ //把审核通过的资源放到redis
            redisTemplate.setKeySerializer(redisSerializer);
            redisTemplate.opsForValue().set("article_"+article.getArticleId(),article);
        }
        articleRepository.save(article);

    }

    @Override
    public void delete(Integer id) {
        articleRepository.deleteById(id);
        redisTemplate.delete("article"+id);
    }

    @Override
    public Article getById(Integer id) {
        if (redisTemplate.hasKey("article"+id)){
            return (Article)redisTemplate.opsForValue().get("article"+id);

        }else{
            Article article = articleRepository.getOne(id);
            if (article.getState()==2) {
                redisTemplate.setKeySerializer(redisSerializer);
                redisTemplate.opsForValue().set("article_" + article.getArticleId(), article);
            }
            return article;
        }

    }
}
