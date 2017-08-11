package com.jinsheng.shiro;

import com.jinsheng.shiro.resolver.CustomRolePermissionResolver;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * @author jinsheng
 */
public class ShiroTest extends BaseTest {

    @Test
    public void test() {

        SimpleAccountRealm realm = new SimpleAccountRealm();
        realm.addAccount("root", "123456", "role1");
        realm.setRolePermissionResolver(new CustomRolePermissionResolver());

        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealm(realm);

        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("root", "123456");
        subject.login(token);

        subject.checkRole("role2");
        subject.checkPermission("resource1");
    }
}
