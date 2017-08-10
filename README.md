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
- SessionManager：如果写过Servlet就应该知道Session的概念，Session呢需要有人去管理它的生命周期，这个组件就是SessionManager；而Shiro并不仅仅可以用在Web环境，也可以用在如普通的JavaSE环境、EJB等环境；所有呢，Shiro就抽象了一个自己的Session来管理主体与应用之间交互的数据；这样的话，比如我们在Web环境用，刚开始是一台Web服务器；接着又上了台EJB服务器；这时想把两台服务器的会话数据放到一个地方，这个时候就可以实现自己的分布式会话（如把数据放到Memcached服务器）；
- SessionDAO：用于会话的 CRUD，比如想把 Session 放到 Redis 中，可以实现自己的 Redis SessionDAO；另外SessionDAO中可以使用Cache进行缓存，以提高性能
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

## shiro、spring security 对比

| |shiro|spring security|
|---:|---:|---:|
|web|支持|支持|
|非 web|支持|支持|
|spring|可集成、可独立|不能脱离 spring|
|cache|支持|支持|
|remember me|支持|支持|
|OAuth|支持|支持|
|SSO|支持|支持|
|学习成本|低|高|
|实例|SpringSide||

## 问题

- 多 Realm 应用场景
- @RequiresRoles、@RequiresPermissions 如何工作