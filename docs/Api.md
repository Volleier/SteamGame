# SteamGame API 统一规范

更新时间：2026-06-15

本文是本项目前后端唯一接口规范。以后前端、后端、联调、测试都以本文为准。  
外部 Steam 接口不写在这里，统一参考 [SteamApi.md](./SteamApi.md)。

## 1. 总原则

1. 前端只调用本项目后端 `/api/**` 接口。
2. 后端禁止把 Steam API Key 暴露给前端。
3. 所有接口统一返回 `ApiResponse<T>`。
4. 前端判断成功以 `code == 200` 为准，不以 HTTP 状态码为准。
5. 业务错误也尽量返回 HTTP 200，错误信息放在 `code/msg`。
6. 未登录阶段默认 `userId = default`。
7. 后续新增接口必须先补本文，再写代码。

## 2. 通用响应

### 2.1 统一结构

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

### 2.2 字段说明

| 字段 | 类型 | 说明 |
|---|---|---|
| `code` | number | 业务状态码 |
| `msg` | string | 业务提示信息 |
| `data` | any | 返回数据 |

### 2.3 成功判定

- 成功：`code === 200`
- 失败：`code !== 200`

### 2.4 前端兼容规则

前端必须同时兼容：

- `msg`
- 旧字段 `message`
- `data`

但规范上只承认 `code/msg/data`。

## 3. 通用错误码

| code | 名称 | 含义 |
|---|---|---|
| 200 | SUCCESS | 成功 |
| 400 | BAD_REQUEST | 请求参数错误 |
| 401 | UNAUTHORIZED | 未授权 |
| 404 | NOT_FOUND | 资源不存在 |
| 500 | INTERNAL_ERROR | 服务端内部错误 |
| 1001 | STEAM_CREDENTIAL_NOT_FOUND | Steam 凭据未配置 |
| 1002 | STEAM_CREDENTIAL_INVALID | Steam 凭据无效 |
| 2001 | STEAM_API_TIMEOUT | Steam 接口超时 |
| 2002 | STEAM_API_UNAVAILABLE | Steam 接口不可用 |
| 3001 | GAME_SYNC_FAILED | 游戏同步失败 |

## 4. 统一数据模型

### 4.1 当前用户

```json
{
  "userId": "default",
  "username": "User",
  "admin": false
}
```

### 4.2 凭据状态

```json
{
  "steamId": "7656119xxxxxxxxxx",
  "hasApiKey": true,
  "persisted": true,
  "validationStatus": "VALID",
  "updatedAt": "2026-06-15T10:00:00+08:00",
  "lastValidatedAt": "2026-06-15T10:00:00+08:00"
}
```

### 4.3 凭据验证结果

```json
{
  "validKeyAndUser": true,
  "ownsGames": true,
  "profilePrivate": false,
  "message": "验证成功",
  "gameCount": 364
}
```

### 4.4 拥有游戏

```json
{
  "appid": 730,
  "name": "Counter-Strike 2",
  "playtimeForever": 1234,
  "developer": "Valve",
  "publisher": "Valve",
  "releaseDate": "2023-09-27",
  "tags": "Shooter,Action"
}
```

### 4.5 游戏数量

```json
{
  "count": 364
}
```

### 4.6 同步结果

```json
{
  "total": 364,
  "saved": 364,
  "games": [],
  "detailsInProgress": true
}
```

### 4.7 管理端系统配置

```json
{
  "steamApiTimeoutSeconds": 15,
  "detailsTimeoutSeconds": 6,
  "detailsDelayMillis": 1500,
  "configPath": "auth.yaml",
  "credentialRevalidateHours": 6
}
```

## 5. 当前已实现接口

### 5.1 凭据配置

#### `POST /api/credentials/configure`

用途：

- 保存 SteamID 和 API Key
- 在线验证凭据
- 选择是否持久化到 `auth.yaml`

请求体：

```json
{
  "steamId": "7656119xxxxxxxxxx",
  "apiKey": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "rememberMe": true
}
```

字段：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `steamId` | string | 是 | 17 位 SteamID |
| `apiKey` | string | 是 | Steam Web API Key |
| `rememberMe` | boolean | 是 | 是否写入本地配置 |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "validKeyAndUser": true,
    "ownsGames": true,
    "profilePrivate": false,
    "message": "验证成功",
    "gameCount": 364
  }
}
```

失败响应示例：

```json
{
  "code": 2001,
  "msg": "Steam API 请求超时，请稍后重试",
  "data": null
}
```

说明：

- `code=200` 代表成功。
- 成功后，`data.validKeyAndUser` 必须为 `true`。
- 若 `rememberMe=true`，后端写入加密配置。

---

### 5.2 凭据状态

#### `GET /api/credentials/status`

用途：

- 获取当前用户的凭据状态
- 用于凭据页初始化

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "steamId": "7656119xxxxxxxxxx",
    "hasApiKey": true,
    "persisted": true,
    "validationStatus": "VALID",
    "updatedAt": "2026-06-15T10:00:00+08:00",
    "lastValidatedAt": "2026-06-15T10:00:00+08:00"
  }
}
```

---

### 5.3 凭据重新验证

#### `POST /api/credentials/verify`

用途：

- 读取当前用户已保存的凭据并重新在线验证

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "validKeyAndUser": true,
    "ownsGames": true,
    "profilePrivate": false,
    "message": "验证成功",
    "gameCount": 364
  }
}
```

---

### 5.4 同步拥有游戏

#### `POST /api/ownedgames/sync`

用途：

- 从 Steam 拉取当前用户游戏库
- 写入本地数据库
- 触发后台详情补全

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "appid": 730,
      "name": "Counter-Strike 2",
      "playtimeForever": 1234,
      "developer": null,
      "publisher": null,
      "releaseDate": null,
      "tags": null
    }
  ]
}
```

说明：

- `sync` 返回的是同步后的本地列表。
- 详情字段允许为空。
- Steam 失败时不得清空本地列表。

---

### 5.5 游戏列表

#### `GET /api/ownedgames/list`

用途：

- 获取当前用户本地游戏列表

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "appid": 730,
      "name": "Counter-Strike 2",
      "playtimeForever": 1234,
      "developer": "Valve",
      "publisher": "Valve",
      "releaseDate": "2023-09-27",
      "tags": "Shooter,Action"
    }
  ]
}
```

---

### 5.6 游戏数量

#### `GET /api/ownedgames/count`

用途：

- 获取当前用户本地游戏总数

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "count": 364
  }
}
```

---

## 6. 管理端接口

管理端接口仅供后台管理视图或调试使用。后续如接入权限体系，可统一加鉴权。

### 6.1 用户管理

#### `GET /api/admin/users`

用途：

- 查看当前用户占位数据

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "userId": "default",
    "username": "User",
    "admin": false,
    "steamIdBound": true
  }
}
```

---

### 6.2 凭据管理

#### `GET /api/admin/credentials`

用途：

- 查看凭据配置状态
- 不返回明文 API Key

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "configured": true,
    "steamId": "7656119xxxxxxxxxx",
    "hasApiKey": true,
    "updatedAt": "2026-06-15T10:00:00+08:00"
  }
}
```

---

### 6.3 同步任务管理

#### `GET /api/admin/sync-jobs`

用途：

- 查看同步任务状态
- 查看游戏数量

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "serviceAvailable": true,
    "ownedGameCount": 364
  }
}
```

#### `POST /api/admin/sync-jobs/trigger`

用途：

- 手动触发用户同步

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 364,
    "saved": 364,
    "gameCount": 364,
    "detailsInProgress": true
  }
}
```

---

### 6.4 游戏元数据管理

#### `GET /api/admin/game-metadata`

用途：

- 查看元数据补全服务是否可用

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "detailsServiceAvailable": true
  }
}
```

#### `POST /api/admin/game-metadata/sync-details`

用途：

- 手动触发当前用户的缺失详情补全

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "updated": 50
  }
}
```

---

### 6.5 系统配置

#### `GET /api/admin/system-config`

用途：

- 查看系统配置

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "steamApiTimeoutSeconds": 15,
    "detailsTimeoutSeconds": 6,
    "detailsDelayMillis": 1500,
    "configPath": "auth.yaml",
    "credentialRevalidateHours": 6
  }
}
```

---

## 7. 前端对接规则

### 7.1 请求基础地址

- 开发环境：`/api`
- 由 Vite 代理转发到后端

### 7.2 前端判断规则

- 成功只看 `code === 200`
- 弹窗错误信息优先用 `msg`
- 若 `msg` 为空，再退回历史字段 `message`

### 7.3 数据处理

- 列表页只认 `OwnedGameDTO`
- 不直接渲染数据库实体
- 详情字段为空时前端显示 `Unknown`

### 7.4 用户范围

- 当前阶段所有接口默认作用于 `default`
- 后续接入真实登录后，`userId` 来源统一改为当前用户上下文

## 8. 规划接口规范

本节接口是后续开发必须遵守的预定义契约。即使当前代码尚未实现，后续实现也必须按本节开发。

### 8.1 游戏元数据详情

#### `GET /api/game-metadata/{appid}`

用途：

- 获取单个游戏的商店元数据。

Path：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `appid` | number | 是 | Steam AppID |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "appid": 730,
    "name": "Counter-Strike 2",
    "type": "game",
    "shortDescription": "For over two decades...",
    "headerImage": "https://...",
    "capsuleImage": "https://...",
    "developers": ["Valve"],
    "publishers": ["Valve"],
    "releaseDate": "2023-09-27",
    "comingSoon": false,
    "platforms": {
      "windows": true,
      "mac": false,
      "linux": true
    },
    "price": {
      "currency": "CNY",
      "initial": 0,
      "final": 0,
      "discountPercent": 0
    },
    "categories": [
      {
        "id": 2,
        "description": "Single-player"
      }
    ],
    "genres": [
      {
        "id": "1",
        "description": "Action"
      }
    ],
    "screenshots": [
      {
        "id": 0,
        "pathThumbnail": "https://...",
        "pathFull": "https://..."
      }
    ],
    "metadataSource": "store_appdetails",
    "metadataSyncedAt": "2026-06-15T10:00:00+08:00"
  }
}
```

#### `POST /api/game-metadata/{appid}/sync`

用途：

- 手动同步单个游戏商店元数据。

成功响应：同 `GET /api/game-metadata/{appid}`。

#### `POST /api/game-metadata/sync-missing`

用途：

- 批量同步缺失元数据的游戏。

请求体：

```json
{
  "userId": "default",
  "limit": 50
}
```

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "requested": 50,
    "updated": 42,
    "failed": 8
  }
}
```

---

### 8.2 当前在线人数

#### `GET /api/game-stats/current-players`

用途：

- 查询某个游戏当前在线人数。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `appid` | number | 是 | Steam AppID |
| `forceRefresh` | boolean | 否 | 是否绕过缓存，默认 `false` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "appid": 730,
    "playerCount": 945321,
    "cached": true,
    "syncedAt": "2026-06-15T10:00:00+08:00"
  }
}
```

规范：

- 默认必须走缓存。
- 缓存建议 5 到 15 分钟。
- Steam 请求失败时可返回过期缓存，并增加 `stale: true`。

---

### 8.3 批量在线人数

#### `POST /api/game-stats/current-players/batch`

用途：

- 批量查询多个游戏当前在线人数。

请求体：

```json
{
  "appids": [730, 570, 578080],
  "forceRefresh": false
}
```

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "items": [
      {
        "appid": 730,
        "playerCount": 945321,
        "cached": true,
        "syncedAt": "2026-06-15T10:00:00+08:00"
      }
    ]
  }
}
```

规范：

- 批量接口必须限制最大数量，建议最大 50。
- 不能在前端游戏列表渲染时逐个调用单查接口。

---

### 8.4 最近游玩

#### `GET /api/player/recent-games`

用途：

- 获取当前用户最近游玩游戏。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |
| `count` | number | 否 | 默认 10，最大 50 |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "totalCount": 10,
    "games": [
      {
        "appid": 730,
        "name": "Counter-Strike 2",
        "playtime2Weeks": 120,
        "playtimeForever": 1234,
        "iconUrl": "https://..."
      }
    ]
  }
}
```

---

### 8.5 用户资料

#### `GET /api/player/profile`

用途：

- 获取当前用户 Steam 公开资料。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "steamId": "7656119xxxxxxxxxx",
    "personaName": "Player",
    "profileUrl": "https://steamcommunity.com/id/example",
    "avatar": "https://...",
    "avatarMedium": "https://...",
    "avatarFull": "https://...",
    "personaState": 1,
    "communityVisibilityState": 3,
    "lastLogoff": 1718420000,
    "timeCreated": 1400000000,
    "countryCode": "CN"
  }
}
```

---

### 8.6 用户概览

#### `GET /api/player/summary`

用途：

- Dashboard 用户概览聚合接口。

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "profile": {
      "steamId": "7656119xxxxxxxxxx",
      "personaName": "Player",
      "avatarFull": "https://..."
    },
    "ownedGameCount": 364,
    "recentGameCount": 10,
    "totalPlaytimeForever": 123456,
    "lastSyncedAt": "2026-06-15T10:00:00+08:00"
  }
}
```

---

### 8.7 游戏新闻

#### `GET /api/game-news`

用途：

- 获取某个游戏新闻。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `appid` | number | 是 | Steam AppID |
| `count` | number | 否 | 默认 10，最大 50 |
| `maxlength` | number | 否 | 内容最大长度 |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "appid": 730,
    "items": [
      {
        "gid": "123",
        "title": "Update",
        "url": "https://...",
        "isExternalUrl": false,
        "author": "Valve",
        "contents": "News content",
        "feedLabel": "steam_community_announcements",
        "date": 1718420000
      }
    ]
  }
}
```

---

### 8.8 全局成就完成率

#### `GET /api/game-achievements/global-percentages`

用途：

- 获取游戏全局成就完成率。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `appid` | number | 是 | Steam AppID |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "appid": 730,
    "achievements": [
      {
        "name": "ACH_WIN_ONE_GAME",
        "percent": 75.2
      }
    ]
  }
}
```

---

### 8.9 游戏筛选列表

#### `GET /api/games`

用途：

- 面向游戏列表页的统一查询接口。
- 后续替代 `/api/ownedgames/list` 作为更强列表接口。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |
| `keyword` | string | 否 | 名称搜索 |
| `genre` | string | 否 | 类型 |
| `category` | string | 否 | 分类 |
| `sort` | string | 否 | `name/playtime/playerCount/releaseDate` |
| `order` | string | 否 | `asc/desc` |
| `page` | number | 否 | 默认 1 |
| `pageSize` | number | 否 | 默认 20，最大 100 |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "items": [
      {
        "appid": 730,
        "name": "Counter-Strike 2",
        "playtimeForever": 1234,
        "developer": "Valve",
        "publisher": "Valve",
        "releaseDate": "2023-09-27",
        "tags": "Shooter,Action",
        "headerImage": "https://...",
        "playerCount": 945321
      }
    ],
    "page": 1,
    "pageSize": 20,
    "total": 364
  }
}

```

---

### 8.10 标签、类型和分类

#### `GET /api/game-taxonomy`

用途：

- 获取当前数据库中的游戏分类、类型、标签聚合。

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "genres": [
      {
        "id": "1",
        "name": "Action",
        "count": 120
      }
    ],
    "categories": [
      {
        "id": "2",
        "name": "Single-player",
        "count": 240
      }
    ],
    "tags": [
      {
        "name": "Shooter",
        "source": "normalized",
        "count": 80
      }
    ]
  }
}
```

规范：

- `genres` 来源 Store AppDetails `genres`。
- `categories` 来源 Store AppDetails `categories`。
- `tags` 是项目归一化标签，不等同于 Steam 官方用户标签。

---

### 8.11 好友列表

#### `GET /api/player/friends`

用途：

- 获取当前用户 Steam 好友列表。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "items": [
      {
        "steamId": "7656119xxxxxxxxxx",
        "relationship": "friend",
        "friendSince": 1718420000
      }
    ]
  }
}
```

规范：

- 受隐私设置影响，失败时返回明确错误码。
- 非优先接口。

---

### 8.12 愿望单

#### `GET /api/player/wishlist`

用途：

- 获取当前用户愿望单。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `userId` | string | 否 | 默认 `default` |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "items": [
      {
        "appid": 730,
        "name": "Counter-Strike 2",
        "priority": 0,
        "addedAt": 1718420000
      }
    ]
  }
}
```

规范：

- 是否可用取决于 Steam 接口权限和隐私表现。
- 非优先接口。

---

### 8.13 API 健康检查

#### `GET /api/health`

用途：

- 前端启动时检查后端是否可用。

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "UP",
    "application": "steam-game",
    "time": "2026-06-15T10:00:00+08:00"
  }
}
```

---

### 8.14 Steam 外部 API 能力清单

#### `GET /api/admin/steam-api/supported`

用途：

- 后台查看当前 Steam Web API 自描述接口清单。

Query：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| `withKey` | boolean | 否 | 是否使用本地已配置 API Key 请求 |

成功响应：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "source": "ISteamWebAPIUtil/GetSupportedAPIList",
    "withKey": false,
    "interfaces": [
      {
        "name": "ISteamUserStats",
        "methods": [
          {
            "name": "GetNumberOfCurrentPlayers",
            "version": 1,
            "httpMethod": "GET"
          }
        ]
      }
    ]
  }
}
```

---

## 9. 数据同步规则

1. `/api/ownedgames/sync` 必须先写入基础游戏库。
2. `developer/publisher/releaseDate/tags` 允许为空。
3. 详情补全是异步任务，不阻塞同步主链路。
4. Steam 失败时返回本地已有数据，不删除旧数据。
5. 详情补全必须按 `userId + appid` 定位，禁止跨用户污染。
6. 轮询或手动补全都必须限流。

## 10. 版本和变更规则

1. 任何新增字段都必须先在这里定义。
2. 任何删除字段都必须先给出迁移方案。
3. 任何新增接口都必须标注：
   - 方法
   - 路径
   - 请求参数
   - 响应结构
   - 错误码
   - 是否需要登录
4. 只要本文不更新，前后端都不允许按“猜测”实现新接口。

## 11. 推荐开发顺序

1. 先按本文补齐后端接口。
2. 再按本文生成前端请求封装。
3. 再写联调测试。
4. 最后才允许改 UI 交互。
