package com.jinsheng.shiro;

import com.alibaba.druid.pool.DruidDataSource;
import com.jinsheng.shiro.resolver.CustomRolePermissionResolver;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
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

    @Test
    public void testJDBCRealm() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        //设置authenticator
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        securityManager.setAuthenticator(authenticator);

        //设置authorizer
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
        authorizer.setPermissionResolver(new WildcardPermissionResolver());
        securityManager.setAuthorizer(authorizer);

        //设置Realm
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/shiro");
        ds.setUsername("root");
        ds.setPassword("root");

        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(ds);
        jdbcRealm.setPermissionsLookupEnabled(true);
        securityManager.setRealm(jdbcRealm);

        //将SecurityManager设置到SecurityUtils 方便全局使用
        SecurityUtils.setSecurityManager(securityManager);

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
        subject.login(token);
        Assert.assertTrue(subject.isAuthenticated());
    }
}
