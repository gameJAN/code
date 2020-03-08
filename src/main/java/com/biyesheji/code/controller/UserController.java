package com.biyesheji.code.controller;

import com.biyesheji.code.entity.User;
import com.biyesheji.code.service.UserService;
import com.biyesheji.code.util.Consts;
import com.biyesheji.code.util.CryptographyUtil;
import com.biyesheji.code.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;

    @Resource
    private JavaMailSender mailSender;
    /*用户注册*/


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
    public Map<String,Object> sendEmail(String email , HttpSession session){
        Map<String,Object> map = new HashMap<>();
        if(StringUtil.isEmpty(email)){
            map.put("success",false);
            map.put("errorInfo","邮箱不能为空!");
            return  map;
        }
        /*验证邮件是否存在*/

        User u = userService.findByEmail(email);
        if (u == null){
            map.put("success",false);
            map.put("errorInfo","邮箱不存在");
            return map;
        }
        String mailCode = StringUtil.genSixRandom();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yu980880532@163.com");
        message.setTo(email);
        message.setSubject("验证码");
        message.setTo("您的验证码为:" + mailCode);
        mailSender.send(message);
        System.out.println(mailCode);
        //把验证码存到session里面
        session.setAttribute(Consts.MAIL_CODE_NAME,mailCode);
        session.setAttribute(Consts.USER_ID_NAME,u.getUserId());

        map.put("success",true);



        return map;
    }
    /*邮件验证码判断*/
    @ResponseBody
    @PostMapping("/checkYzm")
    public Map<String,Object> checkYzm(String yzm ,HttpSession session){
        Map<String,Object> map = new HashMap<>();
        if (StringUtil.isEmpty(yzm)){
            map.put("success",false);
            map.put("errorInfo","验证码不能为空");
            return map;
        }
        String mailCode = (String) session.getAttribute(Consts.MAIL_CODE_NAME);
        Integer userId = (Integer) session.getAttribute(Consts.USER_ID_NAME);
        if(!yzm.equals(mailCode)){
            map.put("success",false);
            map.put("errorInfo","验证码错误");
            return map;
        }
        User user = userService.getById(userId);
        user.setPassword((CryptographyUtil.md5(Consts.PASSWORD,CryptographyUtil.SALT)));
        userService.save(user);
        map.put("success",true);
        return map;


    }


}
