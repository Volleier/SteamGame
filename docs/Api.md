# 用户游戏库接口文档 (Owned Games API)

本文档统一说明 SteamGame 前端用于展示玩家游戏库数据的后端接口，包括游戏总数统计和游戏列表查询。

## 通用说明

**接口前缀**：`/api`

**鉴权限制**：需要用户登录 Token

**数据格式**：`application/json`

推荐后端统一返回标准包装格式：

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

通用错误响应示例：

```json
{
  "code": 401,
  "msg": "未授权或 Token 已过期",
  "data": null
}
```

## 获取用户游戏总数

获取当前用户已收录在 SteamGame 游戏库中的所有游戏总数。该数据将展示在主界面的 `04_INVENTORY` 卡片上。

**接口地址**：`/api/ownedgames/count`

**请求方法**：`GET`

### 请求参数

无。

### 响应格式

成功状态下，后端应返回包含 `count` 字段的 JSON 对象。

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "count": 342
  }
}
```

### 字段说明

| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `count` | `Number` | 玩家拥有的游戏总数 |

## 获取用户拥有的游戏列表

获取当前用户已收录在数据库中的所有 Steam 游戏列表，用于前端游戏列表页的 Steam 风格卡片展示。

**接口地址**：`/api/ownedgames/list`

**请求方法**：`GET`

### 请求参数

无。

### 响应格式

推荐使用标准包装格式：

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "appid": 359550,
      "name": "Tom Clancy's Rainbow Six Siege",
      "playtimeForever": 3840
    },
    {
      "appid": 730,
      "name": "Counter-Strike 2",
      "playtimeForever": 12845
    }
  ]
}
```

前端已做兼容处理，也支持直接数组格式：

```json
[
  {
    "appid": 359550,
    "name": "Tom Clancy's Rainbow Six Siege",
    "playtimeForever": 3840
  },
  {
    "appid": 730,
    "name": "Counter-Strike 2",
    "playtimeForever": 12845
  }
]
```

### 字段说明

| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `appid` | `Number` | 游戏的 Steam AppID |
| `name` | `String` | 游戏的 Steam 名称 |
| `playtimeForever` | `Number` | 总游戏时长，单位为分钟。前端会自动除以 60 折算为小时并保留两位小数 |

## 同步用户游戏库

强制同步当前用户的 Steam 游戏库。后端将连接 Steam 官方 API，拉取最新的游戏信息和游玩时间，将其写入数据库更新，并返回同步后的完整游戏列表。

**接口地址**：`/api/ownedgames/sync`

**请求方法**：`POST`

### 请求参数

无。

### 响应格式

成功状态下，后端应返回包含同步后最新游戏列表的标准包装格式：

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "appid": 359550,
      "name": "Tom Clancy's Rainbow Six Siege",
      "playtimeForever": 3840
    },
    {
      "appid": 730,
      "name": "Counter-Strike 2",
      "playtimeForever": 12845
    }
  ]
}
```

### 字段说明

字段与“获取用户拥有的游戏列表”接口一致：

| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `appid` | `Number` | 游戏的 Steam AppID |
| `name` | `String` | 游戏的 Steam 名称 |
| `playtimeForever` | `Number` | 总游戏时长，单位为分钟。前端会自动除以 60 折算为小时并保留两位小数 |

## 前端调用说明

前端已在 `src/api/games.ts` 中声明以下方法：

| 方法名 | 对应接口 | 说明 |
| :--- | :--- | :--- |
| `getGamesCount()` | `/api/ownedgames/count` | 获取玩家游戏总数。在后端实现前，前端会暂时使用 mock 数据展示 |
| `getOwnedGames()` | `/api/ownedgames/list` | 获取玩家游戏列表，并兼容标准包装格式和直接数组格式 |
| `syncOwnedGames()` | `/api/ownedgames/sync` | 触发游戏库同步，并返回同步后的游戏列表 |

