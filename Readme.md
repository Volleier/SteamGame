# Steam Games Logger
记录Steam游戏的游戏时间和长度

## 部署

## 使用

## 模块

### steam-dependencies (pom)
项目的"依赖版本控制中心"  
功能：
- 统一管理所有子模块的依赖版本（Spring Boot、数据库驱动、工具库等）
- 定义全局Maven插件版本和配置
- 不包含任何业务代码，仅通过`<dependencyManagement>`和`<pluginManagement>`管理版本
- 确保所有子模块使用一致的依赖版本，避免冲突

### steam-common (jar)
项目的"公共代码库"  
功能：
- 存放所有模块共享的代码和配置
- 提供通用工具类、基础DTO、异常处理等
- 定义跨模块的常量、枚举、注解
- 包含数据库实体类、通用AOP切面、基础安全配置等

### steam-login (jar/war)
认证与授权服务  
功能：
- 处理用户登录、注册、注销
- 实现身份认证（如JWT、OAuth2）
- 管理权限验证（RBAC模型）
- 提供会话管理、单点登录(SSO)支

### steam-api (jar)
业务API服务  
功能：
- 提供核心业务逻辑的RESTful API
- 包含控制器(Controller)、服务层(Service)
- 数据校验、业务规则处理

### steam-admin (jar/war)
后台管理系统  
功能：
- 提供管理界面所需的后端接口
- 处理用户管理、角色权限配置
- 数据统计、系统监控