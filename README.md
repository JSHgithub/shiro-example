# shiro-example

## shiro 是什么?

- 简单、轻量的 Java 安全框架

## shiro 可以做些什么？

![shiro](http://dl2.iteye.com/upload/attachment/0093/9788/d59f6d02-1f45-3285-8983-4ea5f18111d5.png)

- Authentication：身份认证/登录
- Authorization：授权，即权限验证
- Session Manager：会话管理，会话可以是普通JavaSE环境的，也可以是如Web环境的
- Cryptography：加密
- Web Support：Web支持，可以非常容易的集成到Web环境
- Caching：缓存
- Concurrency：多线程应用的并发验证，即如在一个线程中开启另一个线程，能把权限自动传播过去
- Testing：测试支持
- Run As：允许一个用户假装为另一个用户（如果他们允许）的身份进行访问
- Remember Me：记住我

## 架构

![shiro](http://dl2.iteye.com/upload/attachment/0093/9792/9b959a65-799d-396e-b5f5-b4fcfe88f53c.png)

- Subject：主体，可以是任何可以与应用交互的“用户”
- SecurityManager：Shiro 的心脏（为什么？），相当于 SpringMVC 中的 [DispatcherServlet](https://stackoverflow.com/questions/2769467/what-is-dispatcher-servlet-in-spring)，控制所有具体的交互，管理认证、授权、会话、缓存和所有 Subject
- Authenticator：认证器
- Authorizer：授权器
- Realm：1个或多个，安全数据源，由用户提供，securityManager 会按照 realms 指定的顺序进行身份认证
- SessionManager：可以实现自己的分布式会话（如把数据放到 Redis 中）
- SessionDAO：用于会话的 CRUD，可以使用Cache进行缓存，以提高性能
- CacheManager：缓存控制器可以提高访问的性能
- Cryptography：密码模块

## 身份认证

- Subject：主体，包括 Principals 和 Credentials
- Realm：验证主体的数据源
- Principals：身份，用户名、邮箱等
- Credentials：凭证，密码、数字证书

身份认证流程

- 设置 SecurityManager
- 调用 Subject.login(token) 进行登录
- SecurityManager 委托给 Authenticator 进行身份验证
- Authenticator（默认 ModularRealmAuthenticator）调用 AuthenticationStrategy 进行多 Realm 身份验证
- Authenticator 把相应的 token 传入 Realm 获取身份验证信息

Realm

- IniRealm
- PropertiesRealm
- JdbcRealm

AuthenticationStrategy

- FirstSuccessfulStrategy：返回第一个Realm身份验证成功的认证信息
- AtLeastOneSuccessfulStrategy：返回所有Realm身份验证成功的认证信息
- AllSuccessfulStrategy：返回所有 Realm 身份验证成功的认证信息

## 授权

授权粒度
- 角色：粗粒度
- 资源：细粒度

授权方式
- 编程式

```java
  Assert.isTrue(SecurityUtils.getSubject().hasRole("admin"));
```
- 注解式

```java
  @RequiresRoles("admin") 
```
- JSP 标签

```jsp
  <shiro:hasRole name="admin"></shiro:hasRole> 
```
字符串通配符权限

```java
  WildcardPermission.implies(Permission p) // 可根据业务灵活扩展
```
授权流程
- Subject.isPermitted() 并委托给SecurityManager
- SecurityManager 委托给 Authorizer
- Authorizer 通过 PermissionResolver 把字符串转换成相应的 Permission 实例
- 调用相应的 Realm 获取 Subject 相应的角色/权限用于匹配传入的角色/权限
- Authorizer 委托给 ModularRealmAuthorizer 进行循环判断

## 加密

散列算法
- MD2、MD5
- SHA1、SHA256、SHA384、SHA512

对称加密
- AES
- Blowfish

加密密码服务
- PasswordService
- DefaultPasswordService 默认实现
- 随机生成盐

验证密码服务
- CredentialsMatcher
- HashedCredentialsMatcher 散列实现
- 需要实现生成密码散列值的算法且和生成密码时的算法一样
- 可以提供自己的盐

## Web 集成

过滤器
- DefaultFilter 默认拦截器，自动注册，可禁用
- DefaultFilterChainManager 维护着 url 模式与拦截器链的关系

|拦截器名|拦截器类|说明|
|---:|---:|---:|
|authc|FormAuthenticationFilter|内置登录实现，基于表单的验证，不判断 remember me|
|anon|AnonymousFilter|匿名拦截器|
|logout|LogoutFilter|退出拦截器|
|user|UserFilter|用户拦截器|
|perms|PermissionsAuthorizationFilter|权限授权拦截器|
|roles|RolesAuthorizationFilter|角色授权拦截器|
||OncePerRequestFilter|保证一次请求只调用一次|
||AdviceFilter|设计思想类似 SpringMVC|
||PathMatchingFilter|提供了 url 模式过滤的功能|

url 模式匹配顺序

```java
  // 使用 ShiroFilterFactoryBean 创建 shiroFilter 时，默认使用 PathMatchingFilterChainResolver 进行解析
  // 如果多个拦截器链都匹配了当前请求 URL，PathMatchingFilterChainResolver 只返回第一个找到的拦截器链
  // 如下：如果请求的url是“/bb/aa”，按照配置中的声明顺序，将使用filter1进行拦截
  
  /bb/**=filter1  
  /bb/aa=filter2  
  /**=filter3
```

## 会话

会话
- HTTP 是无状态的，需要保持用户访问应用时的连接关系，Java 中 HTTP 的 Session 对象用 [javax.servlet.http.HttpSession](http://lavasoft.blog.51cto.com/62575/275589) 来表示。
- Shiro 提供的会话不依赖于任何底层容器，可以独立使用，是完整的会话模块。
- Shiro 提供的会话可以在普通的 JavaSE、JavaEE（如 web 应用）应用中使用，且使用方式一致。
- 默认情况在创建 Subject 时会主动创建一个 Session。

会话监听器
- SessionListener：监听会话创建、过期及停止事件
- SessionListenerAdapter：只监听某一个事件

会话存储
- CachingSessionDAO：会话缓存
- MemorySessionDAO：直接在内存中进行会话维护
- EnterpriseCacheSessionDAO：带缓存功能的会话维护

## Spring 集成

开启 Shiro Spring AOP 权限注解的支持

```java
  @Configuration
  @EnableAspectJAutoProxy(proxyTargetClass=true)
  public class ShiroSpringConfig {

      @Autowired
      private SecurityManager securityManager;

      @Bean
      public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
          AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
          authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
          return authorizationAttributeSourceAdvisor;
      }
  }
```
## Remember Me

记住我
```
  关闭并重新打开浏览器，无需登录即可再次访问
```

使用方式

```java
  UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
```

配置

```java
  @Bean
  public SimpleCookie rememberMeCookie() {
      SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
      simpleCookie.setHttpOnly(true);
      simpleCookie.setMaxAge(2592000); // 30天
      return simpleCookie;
  }

  @Bean
  public CookieRememberMeManager rememberMeManager() {
      CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
      cookieRememberMeManager.setCookie(rememberMeCookie());
      return cookieRememberMeManager;
  }

  @Bean
  public SecurityManager securityManager() {
      DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
      ...
      securityManager.setRememberMeManager(rememberMeManager());
      return securityManager;
  }
```

## shiro、spring security 对比

| |shiro|spring security|
|---:|---:|---:|
|web|支持|支持|
|非 web|支持|支持|
|spring|可集成、可独立|不能脱离 spring|
|session|支持|支持|
|cache|支持|支持|
|remember me|支持|支持|
|encrypt|支持|支持|
|OAuth|支持|支持|
|SSO|支持|支持|
|学习成本|低|高|
|实例|SpringSide||

## 注意

- 当我们使用了 RequiresRoles 与 RequiresPermissions 注解，也就意味着您把代码写死了，这样如果数据库里的 Role 或 Permission 更改了，代码也就无效了。
- 验证码过滤器需要放到 Shiro 之后，因为 Shiro 将包装 HttpSession，可能造成两次的 sesison id 不一样。
- Shiro 先走自己的 Filter 体系，然后才会委托给 Servlet 容器的 FilterChain 进行 Servlet 容器级别的 Filter 链执行。

## 问题

- 多 Realm 应用场景
- @RequiresRoles、@RequiresPermissions 如何工作

## 参考

- [RBAC新解：基于资源的权限管理(Resource-Based Access Control)](http://globeeip.iteye.com/blog/1236167)

## 说明

以上部分内容摘自[张开涛的博客-跟我学shiro](http://jinnianshilongnian.iteye.com/blog/2018398)，特此说明。
