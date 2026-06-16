# Steam API 接口规范

更新时间：2026-06-15

本文是本项目后续前后端开发使用 Steam 数据的接口规范。所有 Steam 外部接口必须由后端封装调用，前端只能访问本项目后端 REST API，不能直接请求 Steam，不能接触明文 Steam API Key。

## 1. 权威来源

Steam API 会变化，后续维护必须以官方来源为准：

- Steam Web API 文档：https://partner.steamgames.com/doc/webapi
- Web API 自描述接口：`https://api.steampowered.com/ISteamWebAPIUtil/GetSupportedAPIList/v1/?format=json`
- Steam Tags 文档：https://partner.steamgames.com/doc/store/tags

注意：

- `GetSupportedAPIList` 不带 key 时只返回公开可见的一部分接口。
- 带普通 Web API Key 或 Publisher Key 时，可能返回更多接口。
- Publisher Key 接口不能用于本项目普通用户功能，除非未来项目成为 Steamworks 发行商后台工具。
- Store API 如 `store.steampowered.com/api/appdetails` 常用于商店详情，但它不属于标准 Steamworks Web API，自身稳定性和字段完整性不如正式 Web API，需要后端缓存和容错。

## 2. 项目使用原则

1. 前端禁止保存、传递、拼接 Steam API Key 到 Steam 官方接口。
2. 后端统一通过 `steam-api` 模块封装 Steam 请求。
3. `steam-login` 只负责凭据配置、加密保存、解密读取和有效性校验。
4. Steam API Key 只允许保存在本地加密配置中，例如 `auth.yaml`。
5. 所有 Steam 原始字段入库前必须映射为项目 DTO 或 domain 字段。
6. 对用户隐私敏感接口必须允许失败，不得清空本地已有数据。
7. Store 详情、标签、在线人数等非核心字段必须可异步补全。

## 3. 本项目推荐接入接口

### 3.1 用户凭据验证

#### `ISteamUser/GetPlayerSummaries/v2`

用途：

- 验证 SteamID 与 API Key 是否匹配。
- 获取用户基础资料。

请求：

```text
GET https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/
```

参数：

| 参数 | 必填 | 说明 |
|---|---:|---|
| `key` | 是 | Steam Web API Key，后端保管 |
| `steamids` | 是 | 逗号分隔的 64 位 SteamID |
| `format` | 否 | 推荐 `json` |

可用字段：

| 字段 | 说明 |
|---|---|
| `steamid` | 用户 SteamID |
| `personaname` | 昵称 |
| `profileurl` | 个人资料页 |
| `avatar` | 小头像 |
| `avatarmedium` | 中头像 |
| `avatarfull` | 大头像 |
| `personastate` | 在线状态 |
| `communityvisibilitystate` | 资料可见性 |
| `lastlogoff` | 最近离线时间 |
| `timecreated` | 账号创建时间，可能不存在 |
| `loccountrycode` | 国家/地区，可能不存在 |

项目规范：

- 用于 `steam-login` 在线验证。
- 后端不能记录包含 `key` 的完整 URL。
- 当前验证超时时间读取 `steam.api.timeoutSeconds`。

### 3.2 用户游戏库

#### `IPlayerService/GetOwnedGames/v1`

用途：

- 获取用户拥有的游戏列表。
- 项目核心数据来源。

请求：

```text
GET https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/
```

参数：

| 参数 | 必填 | 说明 |
|---|---:|---|
| `key` | 是 | Steam Web API Key，后端保管 |
| `steamid` | 是 | 64 位 SteamID |
| `include_appinfo` | 否 | 推荐 `1`，返回游戏名和图标字段 |
| `include_played_free_games` | 否 | 推荐 `1`，包含玩过的免费游戏 |
| `appids_filter` | 否 | 限定 appid 列表 |
| `format` | 否 | 推荐 `json` |

可用字段：

| 字段 | 说明 |
|---|---|
| `game_count` | 游戏数量 |
| `games[].appid` | 游戏 AppID |
| `games[].name` | 游戏名，需 `include_appinfo=1` |
| `games[].playtime_forever` | 总游玩时长，单位分钟 |
| `games[].playtime_2weeks` | 最近两周游玩时长，可能不存在 |
| `games[].img_icon_url` | 图标 hash，需自行拼接 CDN URL |
| `games[].has_community_visible_stats` | 是否有公开统计 |
| `games[].rtime_last_played` | 最近游玩时间，可能不存在 |
| `games[].playtime_windows_forever` | Windows 游玩时长，可能不存在 |
| `games[].playtime_mac_forever` | macOS 游玩时长，可能不存在 |
| `games[].playtime_linux_forever` | Linux 游玩时长，可能不存在 |
| `games[].playtime_deck_forever` | Steam Deck 游玩时长，可能不存在 |

项目规范：

- `steam-api` 必须保存 `user_id + appid` 唯一记录。
- 重复同步必须 upsert，不允许产生重复游戏。
- Steam API 失败时返回本地已有数据，不清空数据库。
- 当前必须入库字段：`appid`、`name`、`playtime_forever`、`steam_id`、`user_id`、`last_synced_at`。

### 3.3 最近游玩游戏

#### `IPlayerService/GetRecentlyPlayedGames/v1`

用途：

- 获取用户最近游玩游戏。
- 可用于 Dashboard 最近活跃、推荐排序。

请求：

```text
GET https://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v1/
```

参数：

| 参数 | 必填 | 说明 |
|---|---:|---|
| `key` | 是 | Steam Web API Key |
| `steamid` | 是 | 64 位 SteamID |
| `count` | 否 | 返回数量 |
| `format` | 否 | 推荐 `json` |

常用字段：

| 字段 | 说明 |
|---|---|
| `total_count` | 最近游玩游戏总数 |
| `games[].appid` | 游戏 AppID |
| `games[].name` | 游戏名 |
| `games[].playtime_2weeks` | 最近两周游玩时长 |
| `games[].playtime_forever` | 总游玩时长 |
| `games[].img_icon_url` | 图标 hash |

项目规范：

- 暂不作为 MVP 必需接口。
- 后续可作为 `recent_playtime_2weeks` 补充字段。

### 3.4 当前在线人数

#### `ISteamUserStats/GetNumberOfCurrentPlayers/v1`

用途：

- 获取某个游戏当前在线玩家总数。

请求：

```text
GET https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/
```

参数：

| 参数 | 必填 | 说明 |
|---|---:|---|
| `appid` | 是 | 游戏 AppID |

返回字段：

| 字段 | 说明 |
|---|---|
| `response.player_count` | 当前在线人数 |
| `response.result` | 状态码 |

项目规范：

- 适合异步批量补全。
- 必须缓存，不能每次列表渲染实时请求 Steam。
- 建议缓存时间：5 到 15 分钟。

### 3.5 游戏成就全局百分比

#### `ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2`

用途：

- 获取某游戏各成就的全球完成率。

请求：

```text
GET https://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/
```

参数：

| 参数 | 必填 | 说明 |
|---|---:|---|
| `gameid` | 是 | 游戏 AppID / gameid |

返回字段：

| 字段 | 说明 |
|---|---|
| `achievement.name` | 成就内部名 |
| `achievement.percent` | 全球完成百分比 |

项目规范：

- 非 MVP。
- 可用于游戏详情页，不进入 owned_game 主表。

### 3.6 游戏新闻

#### `ISteamNews/GetNewsForApp/v2`

用途：

- 获取某游戏新闻。

请求：

```text
GET https://api.steampowered.com/ISteamNews/GetNewsForApp/v2/
```

参数：

| 参数 | 必填 | 说明 |
|---|---:|---|
| `appid` | 是 | 游戏 AppID |
| `maxlength` | 否 | 内容最大长度 |
| `enddate` | 否 | 截止时间戳 |
| `count` | 否 | 返回数量 |
| `feeds` | 否 | 新闻源 |
| `format` | 否 | 推荐 `json` |

常用字段：

| 字段 | 说明 |
|---|---|
| `appnews.appid` | AppID |
| `newsitems[].gid` | 新闻 ID |
| `newsitems[].title` | 标题 |
| `newsitems[].url` | 链接 |
| `newsitems[].is_external_url` | 是否外链 |
| `newsitems[].author` | 作者 |
| `newsitems[].contents` | 内容 |
| `newsitems[].feedlabel` | 来源 |
| `newsitems[].date` | 发布时间戳 |

项目规范：

- 非 MVP。
- 新闻内容较长，必须单独建表或缓存，不写入 owned_game。

### 3.7 Steam 等级与徽章

#### `IPlayerService/GetSteamLevel/v1`

用途：

- 获取用户 Steam 等级。

常用字段：

| 字段 | 说明 |
|---|---|
| `response.player_level` | Steam 等级 |

#### `IPlayerService/GetBadges/v1`

用途：

- 获取用户徽章、经验值等信息。

常用字段：

| 字段 | 说明 |
|---|---|
| `response.badges[]` | 徽章列表 |
| `response.player_xp` | 当前经验 |
| `response.player_level` | 当前等级 |
| `response.player_xp_needed_to_level_up` | 距离升级需要经验 |

项目规范：

- 非 MVP。
- 可用于用户概览页面。

### 3.8 好友列表

#### `ISteamUser/GetFriendList/v1`

用途：

- 获取用户好友 SteamID 列表。

参数：

| 参数 | 必填 | 说明 |
|---|---:|---|
| `key` | 是 | Steam Web API Key |
| `steamid` | 是 | 64 位 SteamID |
| `relationship` | 否 | 一般为 `friend` |

常用字段：

| 字段 | 说明 |
|---|---|
| `friendslist.friends[].steamid` | 好友 SteamID |
| `friendslist.friends[].relationship` | 关系 |
| `friendslist.friends[].friend_since` | 成为好友时间 |

项目规范：

- 受隐私设置影响明显。
- 非 MVP，不建议优先实现。

### 3.9 游戏商店详情

#### Store AppDetails

用途：

- 获取游戏名称、简介、开发商、发行商、发行日期、分类、类型、价格、封面、截图等。
- 当前项目已用它补全 `developer`、`publisher`、`releaseDate`、`tags`。

请求：

```text
GET https://store.steampowered.com/api/appdetails?appids={appid}&l=zh-cn
```

参数：

| 参数 | 必填 | 说明 |
|---|---:|---|
| `appids` | 是 | 游戏 AppID，可单个或多个 |
| `l` | 否 | 语言，推荐 `zh-cn` 或 `english` |
| `cc` | 否 | 国家/地区代码，影响价格 |
| `filters` | 否 | 限制返回字段 |

常用字段：

| 字段 | 说明 |
|---|---|
| `{appid}.success` | 是否成功 |
| `{appid}.data.name` | 游戏名 |
| `{appid}.data.type` | 类型，例如 game/dlc |
| `{appid}.data.short_description` | 短简介 |
| `{appid}.data.detailed_description` | 详细简介，可能含 HTML |
| `{appid}.data.header_image` | 头图 |
| `{appid}.data.capsule_image` | 胶囊图 |
| `{appid}.data.website` | 官网 |
| `{appid}.data.developers[]` | 开发商 |
| `{appid}.data.publishers[]` | 发行商 |
| `{appid}.data.release_date.date` | 发行日期 |
| `{appid}.data.release_date.coming_soon` | 是否未发售 |
| `{appid}.data.platforms.windows` | Windows 支持 |
| `{appid}.data.platforms.mac` | macOS 支持 |
| `{appid}.data.platforms.linux` | Linux 支持 |
| `{appid}.data.categories[]` | 功能分类，例如 单人/多人/成就 |
| `{appid}.data.genres[]` | 类型，例如 动作/冒险/RPG |
| `{appid}.data.screenshots[]` | 截图 |
| `{appid}.data.movies[]` | 视频 |
| `{appid}.data.price_overview` | 价格信息 |
| `{appid}.data.metacritic` | Metacritic 信息，可能不存在 |
| `{appid}.data.pc_requirements` | PC 配置要求 |

项目规范：

- 后端必须容错，因为字段可能缺失。
- 该接口不是标准 Web API，不允许作为强一致数据源。
- 分类 `categories` 和类型 `genres` 可以入库，但不能等同于 Steam 商店“用户标签”。
- 当前项目 `owned_game.tags` 可暂存 `categories + genres` 拼接值。
- 真正的用户标签体系参考 Steam Tags 文档，后续建议单独建 `game_tag` 表。

### 3.10 Steam 标签

Steam 标签是商店标签体系，用于搜索、推荐、展示和个性化。官方文档说明标签可由开发者、玩家和版主影响，但没有稳定、完整、面向普通 Web API Key 的“按 AppID 获取全部标签”的标准 Web API。

项目规范：

- MVP 阶段不要把 Store `categories/genres` 命名为真实 Steam Tags。
- 可以在数据库中区分：
  - `genres`：Store 返回的游戏类型。
  - `categories`：Store 返回的功能分类。
  - `tags`：未来真实标签或项目归一化标签。
- 如果短期需要展示标签，允许临时使用 `categories + genres` 生成展示标签，但字段来源必须标注为 `store_metadata`。

## 4. 公开自描述接口清单

以下清单来自 2026-06-15 不带 key 请求：

```text
https://api.steampowered.com/ISteamWebAPIUtil/GetSupportedAPIList/v1/?format=json
```

| Interface | Methods |
|---|---|
| `IAuthenticationService` | `BeginAuthSessionViaCredentials/v1`<br>`BeginAuthSessionViaQR/v1`<br>`GetAuthSessionInfo/v1`<br>`GetAuthSessionRiskInfo/v1`<br>`GetPasswordRSAPublicKey/v1`<br>`NotifyRiskQuizResults/v1`<br>`PollAuthSessionStatus/v1`<br>`UpdateAuthSessionWithMobileConfirmation/v1`<br>`UpdateAuthSessionWithSteamGuardCode/v1` |
| `IBroadcastService` | `PostGameDataFrameRTMP/v1` |
| `IClientStats_1046930` | `ReportEvent/v1` |
| `IContentServerDirectoryService` | `GetCDNForVideo/v1`<br>`GetClientUpdateHosts/v1`<br>`GetDepotPatchInfo/v1`<br>`GetServersForSteamPipe/v1`<br>`PickSingleContentServer/v1` |
| `IGameNotificationsService` | `UserCreateSession/v1`<br>`UserDeleteSession/v1`<br>`UserUpdateSession/v1` |
| `IGCVersion_1046930` | `GetClientVersion/v1`<br>`GetServerVersion/v1` |
| `IGCVersion_1269260` | `GetClientVersion/v1`<br>`GetServerVersion/v1` |
| `IGCVersion_1422450` | `GetClientVersion/v1`<br>`GetServerVersion/v1` |
| `IGCVersion_440` | `GetClientVersion/v1`<br>`GetServerVersion/v1` |
| `IGCVersion_570` | `GetClientVersion/v1`<br>`GetServerVersion/v1` |
| `IGCVersion_583950` | `GetClientVersion/v1`<br>`GetServerVersion/v1` |
| `IGCVersion_730` | `GetServerVersion/v1` |
| `IHelpRequestLogsService` | `GetApplicationLogDemand/v1`<br>`UploadUserApplicationLog/v1` |
| `IPlayerService` | `RecordOfflinePlaytime/v1` |
| `IPortal2Leaderboards_620` | `GetBucketizedData/v1` |
| `IPublishedFileService` | `GetUserVoteSummary/v1` |
| `ISteamApps` | `GetSDRConfig/v1`<br>`GetServersAtAddress/v1`<br>`UpToDateCheck/v1` |
| `ISteamBroadcast` | `PlayerStats/v1`<br>`ViewerHeartbeat/v1` |
| `ISteamDirectory` | `GetCMList/v1`<br>`GetCMListForConnect/v1`<br>`GetSteamPipeDomains/v1` |
| `ISteamNews` | `GetNewsForApp/v2`<br>`GetNewsForApp/v1` |
| `ISteamRemoteStorage` | `GetCollectionDetails/v1`<br>`GetPublishedFileDetails/v1` |
| `ISteamUserOAuth` | `GetTokenDetails/v1` |
| `ISteamUserStats` | `GetGlobalAchievementPercentagesForApp/v2`<br>`GetGlobalAchievementPercentagesForApp/v1`<br>`GetGlobalStatsForGame/v1`<br>`GetNumberOfCurrentPlayers/v1` |
| `ISteamWebAPIUtil` | `GetServerInfo/v1`<br>`GetSupportedAPIList/v1` |
| `IStoreService` | `GetGamesFollowed/v1`<br>`GetGamesFollowedCount/v1`<br>`GetRecommendedTagsForUser/v1` |
| `ITFSystem_440` | `GetWorldStatus/v1` |
| `IWishlistService` | `GetWishlist/v1`<br>`GetWishlistItemCount/v1`<br>`GetWishlistSortedFiltered/v1` |

## 5. 项目数据表建议

### 5.1 当前 `owned_game`

用于用户拥有游戏库：

| 字段 | 来源 |
|---|---|
| `user_id` | 项目用户 |
| `steam_id` | Steam 凭据 |
| `appid` | `GetOwnedGames` |
| `name` | `GetOwnedGames` 或 Store AppDetails |
| `playtime_forever` | `GetOwnedGames` |
| `developer` | Store AppDetails |
| `publisher` | Store AppDetails |
| `release_date` | Store AppDetails |
| `tags` | 临时展示字段，当前可由 `categories + genres` 生成 |
| `last_synced_at` | 项目同步时间 |
| `details_synced_at` | 项目详情补全时间 |

### 5.2 后续推荐新增

`game_metadata`：

- `appid`
- `name`
- `type`
- `short_description`
- `header_image`
- `capsule_image`
- `developers`
- `publishers`
- `release_date`
- `platform_windows`
- `platform_mac`
- `platform_linux`
- `price_currency`
- `price_initial`
- `price_final`
- `discount_percent`
- `metadata_source`
- `metadata_synced_at`

`game_realtime_stats`：

- `appid`
- `player_count`
- `synced_at`

`game_genre`：

- `appid`
- `genre_id`
- `description`
- `source`

`game_category`：

- `appid`
- `category_id`
- `description`
- `source`

`game_news`：

- `appid`
- `gid`
- `title`
- `url`
- `feedlabel`
- `date`
- `contents`

## 6. 后端封装规范

后端建议新增或维护以下接口，前端只调用这些项目接口：

| 项目接口 | Steam 来源 | 说明 |
|---|---|---|
| `POST /api/ownedgames/sync` | `GetOwnedGames` | 同步当前用户游戏库 |
| `GET /api/ownedgames/list` | 本地 DB | 返回本地游戏列表 |
| `GET /api/ownedgames/count` | 本地 DB | 返回本地游戏数量 |
| `POST /api/game-metadata/sync?appid=` | Store AppDetails | 手动补全单个游戏详情 |
| `POST /api/game-metadata/sync-missing` | Store AppDetails | 批量补全缺失详情 |
| `GET /api/game-stats/current-players?appid=` | `GetNumberOfCurrentPlayers` | 查询当前在线人数，必须缓存 |
| `GET /api/game-news?appid=` | `GetNewsForApp` | 查询游戏新闻 |

## 7. 暂不建议接入的接口

以下接口虽然可能出现在官方清单中，但不适合作为本项目普通用户 MVP 功能：

- `IAuthenticationService`：Steam 登录认证内部流程，项目当前不做 Steam OAuth 登录。
- `IBroadcastService`：直播相关。
- `IContentServerDirectoryService`：SteamPipe/CDN 内部能力。
- `IGameNotificationsService`：游戏通知服务，需要特定应用场景。
- `IHelpRequestLogsService`：日志上传/应用日志需求。
- `IPortal2Leaderboards_620`、`ITFSystem_440`、`IGCVersion_*`：特定游戏或内部服务。
- Publisher Key 相关接口：仅发行商后台可用，不进入普通用户产品链路。

## 8. 实现优先级

P0：

1. `GetPlayerSummaries`：凭据验证。
2. `GetOwnedGames`：游戏库同步。
3. Store AppDetails：开发商、发行商、发行日期、封面、分类、类型。

P1：

1. `GetNumberOfCurrentPlayers`：当前在线人数。
2. `GetRecentlyPlayedGames`：最近游玩。
3. Store 图片、价格、平台支持。

P2：

1. `GetNewsForApp`：游戏新闻。
2. `GetGlobalAchievementPercentagesForApp`：成就完成率。
3. `GetSteamLevel` / `GetBadges`：用户概览。

P3：

1. `GetFriendList`：好友关系。
2. Wishlist / Followed Games：愿望单和关注游戏，需确认权限和隐私表现。

## 9. 维护规则

1. 每次新增 Steam 外部接口前，必须先更新本文档。
2. 每个新增接口必须写明：用途、请求参数、返回字段、隐私限制、缓存策略。
3. 每个 Steam API 调用必须有单元测试或集成测试覆盖 JSON 解析。
4. 任何日志不得输出明文 API Key。
5. 任何 Steam API 请求失败都不能导致本地用户数据被删除。
6. 对 Store AppDetails、在线人数、新闻等高频或不稳定接口必须设置缓存。
