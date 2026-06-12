# 游戏列表数据接口规范 (Games List API)

此文档定义了前端游戏列表（Steam 风格卡片界面）用于获取并展示玩家库存所有游戏的后端接口规范。

## 获取用户拥有的游戏列表

获取当前用户已收录在数据库中的所有 Steam 游戏列表。

**接口地址**：`/api/ownedgames/list`
**请求方法**：`GET`
**鉴权限制**：需要用户登录 Token

### 请求参数

*无*

### 响应格式 (JSON)

前端已作兼容处理，后端可采用以下两种响应格式之一：

#### 格式 1：标准包装格式（推荐）

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

#### 格式 2：直接数组格式

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
| `playtimeForever` | `Number` | 总游戏时长（单位：分钟。前端会自动除以 60 折算为小时并保留两位小数） |

---

*注：前端已在 `src/api/games.ts` 中声明了 `getOwnedGames()` 并做好了上述两种数据结构的兼容适配。*
