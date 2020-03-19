package com.biyesheji.code.shiro.realm;

import com.biyesheji.code.entity.User;
import com.biyesheji.code.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/*
* 自定义Realm
* */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

/*
授权*/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userName =(String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUserName(userName);
        SimpleAuthorizationInfo info =new SimpleAuthorizationInfo();
        Set<String> roles = new HashSet<>();
        if ("管理员".equals(user.getRoleName())){
            roles.add("进入管理员主页");
        }

        return null;
    }
/*
权限认证
*
* */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String userName = (String) authenticationToken.getPrincipal();
        User user = userService.findByUserName(userName);
        if(user == null){
            return null;
        }else {
            AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUserName(),user.getPassword(),"xx");
            return authenticationInfo;
        }

    }
}
