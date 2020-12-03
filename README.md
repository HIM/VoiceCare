# VoiceCare👋👴👦👧👵
a back-end code of a project, using SpringBoot 

- This code is the back-end module of the project
- using the SpringBoot framework🚀
- At present, we can register and log in, send audio files through audio flow to the front end
- and detect the changes of files in folders in the server in real time

VoiceCare
├── controller-UserController -- 用户信息管理接口
├── controller-AudioController -- 与音频相关的听新闻、对话接口
├── model -- 用户信息、音频类、json封装类
├── service-UserService -- 用户信息管理业务逻辑实现类
├── service-AudioService -- 音频服务实现类
├── utils -- 登录验证JWT、封装Json工具类
├── interceptor-LoginInterceptor -- 拦截器工具类
├── config -- 配置类
└── mapper -- MyBatis数据库操作代码

后端技术：
技术 | 说明 | 官网
---|---|---
SpringBoot | 容器+MVC框架 | https://spring.io/projects/spring-boot/
MyBatis | ORM框架 | https://mybatis.org/mybatis-3/zh/index.html
Druid | 数据库连接池 | https://github.com/alibaba/druid
JWT | JWT登录支持 | https://github.com/jwtk/jjwt

开发环境：
工具 | 版本号 | 下载
---|---|---
JDK | 1.8 | https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
Mysql | 5.7 | https://www.mysql.com/
