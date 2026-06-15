# Steam Games Logger
记录并展示 Steam 游戏库、游玩时长和游戏元数据。

## 部署

```bash
steam-api\mvnw.cmd -f pom.xml -DskipTests package
java -Dfile.encoding=UTF-8 -jar steam-launcher\target\steam-launcher-0.0.1.jar
```

## 使用

前端开发：

```bash
cd vue
npm run dev
```

## 模块

### steam-dependencies
根 Maven 聚合工程，统一管理后端模块构建顺序和公共依赖版本。

### steam-common
公共基础模块，提供统一响应 `ApiResponse`、错误码、业务异常、当前用户上下文和凭据接口。

### steam-login
Steam 凭据模块，负责 SteamID/API Key 的配置、加密保存、解密读取、在线验证和默认用户上下文。

### steam-api
普通用户侧游戏业务模块，负责 Steam 游戏库同步、H2 入库、游戏列表/数量查询、游戏详情异步补全。

### steam-admin
平台管理后台模块，提供用户、凭据状态、同步任务、游戏元数据和系统配置的管理接口。

### steam-launcher
唯一后端启动入口，聚合 `steam-common`、`steam-login`、`steam-api`、`steam-admin` 并监听 `8080`。

### vue
Vue 前端应用，负责凭据配置、Dashboard、游戏列表和同步入口。
