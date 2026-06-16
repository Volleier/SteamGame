# SteamGame Steam API 全量开发清单

更新时间：2026-06-15

本文是后续开发执行清单。
接口契约唯一来源：`docs/Api.md`。
外部 Steam 能力来源：`docs/SteamApi.md`。
后续开发顺序必须以本文为准：先补后端能力，再补前端封装，再做页面接入。

## 1. 当前项目接口审视结论

### 1.1 当前已实现的后端项目接口

`steam-login`：

- `POST /api/credentials/configure`
- `GET /api/credentials/status`
- `POST /api/credentials/verify`

`steam-api`：

- `POST /api/ownedgames/sync`
- `GET /api/ownedgames/list`
- `GET /api/ownedgames/count`

`steam-admin`：

- `GET /api/admin/users`
- `GET /api/admin/credentials`
- `GET /api/admin/sync-jobs`
- `POST /api/admin/sync-jobs/trigger`
- `GET /api/admin/game-metadata`
- `POST /api/admin/game-metadata/sync-details`
- `GET /api/admin/system-config`

### 1.2 当前项目已直接调用的 Steam 外部接口

`steam-login/src/main/java/com/SteamGame/login/service/impl/CredentialValidationServiceImpl.java`

- `ISteamUser/GetPlayerSummaries/v2`
- `IPlayerService/GetOwnedGames/v1`

`steam-api/src/main/java/com/SteamGame/api/client/impl/SteamApiClientImpl.java`

- `IPlayerService/GetOwnedGames/v1`
- Store API：`https://store.steampowered.com/api/appdetails`

`steam-api/src/main/java/com/SteamGame/api/service/impl/SteamApiServiceImpl.java`

- `ISteamWebAPIUtil/GetSupportedAPIList/v1`

### 1.3 当前缺失的项目后端接口

`docs/Api.md` 已定义但尚未完整实现：

- `GET /api/game-metadata/{appid}`
- `POST /api/game-metadata/{appid}/sync`
- `POST /api/game-metadata/sync-missing`
- `GET /api/game-stats/current-players`
- `POST /api/game-stats/current-players/batch`
- `GET /api/player/recent-games`
- `GET /api/player/profile`
- `GET /api/player/summary`
- `GET /api/game-news`
- `GET /api/game-achievements/global-percentages`
- `GET /api/games`
- `GET /api/game-taxonomy`
- `GET /api/player/friends`
- `GET /api/player/wishlist`
- `GET /api/health`
- `GET /api/admin/steam-api/supported`

## 2. 总体开发目标

目标是把项目建设成一个后端统一封装 Steam 数据、前端只消费本项目 REST API 的 Steam 游戏数据平台。

必须实现：

1. Steam 凭据配置、加密保存、验证。
2. 用户 Steam 游戏库同步。
3. 游戏商店元数据补全。
4. 当前在线人数查询与缓存。
5. 最近游玩、用户资料、用户概览。
6. 游戏新闻、成就完成率。
7. 游戏列表分页、搜索、排序、筛选。
8. 类型、分类、标签聚合。
9. 好友、愿望单等可选用户社交数据。
10. admin 侧同步、配置、Steam API 能力清单查看。
11. 完整后端接口覆盖 `docs/Api.md`。

## 3. 推荐后端目录结构

### 3.1 `steam-common`

保留和补充公共能力：

```text
steam-common/src/main/java/com/SteamGame/common/response
steam-common/src/main/java/com/SteamGame/common/error
steam-common/src/main/java/com/SteamGame/common/context
steam-common/src/main/java/com/SteamGame/common/util
steam-common/src/main/java/com/SteamGame/common/paging
```

需要新增：

- `common/paging/PageRequest.java`
- `common/paging/PageResult.java`
- `common/error/SteamApiException.java`

职责：统一响应、错误码、当前用户上下文、Steam 凭据接口、通用分页模型。

### 3.2 `steam-login`

保留为凭据和用户上下文模块：

```text
steam-login/src/main/java/com/SteamGame/login/controller
steam-login/src/main/java/com/SteamGame/login/service
steam-login/src/main/java/com/SteamGame/login/service/impl
steam-login/src/main/java/com/SteamGame/login/repository
steam-login/src/main/java/com/SteamGame/login/security
```

职责：配置 SteamID/API Key、加密保存、解密读取、在线验证凭据、提供默认用户上下文。

### 3.3 `steam-api`

建议扩展为完整 Steam 游戏数据模块：

```text
steam-api/src/main/java/com/SteamGame/api/client/steam
steam-api/src/main/java/com/SteamGame/api/client/store
steam-api/src/main/java/com/SteamGame/api/controller
steam-api/src/main/java/com/SteamGame/api/domain/metadata
steam-api/src/main/java/com/SteamGame/api/domain/stats
steam-api/src/main/java/com/SteamGame/api/domain/player
steam-api/src/main/java/com/SteamGame/api/domain/news
steam-api/src/main/java/com/SteamGame/api/domain/achievement
steam-api/src/main/java/com/SteamGame/api/dto/metadata
steam-api/src/main/java/com/SteamGame/api/dto/stats
steam-api/src/main/java/com/SteamGame/api/dto/player
steam-api/src/main/java/com/SteamGame/api/dto/news
steam-api/src/main/java/com/SteamGame/api/dto/achievement
steam-api/src/main/java/com/SteamGame/api/mapper
steam-api/src/main/java/com/SteamGame/api/service
steam-api/src/main/java/com/SteamGame/api/service/impl
steam-api/src/main/java/com/SteamGame/api/config
```

职责：调用 Steam Web API、调用 Steam Store API、保存游戏库和元数据、提供普通用户数据接口。

### 3.4 `steam-admin`

```text
steam-admin/src/main/java/com/SteamGame/admin/controller
steam-admin/src/main/java/com/SteamGame/admin/dto
steam-admin/src/main/java/com/SteamGame/admin/service
```

职责：查看用户、凭据、同步状态，手动触发同步，查看系统配置，查看 Steam API 能力清单。

### 3.5 `steam-launcher`

只保留启动和装配：

```text
steam-launcher/src/main/java/com/SteamGame/launcher/SteamLauncherApplication.java
steam-launcher/src/main/java/com/SteamGame/launcher/startup/ApplicationStartupRunner.java
steam-launcher/src/main/resources/application.yaml
```

职责：唯一后端启动入口、聚合模块、初始化本地 data 目录，不写业务逻辑。

## 4. 数据库开发计划

当前已有：`owned_game`。

### 4.1 扩展 `owned_game`

文件：`steam-api/src/main/resources/schema.sql`

新增字段：

```sql
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_2weeks INT DEFAULT 0;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS rtime_last_played BIGINT;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS img_icon_url VARCHAR(512);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS has_community_visible_stats BOOLEAN;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_windows_forever INT DEFAULT 0;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_mac_forever INT DEFAULT 0;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_linux_forever INT DEFAULT 0;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_deck_forever INT DEFAULT 0;
```

同步修改：

- `OwnedGame.java`
- `OwnedGameMapper.java`
- `OwnedGameDTO.java`
- `OwnedGameDtoConverter.java`

### 4.2 新增 `game_metadata`

用途：保存 Store AppDetails 数据。

```sql
CREATE TABLE IF NOT EXISTS game_metadata (
  appid BIGINT PRIMARY KEY,
  name VARCHAR(512),
  type VARCHAR(64),
  short_description CLOB,
  detailed_description CLOB,
  header_image VARCHAR(1024),
  capsule_image VARCHAR(1024),
  website VARCHAR(1024),
  developers CLOB,
  publishers CLOB,
  release_date VARCHAR(128),
  coming_soon BOOLEAN,
  platform_windows BOOLEAN,
  platform_mac BOOLEAN,
  platform_linux BOOLEAN,
  price_currency VARCHAR(16),
  price_initial INT,
  price_final INT,
  discount_percent INT,
  metacritic_score INT,
  pc_requirements CLOB,
  metadata_source VARCHAR(64),
  metadata_synced_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

新增文件：

- `domain/metadata/GameMetadata.java`
- `dto/metadata/GameMetadataDTO.java`
- `mapper/GameMetadataMapper.java`
- `service/GameMetadataService.java`
- `service/impl/GameMetadataServiceImpl.java`
- `controller/GameMetadataController.java`

### 4.3 新增 `game_category`

```sql
CREATE TABLE IF NOT EXISTS game_category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appid BIGINT NOT NULL,
  category_id INT,
  description VARCHAR(256),
  source VARCHAR(64),
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_game_category_appid ON game_category(appid);
```

### 4.4 新增 `game_genre`

```sql
CREATE TABLE IF NOT EXISTS game_genre (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appid BIGINT NOT NULL,
  genre_id VARCHAR(64),
  description VARCHAR(256),
  source VARCHAR(64),
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_game_genre_appid ON game_genre(appid);
```

### 4.5 新增 `game_realtime_stats`

```sql
CREATE TABLE IF NOT EXISTS game_realtime_stats (
  appid BIGINT PRIMARY KEY,
  player_count INT,
  cached BOOLEAN DEFAULT TRUE,
  stale BOOLEAN DEFAULT FALSE,
  synced_at TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.6 新增 `player_profile`

```sql
CREATE TABLE IF NOT EXISTS player_profile (
  user_id VARCHAR(64) PRIMARY KEY,
  steam_id VARCHAR(32),
  persona_name VARCHAR(256),
  profile_url VARCHAR(1024),
  avatar VARCHAR(1024),
  avatar_medium VARCHAR(1024),
  avatar_full VARCHAR(1024),
  persona_state INT,
  community_visibility_state INT,
  last_logoff BIGINT,
  time_created BIGINT,
  country_code VARCHAR(16),
  synced_at TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.7 新增 `recent_game`

```sql
CREATE TABLE IF NOT EXISTS recent_game (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  playtime_2weeks INT,
  playtime_forever INT,
  icon_url VARCHAR(1024),
  synced_at TIMESTAMP,
  UNIQUE(user_id, appid)
);
```

### 4.8 新增 `game_news`

```sql
CREATE TABLE IF NOT EXISTS game_news (
  gid VARCHAR(128) PRIMARY KEY,
  appid BIGINT NOT NULL,
  title VARCHAR(1024),
  url VARCHAR(2048),
  external_url BOOLEAN,
  author VARCHAR(256),
  contents CLOB,
  feed_label VARCHAR(256),
  date BIGINT,
  synced_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_game_news_appid ON game_news(appid);
```

### 4.9 新增 `game_achievement_global`

```sql
CREATE TABLE IF NOT EXISTS game_achievement_global (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  percent DOUBLE,
  synced_at TIMESTAMP,
  UNIQUE(appid, name)
);
```

### 4.10 新增 `player_friend`

```sql
CREATE TABLE IF NOT EXISTS player_friend (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  friend_steam_id VARCHAR(32) NOT NULL,
  relationship VARCHAR(64),
  friend_since BIGINT,
  synced_at TIMESTAMP,
  UNIQUE(user_id, friend_steam_id)
);
```

### 4.11 新增 `player_wishlist`

```sql
CREATE TABLE IF NOT EXISTS player_wishlist (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  priority INT,
  added_at BIGINT,
  synced_at TIMESTAMP,
  UNIQUE(user_id, appid)
);
```

## 5. Steam Client 开发计划

### 5.1 拆分当前 `SteamApiClient`

当前文件：

```text
steam-api/src/main/java/com/SteamGame/api/client/SteamApiClient.java
steam-api/src/main/java/com/SteamGame/api/client/impl/SteamApiClientImpl.java
```

建议拆分为：

```text
client/steam/SteamWebApiClient.java
client/steam/impl/SteamWebApiClientImpl.java
client/store/SteamStoreClient.java
client/store/impl/SteamStoreClientImpl.java
```

### 5.2 `SteamWebApiClient` 必须实现的方法

```java
PlayerProfileDTO getPlayerSummary(String steamId, String apiKey);
List<OwnedGame> getOwnedGames(String steamId, String apiKey);
List<RecentGameDTO> getRecentlyPlayedGames(String steamId, String apiKey, int count);
CurrentPlayerCountDTO getCurrentPlayers(long appid);
List<AchievementGlobalPercentDTO> getGlobalAchievementPercentages(long appid);
GameNewsResultDTO getNewsForApp(long appid, int count, Integer maxLength);
List<PlayerFriendDTO> getFriendList(String steamId, String apiKey);
SteamApiSupportedListDTO getSupportedApiList(String apiKeyOrNull);
```

可选方法：

```java
SteamLevelDTO getSteamLevel(String steamId, String apiKey);
SteamBadgesDTO getBadges(String steamId, String apiKey);
WishlistDTO getWishlist(String steamId, String apiKey);
```

### 5.3 `SteamStoreClient` 必须实现的方法

```java
GameMetadataDTO getAppDetails(long appid, String language, String countryCode);
Map<Long, GameMetadataDTO> getAppDetailsBatch(List<Long> appids, String language, String countryCode);
```

### 5.4 Client 实现细则

文件：

```text
steam-api/src/main/java/com/SteamGame/api/config/SteamHttpClientConfig.java
```

实现内容：

- 统一 `HttpClient` 或 `RestTemplate` Bean。
- 读取配置：
  - `steam.api.timeoutSeconds`
  - `steam.api.detailsTimeoutSeconds`
  - `steam.api.detailsDelayMillis`
  - `steam.api.maxBatchSize`
  - `steam.api.storeLanguage`
  - `steam.api.storeCountryCode`

所有请求必须：

- 设置超时。
- 不打印包含 `key` 的完整 URL。
- 非 200 返回明确异常。
- JSON 字段缺失时返回空值，不抛 NPE。
- 测试覆盖 JSON 解析。

## 6. 后端接口完整开发目录

以下接口全部以 `docs/Api.md` 为准。

### 6.1 凭据接口

现状：已实现，后续只需完善测试和响应字段。

文件：

```text
steam-login/src/main/java/com/SteamGame/login/controller/CredentialConfigController.java
steam-login/src/main/java/com/SteamGame/login/controller/CredentialVerifyController.java
steam-login/src/main/java/com/SteamGame/login/service/impl/CredentialServiceImpl.java
steam-login/src/main/java/com/SteamGame/login/service/impl/CredentialValidationServiceImpl.java
```

接口：

- `POST /api/credentials/configure`
- `GET /api/credentials/status`
- `POST /api/credentials/verify`

必须补充：

- 凭据验证不能把 Steam API Timeout 显示成凭据无效。
- 前端成功判定只能看 `code == 200`。
- 单测覆盖成功、timeout、无效 SteamID、无效 API Key 格式。

### 6.2 游戏库接口

文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/OwnedGamesController.java
steam-api/src/main/java/com/SteamGame/api/service/OwnedGameService.java
steam-api/src/main/java/com/SteamGame/api/service/impl/OwnedGameServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/mapper/OwnedGameMapper.java
```

接口：

- `POST /api/ownedgames/sync`
- `GET /api/ownedgames/list`
- `GET /api/ownedgames/count`

必须补充：

- `OwnedGameDTO` 增加 `playtime2Weeks`、`rtimeLastPlayed`、`iconUrl`、`hasCommunityVisibleStats`、平台游玩时长。
- `SteamWebApiClient.getOwnedGames()` 解析上述字段。
- Mapper upsert 同步上述字段。

### 6.3 游戏元数据接口

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/GameMetadataController.java
steam-api/src/main/java/com/SteamGame/api/service/GameMetadataService.java
steam-api/src/main/java/com/SteamGame/api/service/impl/GameMetadataServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/mapper/GameMetadataMapper.java
steam-api/src/main/java/com/SteamGame/api/mapper/GameCategoryMapper.java
steam-api/src/main/java/com/SteamGame/api/mapper/GameGenreMapper.java
```

接口：

- `GET /api/game-metadata/{appid}`
- `POST /api/game-metadata/{appid}/sync`
- `POST /api/game-metadata/sync-missing`

Steam 来源：Store `appdetails`。

实现细则：

1. 优先查本地 `game_metadata`。
2. `/sync` 强制请求 Store AppDetails。
3. `/sync-missing` 批量处理缺失数据。
4. `categories` 写入 `game_category`。
5. `genres` 写入 `game_genre`。
6. 图片、价格、平台字段写入 `game_metadata`。

### 6.4 游戏在线人数接口

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/GameStatsController.java
steam-api/src/main/java/com/SteamGame/api/service/GameStatsService.java
steam-api/src/main/java/com/SteamGame/api/service/impl/GameStatsServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/mapper/GameRealtimeStatsMapper.java
steam-api/src/main/java/com/SteamGame/api/dto/stats/CurrentPlayerCountDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/stats/CurrentPlayerBatchRequest.java
steam-api/src/main/java/com/SteamGame/api/dto/stats/CurrentPlayerBatchDTO.java
```

接口：

- `GET /api/game-stats/current-players`
- `POST /api/game-stats/current-players/batch`

Steam 来源：`ISteamUserStats/GetNumberOfCurrentPlayers/v1`。

实现细则：默认读取缓存；缓存未命中或过期才请求 Steam；batch 最大 50；Steam 失败时返回旧缓存并标记 `stale=true`。

### 6.5 玩家资料和最近游玩接口

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/PlayerController.java
steam-api/src/main/java/com/SteamGame/api/service/PlayerProfileService.java
steam-api/src/main/java/com/SteamGame/api/service/RecentGameService.java
steam-api/src/main/java/com/SteamGame/api/service/impl/PlayerProfileServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/service/impl/RecentGameServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/mapper/PlayerProfileMapper.java
steam-api/src/main/java/com/SteamGame/api/mapper/RecentGameMapper.java
steam-api/src/main/java/com/SteamGame/api/dto/player/PlayerProfileDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/player/PlayerSummaryDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/player/RecentGameDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/player/RecentGameResultDTO.java
```

接口：

- `GET /api/player/profile`
- `GET /api/player/summary`
- `GET /api/player/recent-games`

Steam 来源：

- `ISteamUser/GetPlayerSummaries/v2`
- `IPlayerService/GetRecentlyPlayedGames/v1`

实现细则：写入 `player_profile` 和 `recent_game`；`recent-games` count 默认 10，最大 50；`summary` 聚合 profile、ownedGameCount、recentGameCount、totalPlaytimeForever、lastSyncedAt。

### 6.6 新闻和成就接口

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/GameNewsController.java
steam-api/src/main/java/com/SteamGame/api/controller/GameAchievementController.java
steam-api/src/main/java/com/SteamGame/api/service/GameNewsService.java
steam-api/src/main/java/com/SteamGame/api/service/GameAchievementService.java
steam-api/src/main/java/com/SteamGame/api/service/impl/GameNewsServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/service/impl/GameAchievementServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/mapper/GameNewsMapper.java
steam-api/src/main/java/com/SteamGame/api/mapper/GameAchievementGlobalMapper.java
steam-api/src/main/java/com/SteamGame/api/dto/news/GameNewsDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/news/GameNewsResultDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/achievement/AchievementGlobalPercentDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/achievement/AchievementGlobalResultDTO.java
```

接口：

- `GET /api/game-news`
- `GET /api/game-achievements/global-percentages`

Steam 来源：

- `ISteamNews/GetNewsForApp/v2`
- `ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2`

实现细则：新闻按 `gid` 去重；成就按 `appid + name` upsert；都使用本地缓存表。

### 6.7 游戏高级列表和分类聚合接口

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/GamesController.java
steam-api/src/main/java/com/SteamGame/api/controller/GameTaxonomyController.java
steam-api/src/main/java/com/SteamGame/api/service/GameQueryService.java
steam-api/src/main/java/com/SteamGame/api/service/GameTaxonomyService.java
steam-api/src/main/java/com/SteamGame/api/service/impl/GameQueryServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/service/impl/GameTaxonomyServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/dto/GameListQuery.java
steam-api/src/main/java/com/SteamGame/api/dto/GameListItemDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/GameListPageDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/GameTaxonomyDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/TaxonomyDTO.java
```

接口：

- `GET /api/games`
- `GET /api/game-taxonomy`

数据来源：`owned_game`、`game_metadata`、`game_realtime_stats`、`game_genre`、`game_category`。

实现细则：`/api/games` 支持分页、keyword、genre/category、sort/order；`/api/game-taxonomy` 返回 genres、categories、tags 及 count。

### 6.8 好友和愿望单接口

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/service/PlayerFriendService.java
steam-api/src/main/java/com/SteamGame/api/service/PlayerWishlistService.java
steam-api/src/main/java/com/SteamGame/api/service/impl/PlayerFriendServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/service/impl/PlayerWishlistServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/mapper/PlayerFriendMapper.java
steam-api/src/main/java/com/SteamGame/api/mapper/PlayerWishlistMapper.java
steam-api/src/main/java/com/SteamGame/api/dto/player/PlayerFriendDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/player/PlayerFriendResultDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/player/WishlistItemDTO.java
steam-api/src/main/java/com/SteamGame/api/dto/player/WishlistResultDTO.java
```

接口：

- `GET /api/player/friends`
- `GET /api/player/wishlist`

Steam 来源：

- `ISteamUser/GetFriendList/v1`
- `IWishlistService/GetWishlist/v1`
- `IWishlistService/GetWishlistItemCount/v1`

实现细则：受隐私和接口权限影响，失败不能影响主游戏库功能；错误码必须明确。

### 6.9 健康检查和 admin Steam API 能力清单

新增文件：

```text
steam-api/src/main/java/com/SteamGame/api/controller/HealthController.java
steam-admin/src/main/java/com/SteamGame/admin/controller/AdminSteamApiController.java
steam-api/src/main/java/com/SteamGame/api/service/SteamApiCatalogService.java
steam-api/src/main/java/com/SteamGame/api/service/impl/SteamApiCatalogServiceImpl.java
steam-api/src/main/java/com/SteamGame/api/dto/SteamApiSupportedListDTO.java
```

接口：

- `GET /api/health`
- `GET /api/admin/steam-api/supported`

Steam 来源：`ISteamWebAPIUtil/GetSupportedAPIList/v1`。

实现细则：`withKey=false` 不带 key 请求；`withKey=true` 使用本地凭据；返回接口名、方法名、版本、HTTP 方法、参数；不返回 API Key。

## 7. 开发步骤

### Step 1. 统一文档和契约

文件：`docs/SteamApi.md`、`docs/Api.md`、`docs/DevList.md`。

任务：确认 `Api.md` 覆盖所有项目后端接口；确认 `SteamApi.md` 覆盖所有外部 Steam 数据来源；以后新增接口必须先改 `Api.md`。

✅ 验证完成 (2026-06-15)：
- `Api.md` 覆盖已实现接口 13 个 + 规划接口 14 个，共 27 个接口，涵盖凭据、游戏库、元数据、在线人数、玩家资料、新闻、成就、游戏列表、分类聚合、好友、愿望单、健康检查、Admin。
- `SteamApi.md` 覆盖推荐接入接口 10 个（GetPlayerSummaries/GetOwnedGames/GetRecentlyPlayedGames/GetNumberOfCurrentPlayers/GetGlobalAchievementPercentagesForApp/GetNewsForApp/GetSteamLevel/GetBadges/GetFriendList/Store AppDetails），含缓存策略、隐私限制、实现优先级 P0-P3。
- 两份文档与 `DevList.md` 开发清单字段定义完全一致，无遗漏。

### Step 2. 扩展数据库 schema

文件：`steam-api/src/main/resources/schema.sql`。

任务：扩展 `owned_game`；新增所有 `game_*`、`player_*` 表；加索引；保证 H2 可重复执行。

验收：`steam-api\mvnw.cmd -f pom.xml test`。

### Step 3. 新增 domain 和 DTO

文件：`steam-api/src/main/java/com/SteamGame/api/domain/**`、`steam-api/src/main/java/com/SteamGame/api/dto/**`。

任务：为每个表新增 domain；为每个接口新增 DTO；DTO 只暴露前端需要字段。

### Step 4. 拆分 Steam Client

文件：`SteamWebApiClient.java`、`SteamWebApiClientImpl.java`、`SteamStoreClient.java`、`SteamStoreClientImpl.java`、`SteamHttpClientConfig.java`。

任务：从旧 `SteamApiClientImpl` 拆出 Web API 和 Store API；保留旧接口适配并逐步迁移 Service；每个外部接口都有独立解析方法。

### Step 5. 完善凭据和用户资料

任务：凭据验证继续用 `GetPlayerSummaries` + `GetOwnedGames`；新增 `/api/player/profile`；新增 `/api/player/summary`。

### Step 6. 扩展游戏库同步字段

任务：解析 `GetOwnedGames` 全部可用字段；入库平台游玩时长、最近游玩时间、图标 hash；DTO 输出扩展字段。

### Step 7. 实现游戏元数据

任务：实现 AppDetails 单个同步；实现缺失元数据批量同步；保存图片、价格、平台、类型、分类。

### Step 8. 实现在线人数

任务：实现单个当前在线人数；实现批量当前在线人数；加缓存和过期策略。

### Step 9. 实现最近游玩

任务：调用 `GetRecentlyPlayedGames`；写入 `recent_game`；返回 `RecentGameResultDTO`。

### Step 10. 实现新闻和成就

任务：实现 `GetNewsForApp`；实现 `GetGlobalAchievementPercentagesForApp`；写入对应缓存表。

### Step 11. 实现高级游戏列表和分类聚合

任务：实现 `/api/games`；支持搜索、筛选、排序、分页；实现 `/api/game-taxonomy`。

### Step 12. 实现好友和愿望单

任务：实现好友列表；实现愿望单；处理隐私和不可用场景。

### Step 13. 完善 admin

任务：新增 `/api/admin/steam-api/supported`；admin 同步任务支持元数据、在线人数、新闻刷新；system-config 返回所有 Steam API 配置。

### Step 14. 前端 API 封装

文件：

```text
vue/src/api/credentials.ts
vue/src/api/games.ts
vue/src/api/player.ts
vue/src/api/gameMetadata.ts
vue/src/api/gameStats.ts
vue/src/api/gameNews.ts
vue/src/api/gameAchievements.ts
vue/src/api/admin.ts
```

任务：所有接口按 `docs/Api.md` 封装；成功判定统一 `code === 200`；不再直接假设旧字段 `success`。

验收：`cd vue && npm run build`。

### Step 15. 测试体系

新增测试：

```text
steam-api/src/test/java/com/SteamGame/api/client/steam/SteamWebApiClientImplTest.java
steam-api/src/test/java/com/SteamGame/api/client/store/SteamStoreClientImplTest.java
steam-api/src/test/java/com/SteamGame/api/mapper/GameMetadataMapperTest.java
steam-api/src/test/java/com/SteamGame/api/mapper/GameStatsMapperTest.java
steam-api/src/test/java/com/SteamGame/api/controller/GameMetadataControllerTest.java
steam-api/src/test/java/com/SteamGame/api/controller/GameStatsControllerTest.java
steam-api/src/test/java/com/SteamGame/api/controller/PlayerControllerTest.java
steam-api/src/test/java/com/SteamGame/api/controller/GamesControllerTest.java
```

测试要求：每个 Steam JSON parser 必须有测试；每个 Mapper 覆盖 upsert 和 userId 隔离；每个 Controller 验证 DTO 字段；测试不能依赖真实 Steam 网络。

最终验收：

```bash
steam-api\mvnw.cmd -f pom.xml test
cd vue
npm run build
```

## 8. 优先级

P0：保持现有凭据和游戏库同步稳定；扩展 owned_game 字段；实现 game_metadata；实现 `/api/games`。

P1：当前在线人数；最近游玩；用户资料和用户概览；分类、类型聚合。

P2：游戏新闻；成就完成率；admin Steam API 能力清单。

P3：好友列表；愿望单；Steam 等级、徽章。

## 9. 完成标准

全部完成后必须满足：

1. `docs/Api.md` 中所有接口都有后端实现。
2. `docs/SteamApi.md` 中推荐接入的 Steam 数据都有封装 Client。
3. 前端所有请求都走 `vue/src/api/**`。
4. 前端不接触明文 Steam API Key。
5. 后端日志不输出明文 Steam API Key。
6. Steam 外部接口失败不清空本地数据。
7. H2 schema 可重复初始化。
8. Maven 测试通过。
9. Vue build 通过。
