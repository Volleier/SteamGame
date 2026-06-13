# SteamGame 第一阶段开发清单

本文档定义当前项目的第一阶段开发目标：先在现有模块结构上完成可运行、可验收的 Steam 游戏库 MVP。完成本文档后，再执行 `docs/OptimizeList.md` 中的推荐架构重构。

执行顺序：

```text
1. DevList.md
   -> 先跑通当前项目 MVP
   -> 少做模块大搬迁
   -> 保证前后端真实可用

2. OptimizeList.md
   -> 再按 ModuleIntro.md 做推荐架构重构
   -> 调整模块边界
   -> 增加 admin 和公共基础设施
```

## 一、第一阶段目标

第一阶段只解决“平台能获取 Steam 游戏库并展示”的核心闭环。

必须完成：

- 后端通过 `steam-launcher` 启动。
- 前端能配置 Steam 凭据。
- 后端能从本地凭据读取 `steamId` 和解密后的 API Key。
- 后端能调用 Steam `IPlayerService/GetOwnedGames/v1`。
- 后端能把游戏库写入 H2 本地数据库。
- 前端能显示游戏数量和游戏列表。
- 重复同步不产生重复数据。
- Steam API 失败时不清空本地已有数据。

第一阶段暂不做：

- 不重构五模块最终依赖关系。
- 不移动 `CredentialProvider` 到 `steam-common`。
- 不建设完整 `steam-admin`。
- 不删除所有模块启动类。
- 不实现真实多用户权限系统。
- 不把所有公共错误体系一次性重构到 `steam-common`。

这些内容进入第二阶段 `docs/OptimizeList.md`。

## 二、当前项目状态

### 已存在接口

文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/OwnedGamesController.java
```

已有接口：

- `POST /api/ownedgames/sync`
- `GET /api/ownedgames/list`
- `GET /api/ownedgames/count`
- `GET /api/ownedgames/fetch` 调试接口

### 已存在前端调用

文件：

```text
vue/src/api/games.ts
```

已有方法：

- `getOwnedGames()`
- `getGamesCount()`
- `syncOwnedGames()`

前端 `baseURL` 是 `/api`，Vite 代理到 `http://localhost:8080`。

### 已存在 Steam 拉取能力

文件：

```text
steam-api/src/main/java/com/SteamGame/api/client/impl/SteamApiClientImpl.java
```

已有能力：

- 调用 Steam `GetOwnedGames`。
- 解析 `appid`、`name`、`playtime_forever`。
- 调用 Steam Store `appdetails` 补全开发商、发行商、发行日期、标签。

### 已存在入库能力

文件：

```text
steam-api/src/main/java/com/SteamGame/api/mapper/OwnedGameMapper.java
steam-api/src/main/resources/schema.sql
```

已有能力：

- H2 `owned_game` 表。
- `MERGE INTO` upsert。
- 按 `user_id` 查询列表。
- 按 `user_id` 统计数量。
- 更新详情字段。

### 当前主要问题

- `OwnedGameServiceImpl` 当前在 `steam-login`，模块边界不理想，但第一阶段可以先不移动。
- `/fetch?steamId=&apiKey=` 暴露敏感参数，应限制或删除。
- 返回实体包含内部字段，建议第一阶段先通过 DTO 收敛。
- `updateDetails()` 只按 `appid` 更新，后续多用户会串数据。
- `listMissingDetails()` 扫全表，后续数据量大时有风险。
- 后台详情补全使用裸 `CompletableFuture.runAsync()`，不受 Spring 管理。
- 没有端到端验收记录。

## 三、第一阶段架构边界

第一阶段接受当前聚合启动方式：

```text
vue
  -> steam-launcher
       -> steam-login
       -> steam-api
       -> steam-common
```

第一阶段明确：

- `steam-launcher` 是唯一正式启动入口。
- `steam-api` 单独启动不作为验收目标。
- `steam-admin` 暂不参与核心闭环。
- `steam-test` 暂不处理，第二阶段再迁移或删除。

## 四、开发步骤

### Step 1. 固定 steam-launcher 为唯一启动入口

目标：

- 开发和验收都通过 `steam-launcher` 启动后端。
- 避免纠结 `steam-api` 单独启动时 Bean 不完整的问题。

涉及文件：

```text
Build.bat
steam-launcher/src/main/java/com/SteamGame/steamLauncher/SteamLauncherApplication.java
steam-launcher/src/main/resources/application.yaml
Readme.md
docs/DevList.md
```

实现内容：

- 确认 `Build.bat` 启动的是：

```text
steam-launcher/target/steam-launcher-0.0.1.jar
```

- 保持 `SteamLauncherApplication` 中：

```java
@ComponentScan("com.SteamGame")
@MapperScan("com.SteamGame.api.mapper")
```

- 在 `Readme.md` 中补充启动方式：

```bash
steam-api\mvnw.cmd -f pom.xml -DskipTests package
java -Dfile.encoding=UTF-8 -jar steam-launcher\target\steam-launcher-0.0.1.jar
```

验收：

- 后端启动后监听 `8080`。
- `GET /api/ownedgames/count` 能返回 JSON。

### Step 2. 确认 H2 数据库配置稳定

目标：

- 项目开箱即用，不依赖用户安装 MySQL。
- H2 数据库文件固定保存到项目 `data` 目录。

涉及文件：

```text
steam-launcher/src/main/resources/application.yaml
steam-api/src/main/resources/application.yml
steam-api/src/main/resources/application-local.yml
steam-api/src/main/resources/schema.sql
```

实现内容：

- 第一阶段主配置以 `steam-launcher/src/main/resources/application.yaml` 为准。
- 配置：

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/steamdb;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  sql:
    init:
      mode: always
```

- 保留 `schema.sql` 自动建表。
- 确认启动时 `data` 目录存在。

验收：

- 首次启动能自动创建 `data/steamdb.mv.db`。
- `owned_game` 表存在。

### Step 3. 收敛 Owned Games 返回 DTO

目标：

- `/api/ownedgames/list` 和 `/sync` 返回字段严格匹配 `docs/Api.md`。
- 不直接返回数据库实体内部字段。

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/dto/OwnedGameDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/OwnedGameDtoConverter.java
steam-api/src/main/java/com/SteamGame/api/dto/OwnedGameCountDTO.java
```

`OwnedGameDTO` 实现字段：

```java
private Long appid;
private String name;
private Integer playtimeForever;
private String developer;
private String publisher;
private String releaseDate;
private String tags;
```

`OwnedGameCountDTO` 实现字段：

```java
private int count;
```

`OwnedGameDtoConverter` 实现方法：

```java
public OwnedGameDTO toDto(OwnedGame game)
public List<OwnedGameDTO> toDtoList(List<OwnedGame> games)
```

修改文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/OwnedGamesController.java
```

修改内容：

- `/sync` 返回 `ApiResponse<List<OwnedGameDTO>>`。
- `/list` 返回 `ApiResponse<List<OwnedGameDTO>>`。
- `/count` 返回 `ApiResponse<OwnedGameCountDTO>` 或继续返回 `Map.of("count", count)`，二者前端都兼容；推荐使用 DTO。

验收：

- `/api/ownedgames/list` 不再返回 `id`、`userId`、`steamId`、`createdAt`、`updatedAt`。
- 前端游戏列表仍能正常渲染。

### Step 4. 限制或删除 `/fetch` 调试接口

目标：

- 避免通过 URL 传递 Steam API Key。

涉及文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/OwnedGamesController.java
```

推荐实现：

- 第一阶段直接删除 `fetchAndList()`。

可接受实现：

- 加 `@Profile("dev")`。
- 日志中不能打印包含 API Key 的完整 URL。

验收：

- 默认启动时 `/api/ownedgames/fetch` 不作为正式接口使用。
- 前端不依赖该接口。

### Step 5. 修正 SteamApiClient 日志和 userId 职责

目标：

- Steam Client 只负责请求和解析，不负责用户归属。
- 日志不泄露 API Key。

涉及文件：

```text
steam-api/src/main/java/com/SteamGame/api/client/impl/SteamApiClientImpl.java
```

实现内容：

- 不打印完整 Steam URL，至少不要打印 `key` 参数。
- `parseGamesFromJson()` 不再固定：

```java
game.setUserId("default");
```

- `userId` 由业务服务统一设置。
- HTTP 非 200 时记录状态码，并返回明确异常或空列表策略。

验收：

- 日志中搜索不到明文 API Key。
- 同步后 `owned_game.user_id` 仍正确写入 `default`。

### Step 6. 修正 OwnedGameServiceImpl 的用户赋值

目标：

- 第一阶段不移动文件，但修正业务行为。

当前文件：

```text
steam-login/src/main/java/com/SteamGame/login/service/impl/OwnedGameServiceImpl.java
```

实现内容：

- 在 `syncOwnedGames(String userId)` 中统一设置：

```java
game.setUserId(uid);
game.setSteamId(credential.getSteamId());
game.setLastSyncedAt(now);
```

- Steam API 失败时保留当前策略：

```text
返回本地已有数据，不清空数据库
```

- 无有效凭据时推荐抛出明确异常；若暂不引入统一异常，至少返回空列表并在 Controller msg 中体现原因。

验收：

- 同步写入的记录 `user_id = default`。
- 重复同步不新增重复记录。

### Step 7. 修正详情补全的 userId 维度

目标：

- 防止未来多用户时详情更新串数据。

涉及文件：

```text
steam-api/src/main/java/com/SteamGame/api/mapper/OwnedGameMapper.java
steam-login/src/main/java/com/SteamGame/login/service/impl/OwnedGameServiceImpl.java
```

修改 Mapper：

```java
List<OwnedGame> listMissingDetailsByUserId(String userId, int limit);

void updateDetails(
    String userId,
    Long appid,
    String developer,
    String publisher,
    String releaseDate,
    String tags
);
```

SQL 要求：

```sql
WHERE user_id = #{userId}
  AND appid = #{appid}
```

修改 Service：

- `triggerBackgroundDetailsFetch()` 接收 `userId`。
- 只补全当前用户缺失详情。
- 设置 limit，例如 `100`。

验收：

- `updateDetails` 不再只按 `appid` 更新。
- `listMissingDetails` 不再扫全表。

### Step 8. 让详情补全任务可控

目标：

- 第一阶段可以不完整重构线程池，但要避免重复无限任务和扫全表。

涉及文件：

```text
steam-login/src/main/java/com/SteamGame/login/service/impl/OwnedGameServiceImpl.java
```

实现内容：

- 保留 `AtomicBoolean isFetchingDetails`。
- `triggerBackgroundDetailsFetch(userId)` 每次只处理有限数量。
- 每个 appdetails 请求之间保留延迟。
- 捕获异常，不影响 `/sync` 主链路。

可选新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/config/GameTaskConfig.java
```

如果第一阶段有时间，可引入 Spring `ThreadPoolTaskExecutor`；如果时间紧，留到 `OptimizeList.md`。

验收：

- 同步接口不会因为详情补全阻塞过久。
- 详情补全失败不影响基础游戏列表展示。

### Step 9. 明确 `/sync` 第一阶段响应策略

目标：

- 统一文档和实际行为。

涉及文件：

```text
docs/Api.md
docs/DevList.md
vue/src/components/SyncModal.vue
vue/src/views/Dashboard/Settings.vue
```

第一阶段策略：

- `/sync` 必须同步基础游戏库。
- `/sync` 返回同步后的本地列表。
- `developer`、`publisher`、`releaseDate`、`tags` 允许为空。
- 详情字段由后台任务补全。
- 前端继续显示 `Unknown`。

验收：

- 文档说明详情可能异步补全。
- 前端不因详情为空报错。

### Step 10. 端到端验收

目标：

- 证明第一阶段真的可用。

前置条件：

- `auth.yaml` 中存在有效 `steamId` 和加密后的 API Key。

构建：

```bash
steam-api\mvnw.cmd -f pom.xml -DskipTests package
```

启动后端：

```bash
java -Dfile.encoding=UTF-8 -jar steam-launcher\target\steam-launcher-0.0.1.jar
```

启动前端：

```bash
cd vue
npm run dev
```

接口验收：

```bash
curl -X POST http://localhost:8080/api/ownedgames/sync
curl http://localhost:8080/api/ownedgames/list
curl http://localhost:8080/api/ownedgames/count
```

前端验收：

- Dashboard `04_INVENTORY` 显示真实游戏数量。
- 游戏列表页显示真实游戏。
- 设置页手动同步可用。

### Step 11. 补充第一阶段最小测试

目标：

- 不追求完整测试体系，但覆盖最容易回归的点。

新增测试：

```text
steam-api/src/test/java/com/SteamGame/api/client/SteamApiClientImplTest.java
steam-api/src/test/java/com/SteamGame/api/mapper/OwnedGameMapperTest.java
steam-launcher/src/test/java/com/SteamGame/steamLauncher/SteamLauncherApplicationTests.java
```

测试内容：

- Steam JSON 能解析出 `appid/name/playtime_forever`。
- 同一 `user_id + appid` upsert 不重复。
- `updateDetails` 必须带 `user_id`。
- Spring context 能启动。

验收：

```bash
steam-api\mvnw.cmd -f pom.xml test
```

通过或至少记录无法通过的原因。

## 五、第一阶段完成标准

满足以下条件后，才进入 `docs/OptimizeList.md`：

- `steam-launcher` 能启动完整后端。
- 前端能完成 Steam 凭据配置。
- `POST /api/ownedgames/sync` 能从 Steam 拉取游戏。
- 游戏能写入 H2 `owned_game` 表。
- `GET /api/ownedgames/list` 能返回本地游戏列表。
- `GET /api/ownedgames/count` 能返回本地游戏数量。
- 前端 Dashboard 和游戏列表页能展示真实数据。
- 返回给前端的是 DTO，不暴露实体内部字段。
- 日志不输出明文 API Key。
- 默认正式接口不暴露 `/fetch?apiKey=`。
- 重复同步不产生重复记录。
- 详情字段为空时前端正常显示。
- Steam API 失败时本地已有数据不被清空。

## 六、进入 OptimizeList 前的保留问题

以下问题第一阶段可以不彻底解决，但必须记录，进入 `OptimizeList.md`：

- `OwnedGameServiceImpl` 仍在 `steam-login`，需要迁回 `steam-api`。
- `CredentialProvider` 和 `SteamCredential` 仍可能在 `steam-api`，需要下沉到 `steam-common`。
- `steam-login` 对 `steam-api` 的依赖需要解除。
- `steam-admin` 需要修正包名和职责。
- `steam-launcher` 需要简化为纯启动模块。
- 重复 `ApiResponse` 需要统一。
- 旧启动类需要清理。
- `steam-test` 需要迁移有价值测试后删除。
- 详情补全任务需要改为 Spring 管理线程池。
- 需要建立完整单元测试和集成测试体系。

## 七、与 OptimizeList 的关系

`DevList.md` 是第一阶段：

```text
目标：当前项目可运行、可同步、可展示
策略：少改模块结构，优先闭环
```

`OptimizeList.md` 是第二阶段：

```text
目标：调整到最推荐长期架构
策略：重构模块边界，统一 common，建设 admin，清理历史代码
```

不要跳过 `DevList.md` 直接执行 `OptimizeList.md`。否则容易在基础闭环尚未稳定时引入大量模块迁移风险。
