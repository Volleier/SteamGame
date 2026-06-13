# SteamGame 推荐架构改造计划

本文档用于把当前项目改造到 `docs/ModuleIntro.md` 定义的推荐架构。计划允许大规模调整模块边界、包结构和文件职责。

## 一、改造目标

最终目标：

- 后端只通过 `steam-launcher` 启动。
- `steam-common` 提供通用基础设施。
- `steam-login` 只负责用户与 Steam 凭据。
- `steam-api` 只负责普通用户 Steam 游戏数据。
- `steam-admin` 负责平台管理、同步监控和数据维护。
- 前端只访问标准 REST API，不接触明文 API Key。

## 二、阶段 1：统一公共基础能力

### 1. 调整 `steam-common` 包结构

新增目录：

```text
steam-common/src/main/java/com/SteamGame/common/response
steam-common/src/main/java/com/SteamGame/common/error
steam-common/src/main/java/com/SteamGame/common/context
steam-common/src/main/java/com/SteamGame/common/constants
steam-common/src/main/java/com/SteamGame/common/util
```

### 2. 移动并升级 ApiResponse

当前文件：

```text
steam-common/src/main/java/com/SteamGame/common/dto/ApiResponse.java
```

目标文件：

```text
steam-common/src/main/java/com/SteamGame/common/response/ApiResponse.java
```

实现内容：

- 字段：
  - `int code`
  - `String msg`
  - `T data`
- 静态方法：
  - `ok(T data)`
  - `ok()`
  - `fail(int code, String msg)`
  - `fail(ErrorCode errorCode)`

同时修改所有 import：

- `steam-api/src/main/java/com/SteamGame/api/controller/OwnedGamesController.java`
- `steam-login/src/main/java/com/SteamGame/login/controller/CredentialConfigController.java`
- `steam-login/src/main/java/com/SteamGame/login/controller/CredentialVerifyController.java`
- 其他使用 `ApiResponse` 的文件。

### 3. 删除重复响应类

当前文件：

```text
steam-login/src/main/java/com/SteamGame/login/dto/ApiResponse.java
```

处理：

- 删除该文件。
- 所有登录模块响应统一使用 `com.SteamGame.common.response.ApiResponse`。

### 4. 新增 ErrorCode

新增文件：

```text
steam-common/src/main/java/com/SteamGame/common/error/ErrorCode.java
```

实现内容：

```java
public enum ErrorCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    STEAM_CREDENTIAL_NOT_FOUND(1001, "Steam 凭据未配置"),
    STEAM_CREDENTIAL_INVALID(1002, "Steam 凭据无效"),
    STEAM_API_TIMEOUT(2001, "Steam API 请求超时"),
    STEAM_API_UNAVAILABLE(2002, "Steam API 不可用"),
    GAME_SYNC_FAILED(3001, "游戏库同步失败");
}
```

### 5. 新增 BusinessException

新增文件：

```text
steam-common/src/main/java/com/SteamGame/common/error/BusinessException.java
```

实现内容：

- 持有 `ErrorCode`。
- 支持自定义 message。
- Service 层遇到业务错误时抛出，不直接拼 Controller 响应。

### 6. 新增用户上下文接口

新增文件：

```text
steam-common/src/main/java/com/SteamGame/common/context/CurrentUser.java
steam-common/src/main/java/com/SteamGame/common/context/CurrentUserProvider.java
```

`CurrentUser` 字段：

- `String userId`
- `String username`
- `boolean admin`

`CurrentUserProvider` 方法：

```java
CurrentUser currentUser();
```

MVP 默认实现放在 `steam-login`，返回 `default` 用户。

## 三、阶段 2：重构 steam-login 为凭据模块

### 1. 保留的文件职责

保留并整理：

```text
steam-login/src/main/java/com/SteamGame/login/controller/CredentialConfigController.java
steam-login/src/main/java/com/SteamGame/login/controller/CredentialVerifyController.java
steam-login/src/main/java/com/SteamGame/login/service/CredentialConfigService.java
steam-login/src/main/java/com/SteamGame/login/service/CredentialVerifyService.java
steam-login/src/main/java/com/SteamGame/login/service/CredentialValidationService.java
steam-login/src/main/java/com/SteamGame/login/service/CredentialKeyService.java
steam-login/src/main/java/com/SteamGame/login/service/CredentialSessionStore.java
steam-login/src/main/java/com/SteamGame/login/service/impl/CredentialConfigServiceImpl.java
steam-login/src/main/java/com/SteamGame/login/service/impl/CredentialVerifyServiceImpl.java
steam-login/src/main/java/com/SteamGame/login/service/impl/CredentialValidationServiceImpl.java
steam-login/src/main/java/com/SteamGame/login/service/impl/CredentialProviderImpl.java
steam-login/src/main/java/com/SteamGame/login/repository/impl/YamlCredentialRepository.java
```

这些文件只处理：

- SteamID/API Key 输入。
- API Key 加密保存。
- API Key 解密读取。
- 凭据有效性验证。
- 凭据状态展示。

### 2. 移出 OwnedGameServiceImpl

当前文件：

```text
steam-login/src/main/java/com/SteamGame/login/service/impl/OwnedGameServiceImpl.java
```

移动到：

```text
steam-api/src/main/java/com/SteamGame/api/service/impl/OwnedGameServiceImpl.java
```

修改内容：

- 包名改为 `com.SteamGame.api.service.impl`。
- 保持实现 `com.SteamGame.api.service.OwnedGameService`。
- 删除所有不必要的 login 包 import。
- `userId` 从 `CurrentUserProvider` 或 Controller 传入。

完成后删除旧文件。

### 3. 调整 CredentialProvider 接口归属

推荐移动：

当前文件：

```text
steam-api/src/main/java/com/SteamGame/api/service/CredentialProvider.java
steam-api/src/main/java/com/SteamGame/api/domain/SteamCredential.java
```

目标文件：

```text
steam-common/src/main/java/com/SteamGame/common/context/CredentialProvider.java
steam-common/src/main/java/com/SteamGame/common/context/SteamCredential.java
```

实现内容不变：

```java
SteamCredential getCurrentCredential(String userId);
```

需要修改 import：

- `CredentialProviderImpl`
- `OwnedGameServiceImpl`
- 其他引用 `SteamCredential` 的文件。

### 4. 新增默认 CurrentUserProvider 实现

新增文件：

```text
steam-login/src/main/java/com/SteamGame/login/security/DefaultCurrentUserProvider.java
```

实现内容：

- 实现 `CurrentUserProvider`。
- 当前 MVP 返回：

```java
new CurrentUser("default", "User", false)
```

后续接入真实登录后，在这里读取 Token/JWT/session。

### 5. 移除 login 模块对 steam-api 的反向业务依赖

修改文件：

```text
steam-login/pom.xml
```

目标：

- 长期删除对 `steam-api` 的依赖。
- 保留对 `steam-common` 的依赖。

注意：

- 要先完成 `CredentialProvider` 和 `SteamCredential` 下沉到 `steam-common`。
- 要先移动 `OwnedGameServiceImpl`。

## 四、阶段 3：重构 steam-api 为游戏业务模块

### 1. 新增 DTO 包

新增目录：

```text
steam-api/src/main/java/com/SteamGame/api/dto
```

新增文件：

```text
OwnedGameDTO.java
OwnedGameCountDTO.java
OwnedGameSyncResultDTO.java
```

`OwnedGameDTO` 内容：

- `Long appid`
- `String name`
- `Integer playtimeForever`
- `String developer`
- `String publisher`
- `String releaseDate`
- `String tags`

`OwnedGameCountDTO` 内容：

- `int count`

`OwnedGameSyncResultDTO` 内容：

- `int total`
- `int saved`
- `List<OwnedGameDTO> games`
- `boolean detailsInProgress`

### 2. 新增 DTO 转换器

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/dto/OwnedGameDtoConverter.java
```

实现内容：

- `OwnedGameDTO toDto(OwnedGame game)`
- `List<OwnedGameDTO> toDtoList(List<OwnedGame> games)`

Controller 只返回 DTO，不返回 `OwnedGame` 实体。

### 3. 改造 OwnedGamesController

当前文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/OwnedGamesController.java
```

修改内容：

- 使用 `CurrentUserProvider` 获取当前用户。
- 返回 `ApiResponse<List<OwnedGameDTO>>`。
- `/count` 返回 `ApiResponse<OwnedGameCountDTO>`。
- 删除或限制 `/fetch` 调试接口。

推荐方法：

```java
@PostMapping("/sync")
public ApiResponse<List<OwnedGameDTO>> sync()

@GetMapping("/list")
public ApiResponse<List<OwnedGameDTO>> list()

@GetMapping("/count")
public ApiResponse<OwnedGameCountDTO> count()
```

### 4. 改造 OwnedGameService 接口

当前文件：

```text
steam-api/src/main/java/com/SteamGame/api/service/OwnedGameService.java
```

推荐方法：

```java
List<OwnedGame> syncOwnedGames(String userId);
List<OwnedGame> listOwnedGames(String userId);
int countOwnedGames(String userId);
```

可新增：

```java
OwnedGameSyncResult syncOwnedGamesWithResult(String userId);
```

其中 `OwnedGameSyncResult` 可放在：

```text
steam-api/src/main/java/com/SteamGame/api/domain/OwnedGameSyncResult.java
```

### 5. 改造 SteamApiClientImpl

当前文件：

```text
steam-api/src/main/java/com/SteamGame/api/client/impl/SteamApiClientImpl.java
```

修改内容：

- 不再设置 `game.setUserId("default")`。
- 只负责解析 Steam 返回字段：
  - `appid`
  - `name`
  - `playtime_forever`
- 不记录包含 API Key 的完整 URL。
- HTTP 非 200 时抛出 `BusinessException` 或专用异常。
- `fillGameDetails()` 只填充传入对象，不操作数据库。

### 6. 改造 OwnedGameServiceImpl

目标文件：

```text
steam-api/src/main/java/com/SteamGame/api/service/impl/OwnedGameServiceImpl.java
```

实现内容：

```text
1. 从 CredentialProvider 获取当前用户凭据
2. 校验凭据是否有效
3. 调用 SteamApiClient.fetchOwnedGames()
4. 对每个 OwnedGame 设置 userId、steamId、lastSyncedAt
5. 批量 upsert 到 owned_game
6. 触发 OwnedGameDetailsService 异步补全详情
7. 查询并返回本地最新列表
```

错误处理：

- 无凭据：抛 `BusinessException(STEAM_CREDENTIAL_NOT_FOUND)`。
- Steam API 失败：记录日志，返回本地已有数据或抛出明确异常，由产品策略决定。
- 数据库写入失败：抛 `BusinessException(GAME_SYNC_FAILED)`。

### 7. 新增 OwnedGameDetailsService

新增接口：

```text
steam-api/src/main/java/com/SteamGame/api/service/OwnedGameDetailsService.java
```

新增实现：

```text
steam-api/src/main/java/com/SteamGame/api/service/impl/OwnedGameDetailsServiceImpl.java
```

实现内容：

- `enqueueMissingDetails(String userId)`
- `syncMissingDetails(String userId, int limit)`
- 查询缺少详情的游戏。
- 调用 `SteamApiClient.fillGameDetails()`。
- 调用 Mapper 更新详情。
- 控制请求间隔和失败日志。

### 8. 改造 OwnedGameMapper

当前文件：

```text
steam-api/src/main/java/com/SteamGame/api/mapper/OwnedGameMapper.java
```

修改内容：

- `updateDetails()` 增加 `userId` 条件。
- `listMissingDetails()` 改为 `listMissingDetailsByUserId(userId, limit)`。
- `upsert()` 更新 `updated_at`。
- 新增 `batchUpsert()` 可选。

推荐方法：

```java
void upsert(OwnedGame game);
List<OwnedGame> listByUserId(String userId);
int countByUserId(String userId);
List<OwnedGame> listMissingDetailsByUserId(String userId, int limit);
void updateDetails(String userId, Long appid, String developer, String publisher, String releaseDate, String tags);
```

### 9. 改造 schema.sql

当前文件：

```text
steam-api/src/main/resources/schema.sql
```

修改内容：

- 增加 `details_synced_at`。
- 增加索引。
- 确保 `updated_at` 在业务更新中刷新。

推荐新增：

```sql
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS details_synced_at TIMESTAMP;
CREATE INDEX IF NOT EXISTS idx_owned_game_user_id ON owned_game(user_id);
```

H2 对复杂索引兼容性有限，先保持简单索引。

### 10. 新增任务线程池配置

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/config/GameTaskConfig.java
```

实现内容：

- 定义 `ThreadPoolTaskExecutor`。
- Bean 名称 `gameDetailsExecutor`。
- 用于详情补全任务。

## 五、阶段 4：重构 steam-admin 为管理后台模块

### 1. 修正包名和启动类

当前文件：

```text
steam-admin/src/main/java/com/SteamGame/login/LoginApplication.java
```

处理：

- 删除该文件。
- 新增：

```text
steam-admin/src/main/java/com/SteamGame/admin/AdminModuleConfiguration.java
```

实现内容：

```java
@Configuration
public class AdminModuleConfiguration {
}
```

`steam-admin` 不需要自己的 `@SpringBootApplication`，由 `steam-launcher` 统一启动。

### 2. 新增 admin controller 包

新增目录：

```text
steam-admin/src/main/java/com/SteamGame/admin/controller
```

新增文件：

```text
AdminUserController.java
AdminCredentialController.java
AdminSyncJobController.java
AdminGameMetadataController.java
AdminSystemConfigController.java
```

### 3. AdminUserController

新增文件：

```text
steam-admin/src/main/java/com/SteamGame/admin/controller/AdminUserController.java
```

接口前缀：

```text
/api/admin/users
```

MVP 实现内容：

- `GET /api/admin/users`
- 返回单用户 `default` 的占位数据。

后续实现：

- 用户列表。
- 用户状态。
- SteamID 绑定状态。

### 4. AdminCredentialController

新增文件：

```text
steam-admin/src/main/java/com/SteamGame/admin/controller/AdminCredentialController.java
```

接口前缀：

```text
/api/admin/credentials
```

实现内容：

- 查看凭据配置状态。
- 查看最近验证时间。
- 查看失败原因。
- 不返回明文 API Key。

依赖：

- `steam-login` 的凭据状态服务。

### 5. AdminSyncJobController

新增文件：

```text
steam-admin/src/main/java/com/SteamGame/admin/controller/AdminSyncJobController.java
```

接口前缀：

```text
/api/admin/sync-jobs
```

实现内容：

- 查询同步历史。
- 查询最近同步结果。
- 手动触发某用户同步。
- 重试失败任务。

依赖：

- `steam-api` 的同步服务。

### 6. AdminGameMetadataController

新增文件：

```text
steam-admin/src/main/java/com/SteamGame/admin/controller/AdminGameMetadataController.java
```

接口前缀：

```text
/api/admin/game-metadata
```

实现内容：

- 查询缺少详情的游戏。
- 手动触发详情补全。
- 查看元数据缓存状态。

依赖：

- `OwnedGameDetailsService`
- 后续 `GameMetadataService`

### 7. AdminSystemConfigController

新增文件：

```text
steam-admin/src/main/java/com/SteamGame/admin/controller/AdminSystemConfigController.java
```

接口前缀：

```text
/api/admin/system-config
```

实现内容：

- 查询当前同步配置。
- 查询 Steam API 超时配置。
- 查询详情补全并发配置。

初期只读配置即可，后续再支持修改。

### 8. 修改 steam-admin/pom.xml

当前文件：

```text
steam-admin/pom.xml
```

新增依赖：

```xml
<dependency>
  <groupId>com.SteamGame</groupId>
  <artifactId>steam-common</artifactId>
  <version>0.0.1</version>
</dependency>
<dependency>
  <groupId>com.SteamGame</groupId>
  <artifactId>steam-login</artifactId>
  <version>0.0.1</version>
</dependency>
<dependency>
  <groupId>com.SteamGame</groupId>
  <artifactId>steam-api</artifactId>
  <version>0.0.1</version>
</dependency>
```

说明：

- `steam-admin` 可以依赖 `steam-api` 和 `steam-login`，因为它是管理聚合模块。
- 不要让 `steam-api` 或 `steam-login` 依赖 `steam-admin`。

## 六、阶段 5：简化 steam-launcher

### 1. 调整包名

当前文件：

```text
steam-launcher/src/main/java/com/SteamGame/steamLauncher/SteamLauncherApplication.java
```

推荐目标：

```text
steam-launcher/src/main/java/com/SteamGame/launcher/SteamLauncherApplication.java
```

包名：

```java
package com.SteamGame.launcher;
```

### 2. 保留唯一启动类

`SteamLauncherApplication` 只保留：

- `main()`
- `@SpringBootApplication`
- `@ComponentScan("com.SteamGame")`
- `@MapperScan({"com.SteamGame.api.mapper", "com.SteamGame.admin.mapper"})`

如果 admin 暂无 mapper，可以只扫 api mapper。

### 3. 移出启动类中的业务初始化

当前 `SteamLauncherApplication` 中调用：

- `CredentialVerifyController.init()`
- `CredentialConfigController.init()`

处理：

- 不要在 launcher 里直接调用 Controller。
- 若确实需要初始化，放到 service 或 startup runner：

新增：

```text
steam-launcher/src/main/java/com/SteamGame/launcher/startup/ApplicationStartupRunner.java
```

实现内容：

- 打印 datasource URL。
- 创建 data 目录。
- 不调用 Controller。

### 4. 统一配置文件

当前文件：

```text
steam-launcher/src/main/resources/application.yaml
steam-api/src/main/resources/application.yml
steam-api/src/main/resources/application-local.yml
steam-api/src/main/resources/application-dev.yml
```

推荐：

- 主运行配置放 `steam-launcher/src/main/resources/application.yaml`。
- 模块默认配置可以保留在模块内，但不要互相冲突。

`steam-launcher/application.yaml` 推荐内容：

```yaml
spring:
  application:
    name: steam-game
  profiles:
    active: local
  datasource:
    url: jdbc:h2:file:./data/steamdb;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  sql:
    init:
      mode: always

server:
  port: 8080

login:
  config:
    path: auth.yaml

steam:
  api:
    timeoutSeconds: 15
    detailsTimeoutSeconds: 6
    detailsDelayMillis: 1500
```

## 七、阶段 6：pom 依赖关系整理

### 1. 根 pom.xml

当前文件：

```text
pom.xml
```

推荐 modules 顺序：

```xml
<modules>
    <module>steam-common</module>
    <module>steam-login</module>
    <module>steam-api</module>
    <module>steam-admin</module>
    <module>steam-launcher</module>
</modules>
```

可保持现状。

### 2. steam-login/pom.xml

目标依赖：

```text
steam-common
spring boot starter web
spring boot starter validation
snakeyaml
```

删除：

```text
steam-api
```

前提：

- `CredentialProvider` 和 `SteamCredential` 已移动到 `steam-common`。
- `OwnedGameServiceImpl` 已移动到 `steam-api`。

### 3. steam-api/pom.xml

目标依赖：

```text
steam-common
steam-login
spring boot starter web
mybatis spring boot starter
h2 runtime
jackson databind
```

说明：

- 如果 `CredentialProvider` 接口在 common，`steam-api` 可以只依赖 common。
- 但运行时需要 login 的实现，由 launcher 聚合提供。
- 若编译期不直接使用 login 类，建议 `steam-api` 不依赖 `steam-login`。

推荐最终：

```text
steam-api -> steam-common
steam-login -> steam-common
steam-launcher -> steam-api + steam-login + steam-admin
```

### 4. steam-admin/pom.xml

目标依赖：

```text
steam-common
steam-login
steam-api
spring boot starter web
```

### 5. steam-launcher/pom.xml

目标依赖：

```text
steam-common
steam-login
steam-api
steam-admin
spring boot starter web
h2 runtime if not transitive
```

`steam-launcher` 是唯一可执行 jar。

## 八、阶段 7：测试计划

### 1. common 测试

新增：

```text
steam-common/src/test/java/com/SteamGame/common/response/ApiResponseTest.java
steam-common/src/test/java/com/SteamGame/common/error/BusinessExceptionTest.java
```

验证：

- `ApiResponse.ok()` 格式。
- `ApiResponse.fail()` 格式。
- `BusinessException` 正确持有 `ErrorCode`。

### 2. login 测试

新增：

```text
steam-login/src/test/java/com/SteamGame/login/service/CredentialProviderImplTest.java
steam-login/src/test/java/com/SteamGame/login/service/CredentialValidationServiceImplTest.java
```

验证：

- 能从测试 YAML 解密凭据。
- 缺少 key 时返回无效凭据。
- 格式校验正确。

### 3. api 测试

新增：

```text
steam-api/src/test/java/com/SteamGame/api/client/SteamApiClientImplTest.java
steam-api/src/test/java/com/SteamGame/api/mapper/OwnedGameMapperTest.java
steam-api/src/test/java/com/SteamGame/api/controller/OwnedGamesControllerTest.java
```

验证：

- Steam JSON 解析。
- upsert 不产生重复数据。
- `updateDetails` 不跨用户更新。
- Controller 返回 DTO，不返回实体内部字段。

### 4. launcher 集成测试

新增：

```text
steam-launcher/src/test/java/com/SteamGame/launcher/SteamLauncherContextTest.java
```

验证：

- Spring context 能启动。
- `OwnedGameService` Bean 存在。
- `CredentialProvider` Bean 存在。
- `OwnedGamesController` Bean 存在。

## 九、阶段 8：删除和清理

### 1. 删除 steam-test

如果确认无保留价值，可删除：

```text
steam-test
```

原因：

- 不在根 `pom.xml` modules 中。
- 不参与主构建。
- 功能是早期实验调试。

如有可复用测试逻辑，应迁移到：

```text
steam-api/src/test
```

### 2. 清理旧启动类

删除或停用：

```text
steam-api/src/main/java/com/SteamGame/api/SteamApiApplication.java
steam-admin/src/main/java/com/SteamGame/login/LoginApplication.java
steam-common/src/main/java/com/SteamGame/common/CommonApplication.java
```

建议：

- 模块 jar 不需要自己的 `@SpringBootApplication`。
- 只保留 `steam-launcher` 的启动类。

如果为了模块测试保留，也不要作为部署入口。

### 3. 清理敏感调试接口

修改：

```text
steam-api/src/main/java/com/SteamGame/api/controller/OwnedGamesController.java
```

删除：

```java
@GetMapping("/fetch")
```

或加：

```java
@Profile("dev")
```

## 十、推荐执行顺序

1. 重构 `steam-common`：统一 `ApiResponse`、`ErrorCode`、`BusinessException`、用户上下文接口。
2. 移动 `CredentialProvider` 和 `SteamCredential` 到 `steam-common`。
3. 移动 `OwnedGameServiceImpl` 到 `steam-api`。
4. 修改 `steam-login/pom.xml`，删除对 `steam-api` 的依赖。
5. 新增 `OwnedGameDTO` 和转换器。
6. 改造 `OwnedGamesController`，实体不再直出。
7. 改造 `SteamApiClientImpl`，移除固定 `default` userId 和敏感 URL 日志。
8. 改造 `OwnedGameMapper`，所有详情更新带 `user_id`。
9. 新增 `OwnedGameDetailsService` 和任务线程池。
10. 修正 `steam-admin` 包名和模块结构，新增 admin 占位接口。
11. 简化 `steam-launcher`，只做启动组装。
12. 统一 `application.yaml`。
13. 清理旧启动类和 `/fetch`。
14. 迁移或删除 `steam-test`。
15. 补充单元测试和 launcher 集成测试。

## 十一、最终验收标准

后端构建：

```bash
steam-api\mvnw.cmd -f pom.xml clean test
```

后端启动：

```bash
java -Dfile.encoding=UTF-8 -jar steam-launcher\target\steam-launcher-0.0.1.jar
```

接口验收：

```bash
curl http://localhost:8080/api/ownedgames/count
curl http://localhost:8080/api/ownedgames/list
curl -X POST http://localhost:8080/api/ownedgames/sync
curl http://localhost:8080/api/admin/users
```

验收条件：

- 只有 `steam-launcher` 是正式启动入口。
- `steam-login` 不依赖 `steam-api`。
- 游戏业务实现全部在 `steam-api`。
- 管理接口全部在 `steam-admin`。
- 通用响应和错误码全部来自 `steam-common`。
- `/api/ownedgames/list` 不返回实体内部字段。
- `/api/ownedgames/sync` 不需要前端传 API Key。
- 重复同步不产生重复游戏。
- 详情更新不会跨用户污染。
- Maven test 通过。
