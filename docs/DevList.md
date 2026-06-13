# Steam 玩家游戏库后端开发清单

## 目标

根据 `docs/api.md` 实现玩家游戏库后端闭环：

1. 后端从本地凭证中获取 `steamId` 和 Steam Web API Key。
2. 调用 Steam `IPlayerService/GetOwnedGames/v1` 获取玩家游戏列表。
3. 将游戏列表保存到本地数据库。
4. 同步完成后返回最新游戏列表给前端。
5. 支持前端查询游戏列表和游戏总数。

## 当前项目现状

- 前端 API 基地址为 `/api`，Vite 代理到 `http://localhost:8080`。
- 前端已调用：
  - `GET /api/ownedgames/list`
  - `GET /api/ownedgames/count`
- `steam-api` 已存在 `OwnedGamesController`、`SteamApiServiceImpl`、`OwnedGameMapper`、`OwnedGame`。
- 当前 `SteamApiServiceImpl.getOwnedGames()` 已能请求 Steam 并解析 `response.games`。
- 当前 `owned_game` 表没有用户维度。
- 当前 `/api/ownedgames/fetch` 需要传 `steamId` 和 `apiKey`，不适合作为正式接口。
- 当前 `/api/ownedgames/count` 尚未实现。
- `steam-login` 已有本地凭证保存、加密、解密、校验逻辑。

## 推荐总体架构

```text
Frontend
  |
  | POST /api/ownedgames/sync
  | GET  /api/ownedgames/list
  | GET  /api/ownedgames/count
  v
OwnedGamesController
  v
OwnedGameService
  |-- CredentialProvider: 读取 steamId 和解密后的 apiKey
  |-- SteamApiClient: 调用 Steam GetOwnedGames
  |-- OwnedGameMapper: 写入和查询本地数据库
  v
Local Database
```

## 接口设计

### 1. 同步玩家游戏库

```http
POST /api/ownedgames/sync
```

职责：

- 从本地凭证模块读取当前用户 `steamId` 和解密后的 `apiKey`。
- 调用 Steam Web API 获取玩家游戏列表。
- 将游戏列表 upsert 到本地数据库。
- 返回同步后的最新游戏列表给前端。

推荐响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "appid": 730,
      "name": "Counter-Strike 2",
      "playtimeForever": 12845
    }
  ]
}
```

### 2. 查询本地游戏列表

```http
GET /api/ownedgames/list
```

职责：

- 从本地数据库读取当前用户已同步的游戏列表。
- 不直接依赖前端传 `steamId` 或 `apiKey`。

推荐响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "appid": 730,
      "name": "Counter-Strike 2",
      "playtimeForever": 12845
    }
  ]
}
```

### 3. 查询本地游戏总数

```http
GET /api/ownedgames/count
```

职责：

- 从本地数据库统计当前用户游戏数量。

推荐响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "count": 342
  }
}
```

## 数据库设计

默认使用 H2 文件数据库作为项目自带数据库，部署时无需用户额外安装或配置 MySQL。数据库文件由应用自动创建并持久化到本地目录。

推荐本地数据库路径：

```text
./data/steamdb.mv.db
```

推荐 Spring Profile：

| Profile | 数据库 | 用途 |
| :--- | :--- | :--- |
| `local` | H2 File DB | 默认部署方案，项目自带数据库，无需配置 |
| `mysql` | MySQL | 可选服务器部署方案，多用户或正式服务场景使用 |

建议将当前 `owned_game` 表升级为带用户维度的结构，并通过 `schema.sql` 自动初始化。

```sql
CREATE TABLE owned_game (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL DEFAULT 'default',
  steam_id VARCHAR(32) NOT NULL,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  playtime_forever INT NOT NULL DEFAULT 0,
  last_synced_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_owned_game_user_app (user_id, appid)
);
```

字段说明：

| 字段               | 说明                                |
| :----------------- | :---------------------------------- |
| `user_id`          | 本地用户 ID。当前可先使用 `default` |
| `steam_id`         | Steam 64 位用户 ID                  |
| `appid`            | Steam 游戏 AppID                    |
| `name`             | 游戏名称                            |
| `playtime_forever` | 总游玩时长，单位分钟                |
| `last_synced_at`   | 最近一次从 Steam 同步的时间         |

## 后端开发步骤

### Step 1. 统一后端启动入口

目标：确保前端代理到 `localhost:8080` 后，以下接口都能访问：

- `/api/credentials/*`
- `/api/ownedgames/*`

建议：

- 以 `steam-login` 或一个聚合启动模块作为主 Spring Boot 应用。
- 确保扫描范围覆盖 `com.SteamGame.login`、`com.SteamGame.api`、`com.SteamGame.common`。
- 如有需要，在启动类增加：

```java
@ComponentScan("com.SteamGame")
@MapperScan("com.SteamGame.api.mapper")
```

### Step 2. 配置项目自带 H2 文件数据库

目标：部署时默认使用 H2 文件数据库，让项目开箱即用，不要求用户安装或配置 MySQL。

建议新增或调整配置文件：

```text
steam-api/src/main/resources/application-local.yml
```

推荐配置：

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/steamdb;MODE=MySQL;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  sql:
    init:
      mode: always
  h2:
    console:
      enabled: false
```

建议将默认 profile 从 `dev` 调整为 `local`：

```yaml
spring:
  profiles:
    active: local
```

说明：

- `jdbc:h2:file` 表示使用文件数据库，应用重启后数据不会丢失。
- `./data/steamdb` 会在运行目录下生成数据库文件。
- `MODE=MySQL` 用于提高 H2 对 MySQL 语法的兼容性。
- `DATABASE_TO_UPPER=false` 避免 H2 自动将表名和字段名转成大写。
- `AUTO_SERVER=TRUE` 允许本机多进程短暂访问同一数据库文件。
- 部署包只需要携带应用本身，数据库文件可在首次启动时自动创建。

### Step 3. 使用 schema.sql 自动建表

目标：由 Spring Boot 在启动时初始化数据库结构，避免在 Mapper 中执行 `CREATE TABLE`。

建议新增：

```text
steam-api/src/main/resources/schema.sql
```

内容：

```sql
CREATE TABLE IF NOT EXISTS owned_game (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL DEFAULT 'default',
  steam_id VARCHAR(32) NOT NULL,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  playtime_forever INT NOT NULL DEFAULT 0,
  last_synced_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_owned_game_user_app UNIQUE (user_id, appid)
);
```

处理建议：

- 删除或废弃 `OwnedGameMapper.createTableIfNotExists()`。
- 删除或废弃 `OwnedGameMapper.countTable()`。
- 表结构统一交给 `schema.sql` 或后续迁移工具维护。

### Step 4. 抽出 Steam API 客户端

目标：让 Steam 请求逻辑只负责网络请求和响应解析，不负责落库。

建议新增或改造：

```java
public interface SteamApiClient {
    List<OwnedGame> fetchOwnedGames(String steamId, String apiKey);
}
```

实现逻辑：

- 请求地址：`https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/`
- 请求参数：
  - `key`
  - `steamid`
  - `include_appinfo=1`
  - `include_played_free_games=1`
  - `format=json`
- 解析路径：`response.games`
- 字段映射：
  - `appid` -> `appid`
  - `name` -> `name`
  - `playtime_forever` -> `playtimeForever`

### Step 5. 增加凭证提供服务

目标：后端内部获取 `steamId` 和解密后的 `apiKey`，避免前端传敏感信息。

建议新增：

```java
public interface CredentialProvider {
    SteamCredential getCurrentCredential(String userId);
}
```

```java
public class SteamCredential {
    private String userId;
    private String steamId;
    private String apiKey;
}
```

实现来源：

- 优先读取内存会话凭证。
- 其次读取 `auth.yaml`，用现有 `CredentialKeyService` 和 `CryptoUtil.decrypt()` 解密。

注意事项：

- 当前 `rememberMe=false` 分支没有保存真实 apiKey，只保存空字符串。
- 若要支持非持久化凭证同步游戏库，需要将内存会话改成保存加密 apiKey 和 iv。
- MVP 阶段可先要求 `rememberMe=true`，从 `auth.yaml` 解密获取 apiKey。

### Step 6. 新增 OwnedGameService

目标：承接完整业务流程。

建议接口：

```java
public interface OwnedGameService {
    List<OwnedGame> syncOwnedGames(String userId);
    List<OwnedGame> listOwnedGames(String userId);
    int countOwnedGames(String userId);
}
```

`syncOwnedGames()` 流程：

```text
读取当前用户凭证
-> 校验 steamId/apiKey 是否存在
-> 调用 SteamApiClient.fetchOwnedGames()
-> 对返回游戏列表执行批量 upsert
-> 查询数据库最新列表
-> 返回给 Controller
```

### Step 7. 改造 OwnedGameMapper

目标：支持用户维度查询、统计、upsert。

建议方法：

```java
List<OwnedGame> listByUserId(String userId);

int countByUserId(String userId);

void upsert(OwnedGame game);

void batchUpsert(List<OwnedGame> games);
```

默认 H2 文件数据库推荐使用 `MERGE INTO` 或先查后写。

H2 兼容方案：

```sql
MERGE INTO owned_game (
  user_id,
  steam_id,
  appid,
  name,
  playtime_forever,
  last_synced_at
)
KEY (user_id, appid)
VALUES (?, ?, ?, ?, ?, ?);
```

如果启用 MySQL profile，可使用：

```sql
INSERT INTO owned_game(user_id, steam_id, appid, name, playtime_forever, last_synced_at)
VALUES(?, ?, ?, ?, ?, ?)
ON DUPLICATE KEY UPDATE
  steam_id = VALUES(steam_id),
  name = VALUES(name),
  playtime_forever = VALUES(playtime_forever),
  last_synced_at = VALUES(last_synced_at),
  updated_at = CURRENT_TIMESTAMP;
```

### Step 8. 改造 OwnedGamesController

目标：移除正式链路中需要前端传 `apiKey` 的接口。

建议接口：

```java
@PostMapping("/sync")
public ApiResponse<List<OwnedGameDTO>> sync()

@GetMapping("/list")
public ApiResponse<List<OwnedGameDTO>> list()

@GetMapping("/count")
public ApiResponse<Map<String, Integer>> count()
```

处理建议：

- `/sync`：从 Steam 同步，写入数据库，返回最新列表。
- `/list`：只读数据库。
- `/count`：只统计数据库。
- 原 `/fetch?steamId=&apiKey=` 可以保留为开发调试接口，但不要给前端正式使用。

### Step 9. 统一响应结构

目标：和 `docs/api.md` 保持一致。

推荐将 `ApiResponse<T>` 放到 `steam-common`，供 `steam-login` 和 `steam-api` 共用。

结构：

```java
public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;
}
```

成功示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

错误示例：

```json
{
  "code": 401,
  "msg": "未授权或 Token 已过期",
  "data": null
}
```

### Step 10. 前端适配

当前 `getOwnedGames()` 已兼容包装格式和直接数组格式，暂时可不改。

当前 `getGamesCount()` 需要兼容文档中的包装格式：

```ts
return response.data?.data?.count ?? response.data?.count ?? 0;
```

建议新增同步调用：

```ts
export async function syncOwnedGames(): Promise<OwnedGame[]> {
  const response = await http.post("/ownedgames/sync");
  const list = response.data?.data ?? response.data ?? [];
  return list.map(toOwnedGame);
}
```

## 异常处理清单

- 未配置 Steam 凭证：返回 `401` 或业务码 `CONFIG_NOT_FOUND`。
- apiKey 解密失败：返回 `500` 或业务码 `DECRYPT_FAILED`。
- Steam API 超时：返回 `503` 或业务码 `STEAM_API_TIMEOUT`。
- Steam API 返回非 200：返回 `503` 或业务码 `STEAM_API_UNAVAILABLE`。
- 玩家资料私密或无游戏：返回空列表，并给出明确 `msg`。
- 数据库写入失败：返回 `500`，日志记录 appid 和 userId。
- Steam 返回列表为空：允许保存同步时间，并返回空数组。

## MVP 开发优先级

1. 统一 Spring Boot 启动入口和扫描范围。
2. 配置默认 `local` profile，使用 H2 文件数据库作为项目自带数据库。
3. 新增 `schema.sql`，应用启动时自动创建 `owned_game` 表。
4. 新增 `CredentialProvider`，从 `auth.yaml` 解密获得 `steamId/apiKey`。
5. 抽出 `SteamApiClient.fetchOwnedGames()`。
6. 实现 `OwnedGameService.syncOwnedGames()`。
7. 实现 `POST /api/ownedgames/sync`，完成同步后返回前端。
8. 实现 `GET /api/ownedgames/list`。
9. 实现 `GET /api/ownedgames/count`。
10. 前端调整 `getGamesCount()` 包装格式读取。
11. 增加基本测试：Steam 响应解析、Mapper upsert、Controller 返回格式。

## 验收标准

- 配置 Steam 凭证后，后端可以不依赖前端传 `apiKey` 调用 Steam。
- 调用 `POST /api/ownedgames/sync` 后，本地数据库写入玩家游戏列表。
- 同步接口返回最新游戏列表。
- 调用 `GET /api/ownedgames/list` 能从本地数据库返回游戏列表。
- 调用 `GET /api/ownedgames/count` 能返回本地数据库中的游戏数量。
- 前端 Dashboard 和游戏列表页面能正常显示真实数据。
- 重复同步同一个游戏不会产生重复记录。
- Steam API 不可用时不会清空本地已有数据。
- 默认部署不需要安装或配置 MySQL，首次启动会自动创建 H2 数据库文件和表结构。
