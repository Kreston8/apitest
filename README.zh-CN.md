# apitest

English | [简体中文](README.zh-CN.md)

一个极简的 **Spring Boot RESTful CRUD** 示例项目：经典的 Controller → Service → Mapper 三层架构，管理 `t_user` 用户表。基于 MyBatis-Plus，单表增删改查几乎不用手写 SQL。

## 功能特性

- 6 个 RESTful 接口覆盖完整增删改查：列表、按 id 查询、模糊查询、新增、修改、删除
- 标准分层架构：`Controller` → `Service` → `Mapper` → MySQL
- MyBatis-Plus 自动生成全部单表 SQL
- 统一返回体 `Result{code, msg, data}`，前后端契约固定
- **springdoc-openapi 自动生成 OpenAPI 3 接口文档**：自带 Swagger UI 页面 + JSON 规范，可直接导入 Apifox
- JDK 21 + Spring Boot 3.3.13 + MySQL 8

## 技术栈

| 层级 | 技术 |
|---|---|
| 语言 / 运行时 | Java 21（OpenJDK Temurin） |
| 框架 | Spring Boot 3.3.13 |
| ORM | MyBatis-Plus 3.5.17（spring-boot3 starter，`AbstractRepository`） |
| 接口文档 | springdoc-openapi 2.5.0 |
| 数据库 | MySQL 8（库 `testdb`，表 `t_user`） |
| 构建 | Maven 3.8+ |
| 其他 | Lombok 1.18.36 |

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- 本机 MySQL 8 运行中

### 1. 初始化数据库

```sql
-- 执行项目自带的建表脚本
mysql -u root < src/main/resources/sql/user_table.sql
```

会创建 `testdb` 库、`t_user` 表，并写入两条测试数据（`zhangsan`、`lisi`）。

### 2. 修改连接配置

编辑 `src/main/resources/application.yml`（本地默认账号 `apitest` / 密码 `apitest123`，与你的环境不一致就改）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/testdb?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: apitest
    password: apitest123
```

> 除本地开发外，建议把账号密码改用环境变量（如 `${DB_USER}`、`${DB_PASSWORD}`），不要硬编码。

### 3. 构建并运行

```bash
mvn -q -DskipTests package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

服务运行在 **8080** 端口。日志出现 `Started DemoApplication` 后验证：

```bash
curl http://127.0.0.1:8080/user/list
```

## 接口文档（OpenAPI 3）

springdoc-openapi 自动生成规范文档，无需额外配置：

| 资源 | 地址 |
|---|---|
| Swagger UI（浏览器查看） | `http://127.0.0.1:8080/swagger-ui/index.html` |
| OpenAPI JSON 规范 | `http://127.0.0.1:8080/v3/api-docs` |

**导入 Apifox**（或其他 OpenAPI 客户端）：新建/选择项目 → 导入数据 → OpenAPI/Swagger → URL 导入 → 填 `http://127.0.0.1:8080/v3/api-docs`。接口、参数、响应结构全部自动生成；改完代码重新导入（覆盖）即可保持同步。

## 接口文档

统一前缀：`/user` · 统一返回：`{"code":200,"msg":"操作成功","data":...}`（`code=200` 表示成功）

| 方法 | 路径 | 参数 | 说明 |
|---|---|---|---|
| GET | `/user/list` | — | 查询全部用户 |
| GET | `/user/{id}` | 路径参数 id | 按 id 查询单个 |
| GET | `/user/query` | 查询参数 username（可选） | 按用户名模糊查询 |
| POST | `/user` | JSON 请求体 | 新增用户 |
| PUT | `/user` | JSON 请求体（必须含 id） | 修改用户 |
| DELETE | `/user/{id}` | 路径参数 id | 删除用户 |

### 示例

**GET /user/list**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {"id": 1, "username": "zhangsan", "age": 22, "email": "zhangsan@test.com"},
    {"id": 2, "username": "lisi", "age": 25, "email": "lisi@test.com"}
  ]
}
```

**POST /user**

请求体：

```json
{"username": "wangwu", "age": 30, "email": "wangwu@test.com"}
```

响应：`{"code": 200, "msg": "操作成功", "data": true}`

**PUT /user** — 请求体**必须带 id**，否则更新条件为空、返回 false：

```json
{"id": 3, "username": "wangwu", "age": 31, "email": "wangwu@test.com"}
```

**DELETE /user/3** — 响应：`{"code": 200, "msg": "操作成功", "data": true}`

**GET /user/query?username=zhang** — 通过 `LIKE '%zhang%'` 匹配到 `zhangsan`。

## 项目结构

```
apitest/
├── pom.xml                         # 依赖：web、mybatis-plus-boot3、springdoc、mysql、lombok
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java       # Spring Boot 启动类
│   ├── controller/UserController.java  # 接口层：REST 端点
│   ├── service/UserService.java   # 业务层（继承 AbstractRepository）
│   ├── mapper/UserMapper.java     # 数据层（继承 BaseMapper）
│   └── entity/
│       ├── User.java              # 实体，映射 t_user 表
│       └── Result.java            # 统一返回体
└── src/main/resources/
    ├── application.yml            # 端口 8080、数据源、mybatis-plus 配置
    └── sql/user_table.sql         # 建表语句 + 测试数据
```

## 常见问题

- **`Port 8080 was already in use`** → `ss -tlnp | grep 8080` 找到 PID，`kill <PID>`。
- **`Table 'testdb.t_user' doesn't exist`** → 先执行建表 SQL 脚本。
- **编译报 `JCTree` 相关错误** → Lombok 需 ≥ 1.18.30 才支持 JDK 21（本项目已用 1.18.36）。
- **客户端连不上服务** → 在 WSL2 中运行时，若 localhost 转发失效，改用 WSL IP（`wsl hostname -I`）访问。
