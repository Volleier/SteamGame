# SteamGame 推荐模块架构说明

本文档定义 SteamGame 后端五个 Maven 模块在长期推荐架构中的职责、边界和依赖方向。该方案以“搭建一个获取 Steam 游戏数据的平台”为目标，允许对现有项目做较大重构。

## 一、总体原则

推荐后端保持五个模块：

```text
steam-common
steam-login
steam-api
steam-admin
steam-launcher
```

核心原则：

- `steam-common` 只放跨模块共享的基础能力，不放业务流程。
- `steam-login` 只负责用户、凭据、安全和认证上下文。
- `steam-api` 负责面向普通用户的 Steam 游戏数据业务。
- `steam-admin` 负责平台运营、维护、监控和数据修复。
- `steam-launcher` 只负责组装和启动，不承载业务。

推荐依赖方向：

```text
steam-launcher
  -> steam-admin
  -> steam-api
  -> steam-login
  -> steam-common

steam-admin
  -> steam-api
  -> steam-login
  -> steam-common

steam-api
  -> steam-login
  -> steam-common

steam-login
  -> steam-common

steam-common
  -> no project module dependency
```

注意：业务实现不能反向放置。例如游戏库同步实现不应放在 `steam-login`，凭据加密实现不应放在 `steam-api`。

## 二、steam-common

### 定位

公共基础模块，提供所有后端模块可复用的通用类型、异常、响应结构、上下文接口和工具类。

### 应放内容

- 统一 API 响应：
  - `ApiResponse<T>`
  - `PageResponse<T>`
- 统一错误模型：
  - `ErrorCode`
  - `BusinessException`
  - `GlobalExceptionHandler` 可选，若需要 web 依赖则放到 launcher 或 web-common 包。
- 用户上下文抽象：
  - `CurrentUser`
  - `CurrentUserProvider`
- 通用常量：
  - 默认用户 ID
  - 时间格式
  - Header 名称
- 通用工具：
  - 字符串脱敏
  - 时间转换
  - 简单校验工具
- 跨模块 DTO 或接口：
  - `SteamCredentialView`
  - `CredentialProvider` 如果希望 `steam-api` 只依赖 common 接口

### 不应放内容

- Steam API 调用逻辑。
- 凭据加密解密业务。
- 游戏库同步业务。
- Controller。
- 数据库 Mapper。

### 推荐包结构

```text
com.SteamGame.common
  response
  error
  context
  constants
  util
```

## 三、steam-login

### 定位

认证与 Steam 凭据管理模块。它负责“用户是谁”和“如何安全拿到用户的 Steam 凭据”，不负责游戏库业务。

### 应放内容

- Steam 凭据配置接口：
  - 保存 SteamID 和 API Key。
  - API Key 加密后持久化。
- Steam 凭据校验接口：
  - 校验 SteamID/API Key 格式。
  - 在线调用 Steam 轻量接口确认凭据可用。
- 凭据读取与解密：
  - `CredentialProviderImpl`
  - 从 session、数据库或 `auth.yaml` 获取凭据。
- 用户认证上下文：
  - 当前用户解析。
  - 单用户 MVP 阶段可返回 `default`。
- 凭据缓存与定时重校验：
  - 失效检测。
  - 定时验证。
  - 验证状态记录。
- 凭据存储：
  - MVP 可继续 `auth.yaml`。
  - 平台化阶段推荐迁移到数据库表。

### 不应放内容

- `OwnedGameServiceImpl`。
- Steam 游戏库同步。
- 游戏详情补全。
- 游戏数据 Mapper。
- 游戏数据 Controller。

### 推荐包结构

```text
com.SteamGame.login
  controller
  service
  service.impl
  repository
  repository.impl
  dto
  model
  security
  config
```

### 推荐公开能力

`steam-login` 对其他模块主要暴露：

```java
public interface CredentialProvider {
    SteamCredential getCurrentCredential(String userId);
}
```

以及：

```java
public interface CurrentUserProvider {
    CurrentUser currentUser();
}
```

## 四、steam-api

### 定位

普通用户侧 Steam 游戏数据业务模块。它负责从 Steam 获取数据、落库、查询并返回给前端。

### 应放内容

- 普通用户 API：
  - `/api/ownedgames/sync`
  - `/api/ownedgames/list`
  - `/api/ownedgames/count`
  - 后续 `/api/games/{appid}`
  - 后续 `/api/games/statistics`
- Steam 外部 API Client：
  - `IPlayerService/GetOwnedGames`
  - Steam Store `appdetails`
  - 封面/图片 URL 生成策略
- 游戏库业务服务：
  - `OwnedGameService`
  - `OwnedGameServiceImpl`
  - `OwnedGameDetailsService`
  - `GameMetadataService`
- 数据库访问：
  - `OwnedGameMapper`
  - `GameMetadataMapper`
  - `SyncJobMapper`
- 数据实体：
  - `OwnedGame`
  - `GameMetadata`
  - `GameSyncJob`
- API DTO：
  - `OwnedGameDTO`
  - `OwnedGameSyncResultDTO`
  - `OwnedGameCountDTO`
- 同步策略：
  - 基础游戏库同步。
  - 游戏详情异步补全。
  - 失败重试。
  - 增量刷新。
- 数据库 schema：
  - `owned_game`
  - `game_metadata`
  - `game_sync_job`

### 不应放内容

- API Key 加密和持久化细节。
- 用户登录流程。
- 管理员后台接口。
- 启动类业务初始化逻辑。

### 推荐包结构

```text
com.SteamGame.api
  controller
  service
  service.impl
  client
  client.impl
  mapper
  domain
  dto
  config
  task
```

### 推荐同步链路

```text
OwnedGamesController.sync()
  -> CurrentUserProvider.currentUser()
  -> CredentialProvider.getCurrentCredential(userId)
  -> SteamApiClient.fetchOwnedGames(steamId, apiKey)
  -> OwnedGameService.upsertOwnedGames(userId, steamId, games)
  -> OwnedGameDetailsService.enqueueMissingDetails(userId)
  -> return OwnedGameDTO list
```

## 五、steam-admin

### 定位

平台管理后台模块。它服务管理员，不服务普通用户。适合在项目平台化后用于运维、监控、数据修复和系统配置。

### 应放内容

- 用户管理：
  - 用户列表。
  - 用户状态。
  - SteamID 绑定状态。
- 凭据状态管理：
  - 是否已配置。
  - 最近验证时间。
  - 验证失败原因。
  - 不展示明文 API Key。
- 同步任务管理：
  - 查看同步历史。
  - 手动触发同步。
  - 重试失败任务。
  - 取消卡住任务。
- Steam API 调用监控：
  - 请求量。
  - 失败率。
  - 超时次数。
  - 限流情况。
- 游戏元数据维护：
  - 缺失详情列表。
  - 手动补全或重新拉取详情。
  - 图片资源状态。
- 数据修复：
  - 重复记录检测。
  - 缺失字段修复。
  - 跨用户脏数据修复。
- 系统配置：
  - 同步频率。
  - 详情补全并发数。
  - Steam API 超时时间。
  - 重试次数。
- 审计日志：
  - 管理员操作记录。
  - 手动同步记录。
  - 配置变更记录。

### 不应放内容

- 普通用户游戏列表接口。
- Steam 凭据保存接口。
- 核心同步实现。
- 应用启动组装逻辑。

### 推荐包结构

```text
com.SteamGame.admin
  controller
  service
  service.impl
  dto
  mapper
  domain
```

### 推荐接口前缀

```text
/api/admin/users
/api/admin/credentials
/api/admin/sync-jobs
/api/admin/game-metadata
/api/admin/system-config
/api/admin/audit-logs
```

## 六、steam-launcher

### 定位

应用启动模块。负责把其他模块装配成一个可运行的 Spring Boot 应用。

### 应放内容

- 唯一 Spring Boot 启动类：
  - `SteamLauncherApplication`
- 全局扫描配置：
  - `@ComponentScan("com.SteamGame")`
  - `@MapperScan(...)`
- 全局运行配置：
  - `application.yaml`
  - profile 选择
  - server port
  - datasource
  - logging
- 启动前置检查：
  - 创建 `data` 目录。
  - 检查 H2 文件路径。
  - 打印当前 profile 和 datasource URL。
- 组合型 Bean：
  - `TaskExecutor`
  - 全局 CORS
  - 全局异常处理装配

### 不应放内容

- Controller。
- Service 业务逻辑。
- Mapper。
- Steam API 调用。
- 凭据加密解密。

### 推荐包结构

```text
com.SteamGame.launcher
  SteamLauncherApplication
  config
  startup
```

## 七、最终推荐运行结构

```text
Browser / Vue
  -> /api/credentials/*
  -> /api/ownedgames/*
  -> /api/admin/*

steam-launcher
  -> scans all modules
  -> starts one backend process on 8080

steam-login
  -> credential and user context

steam-api
  -> user-facing Steam game data

steam-admin
  -> platform operations and maintenance

steam-common
  -> shared response, error, context, constants
```

## 八、当前项目最重要的架构调整

按优先级：

1. 将 `OwnedGameServiceImpl` 从 `steam-login` 移到 `steam-api`。
2. 将 `steam-admin` 的包名从 `com.SteamGame.login` 改为 `com.SteamGame.admin`。
3. 移除 `steam-api` 单独启动入口或明确只用于测试，生产只用 `steam-launcher`。
4. 把重复的 `ApiResponse` 统一到 `steam-common`。
5. 引入 DTO，避免数据库实体直接返回给前端。
6. 修正所有游戏数据更新都带 `user_id`。
7. 将详情补全和同步任务状态平台化，为 `steam-admin` 提供管理基础。
