package com.biyesheji.code.run;

import com.biyesheji.code.service.ArcTypeService;
import com.biyesheji.code.util.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component("StartupRunner")
public class StarupRunner implements CommandLineRunner {

    @Autowired
    private ServletContext application;

    @Autowired
    private ArcTypeService arcTypeService;

    @Override
    public void run(String... args) throws Exception{
        loadData();

    }
    /*加载数据到application缓存中*/
    public void loadData(){
        application.setAttribute(Consts.ARC_TYPE_LIST,arcTypeService.listAll(Sort.Direction.ASC,"sort"));//所有资源分类

    }

}
