package com.biyesheji.code.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/*邮箱配置类*/
@Configuration
public class MailConfig {

    /*获取邮件发送实例*/
    @Bean
    public MailSender mailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.163.com");     //指定发送Email的服务器主机名
        mailSender.setPort(587);                //默认端口
        mailSender.setUsername("yu980880532@163.com");//用户名
        mailSender.setPassword("yu18072715924");
        return mailSender;
    }
}
