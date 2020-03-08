package com.biyesheji.code.shiro.config;

/*
* Shiro配置类
* Filter Chained定义说明
* 1.一个url可以配置多个Filter，使用逗号分隔
* 2.当设置多个过滤器的时候，全部验证通过，才视为通过
* 3.部分过滤器可以指定参数，比如prms，roles
* */

import com.biyesheji.code.shiro.realm.MyRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //必须设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //默认会自动寻找web工程根目录下的“login.jsp”页面
        shiroFilterFactoryBean.setLoginUrl("/");

        //拦截器
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
        //过滤链的定义,从上往下执行,一般将/**放在最下面   这是个坑
        //authc：所有的url必须认证通过才可以访问  anon：所有url都可以匿名访问
        //配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/","anon");
        filterChainDefinitionMap.put("/static/**","anon");
        filterChainDefinitionMap.put("/ueditor/**","anon");
        filterChainDefinitionMap.put("/upload/**","anon");
        filterChainDefinitionMap.put("/user/register.html","anon");
        filterChainDefinitionMap.put("/user/login.html","anon");
        filterChainDefinitionMap.put("/user/register","anon");
        filterChainDefinitionMap.put("/user/login","anon");
        filterChainDefinitionMap.put("/user/sendEmail","anon");
        filterChainDefinitionMap.put("/user/findPassword.html","anon");
        filterChainDefinitionMap.put("/user/checkYzm","anon");
        //配置退出过滤器,启动代码 SHRIO已经实现
        filterChainDefinitionMap.put("/user/logout","logout");

        filterChainDefinitionMap.put("/**","authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;


    }

    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm
        securityManager.setRealm(myRealm());
        return securityManager;
    }


    /*
    * 身份认证realm
    * */
    @Bean
    public MyRealm myRealm(){
        MyRealm myRealm = new MyRealm();
        return myRealm;
    }
    /*
    * shiro生命周期
    * */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /*开启Shiro的注解,借助springAOP扫描Shiro注解的类
    * 配置以下两个Bean即可实现这个功能*/
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

}
