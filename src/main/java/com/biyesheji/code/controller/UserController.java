package com.biyesheji.code.controller;

import com.biyesheji.code.entity.Article;
import com.biyesheji.code.entity.User;
import com.biyesheji.code.service.ArticleService;
import com.biyesheji.code.service.UserService;
import com.biyesheji.code.util.CheckShareLinkEnableUtil;
import com.biyesheji.code.util.Consts;
import com.biyesheji.code.util.CryptographyUtil;
import com.biyesheji.code.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sun.net.httpserver.HttpsServerImpl;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;

    @Resource
    private JavaMailSender mailSender;
    /*用户注册*/

    @Autowired
    private ArticleService articleService;


    @ResponseBody
    @PostMapping("/register")
    public Map<String,Object> register(@Valid User user, BindingResult bindingResult){
        Map<String,Object> map = new HashMap<>();
        if (bindingResult.hasErrors()){
            map.put("success",false);
            map.put("errorInfo",bindingResult.getFieldError().getDefaultMessage());
        }else if (userService.findByUserName(user.getUserName())!= null){
            map.put("success",false);
            map.put("errorInfo","用户名已存在，请更换");
        }else if (userService.findByEmail(user.getEmail())!= null){
            map.put("success",false);
            map.put("errorInfo","邮箱已存在，请更换");
        }else{
            user.setPassword(CryptographyUtil.md5(user.getPassword(),CryptographyUtil.SALT));
            user.setRegistrationDate(new Date());
            user.setLatelyLoginTime(new Date());
            user.setHeadPortrait("tou.jpg");
            userService.save(user);
            map.put("success",true);
        }

        return map;
    }

    /*用户登录*/


    @ResponseBody
    @PostMapping("/login")
    public Map<String,Object> login(User user, HttpSession session) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtil.isEmpty(user.getUserName())){
            map.put("success",false);
            map.put("errorInfo","请输入用户名!");

        }else if(StringUtil.isEmpty(user.getPassword())){
            map.put("success",false);
            map.put("errorInfo","请输入密码!");
        }else{
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(),CryptographyUtil.md5(user.getPassword(),CryptographyUtil.SALT));
            try{
                subject.login(token);   //登录验证
                String userName = (String) SecurityUtils.getSubject().getPrincipal();
                User currentUser = userService.findByUserName(userName);
                if (currentUser.isOff()){
                    map.put("success",false);
                    map.put("errorInfo","用户已经封禁");
                    subject.logout();
                }else {
                    currentUser.setLatelyLoginTime(new Date());
                    userService.save(currentUser);
                    session.setAttribute(Consts.CURRENT_USER,currentUser);
                    map.put("success",true);

                }
            }catch (Exception e){
                e.printStackTrace();
                map.put("success",false);
                map.put("errorInfo","用户名或密码错误");

            }
        }

        return  map;

    }

    @ResponseBody
    @PostMapping("/sendEmail")
    public Map<String,Object> sendEmail(String email, HttpSession session){
        Map<String,Object> map = new HashMap<>();
        if(StringUtil.isEmpty(email)){
            map.put("success",false);
            map.put("errorInfo","邮箱不能为空！");
            return map;
        }
        //验证邮件是否存在
        User u = userService.findByEmail(email);
        if(u==null){
            map.put("success",false);
            map.put("errorInfo","邮箱不存在！");
            return map;
        }
        String mailCode = StringUtil.genSixRandom();
        //发邮件
        SimpleMailMessage message = new SimpleMailMessage();        //消息构造器
        message.setFrom("yu980880532@163.com");                        //发件人
        message.setTo(email);                                       //收件人
        message.setSubject("用户找回密码");         //主题
        message.setText("您本次的验证码是：" +mailCode);            //正文内容
        mailSender.send(message);
        System.out.println(mailCode);
        //验证码存到session
        session.setAttribute(Consts.MAIL_CODE_NAME,mailCode);
        session.setAttribute(Consts.USER_ID_NAME,u.getUserId());

        map.put("success",true);
        return map;
    }

    /*邮件验证码判断*/
    @ResponseBody
    @PostMapping("/checkYzm")
    public Map<String,Object> checkYzm(String yzm, HttpSession session){
        Map<String,Object> map = new HashMap<>();
        if(StringUtil.isEmpty(yzm)){
            map.put("success",false);
            map.put("errorInfo","验证码不能为空！");
            return map;
        }
        String mailCode = (String) session.getAttribute(Consts.MAIL_CODE_NAME);
        Integer userId = (Integer) session.getAttribute(Consts.USER_ID_NAME);

        if(!yzm.equals(mailCode)){
            map.put("success",false);
            map.put("errorInfo","验证码错误！");
            return map;
        }

        //给用户重置密码为123456
        User user = userService.getById(userId);
        user.setPassword((CryptographyUtil.md5(Consts.PASSWORD,CryptographyUtil.SALT)));
        userService.save(user);
        map.put("success",true);
        return map;
    }
    /*资源管理*/
    @GetMapping("/articleManage")
    public String articleManage(){
        return "/user/articleManage";
    }
    /*根据条件分页查询信息列表*/
    @ResponseBody
    @RequestMapping("/articleList")
    public Map<String,Object> articleList(Article s_article, @RequestParam(value = "page",required = false)Integer page,
                                          @RequestParam(value = "limit",required = false)Integer pageSize,HttpSession session){

        Map<String,Object> map = new HashMap<>();
        User currentUser =(User) session.getAttribute(Consts.CURRENT_USER);
        s_article.setUser(currentUser);
        map.put("data",articleService.list(s_article,null,null,null,page,pageSize, Sort.Direction.DESC,"publishDate"));
        map.put("count",articleService.geCount(s_article,null,null,null));//总记录数
        map.put("code",0);
        return map;

    }

    /*进入资源发布页面*/
    @GetMapping("toAddArticle")
    public String toAddArticle(){
        return "/user/addArticle";
    }

    /*添加或修改资源*/
    @ResponseBody
    @PostMapping("/saveArticle")
    public Map<String,Object> saveArticle(Article article,HttpSession session) throws IOException {
        Map<String,Object> resultMap = new HashMap<>();
        if(article.getPoints()<0||article.getPoints()>10){
            resultMap.put("success",false);
            resultMap.put("erroInfo","积分超出正常区间!");
            return resultMap;
        }
        if (!CheckShareLinkEnableUtil.check(article.getDownload())){
            resultMap.put("success",false);
            resultMap.put("erroInfo","分享链接已失效");
            return resultMap;
        }
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if (article.getArticleId()==null){  //添加资源
            article.setPublishDate(new Date());
            article.setUser(currentUser);
            if (article.getPoints()==0){ //设置免费资源
                article.setFree(true);
            }
            article.setState(1);//审核状态
            article.setClick(new Random().nextInt(150)+50);   //设置点击数为50-200
            articleService.save(article);
            resultMap.put("success",true);

        }else{
            Article oldArticle =articleService.getById(article.getArticleId());
            if (oldArticle.getUser().getUserId().intValue()== currentUser.getUserId().intValue()){
                oldArticle.setName(article.getName());
                oldArticle.setArcType(article.getArcType());
                oldArticle.setDownload(article.getDownload());
                oldArticle.setPassword(article.getPassword());
                oldArticle.setKeywords(article.getKeywords());
                oldArticle.setDescription(article.getDescription());
                oldArticle.setContent(article.getContent());
                if (oldArticle.getState()==3){
                    oldArticle.setState(1);
                }
                articleService.save(oldArticle);
                //-TODO 更新时需要把新资源信息放入lucene
                resultMap.put("success",true);

            }
        }
        return resultMap;
    }

    @ResponseBody
    @RequestMapping("/checkArticleUser")
    public Map<String,Object> checkArticleUser(Integer articleId, HttpSession session){
        Map<String,Object> resultMap = new HashMap<>();
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        Article article = articleService.getById(articleId);
        if (article.getUser().getUserId().intValue() == currentUser.getUserId().intValue()){
            resultMap.put("success",true);
        }else {
            resultMap.put("success",false);
            resultMap.put("erroInfo","您不是资源所有者，不能修改");
        }
        return resultMap;
    }

    /*进去资源修改页面*/
    @GetMapping("/toEditorArticle/{articleId}")
    public ModelAndView toEditArticle(@PathVariable(value = "articleId",required = true)Integer articleId){
        ModelAndView mav = new ModelAndView();
        Article article =articleService.getById(articleId);
        mav.setViewName("/user/editArticle");
        return mav;
    }

    /*根据id删除一条资源*/
    @ResponseBody
    @RequestMapping("/articleDelete")
    public Map<String,Object> articleDelete(Integer articleId,HttpSession session){
        Map<String,Object> resultMap = new HashMap<>();
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        Article article = articleService.getById(articleId);
        if (article.getUser().getUserId().intValue()== currentUser.getUserId().intValue()){
            //TODO 需要先删除评论
            articleService.delete(articleId);
            //TODO 需要把资源从lucene里面删除
            resultMap.put("success",true);

        }else {
            resultMap.put("success",false);
            resultMap.put("erroInfo","您不是资源所有者，不能删除");
        }
        return resultMap;
    }

}
