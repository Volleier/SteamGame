# 库容接口设计 (Inventory API)

此文档定义了前端主界面无限画布系统中用于展示“玩家已收录游戏数量”的后端接口规范。

## 获取用户游戏总数

获取当前用户已收录在 SteamGame 游戏库中的所有游戏总数。该数据将展示在主界面的 `04_INVENTORY` 卡片上。

**接口地址**：`/api/ownedgames/count`
**请求方法**：`GET`
**鉴权限制**：需要用户登录 Token

### 请求参数

*无*

### 响应格式 (JSON)

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

### 错误响应示例

```json
{
  "code": 401,
  "msg": "未授权或 Token 已过期",
  "data": null
}
```

---

*注：前端已在 `src/api/games.ts` 中声明了 `getGamesCount()` 调用该接口。在后端实现此接口前，前端将暂时使用 mock 数据进行界面展示。*
