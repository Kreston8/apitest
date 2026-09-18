# apitest

English | [简体中文](README.zh-CN.md)

一个极简的 **Spring Boot RESTful CRUD** 示例项目：经典的 Controller → Service → Mapper 三层架构，管理 `t_user` 用户表。基于 MyBatis-Plus，单表增删改查几乎不用手写 SQL。

## 功能特性

- 7 个 RESTful 接口覆盖完整增删改查 + **账户转账**：列表、按 id 查询、模糊查询、新增、修改、删除、转账
- **转账带事务**：`扣款 + 入账` 在同一个 `@Transactional` 里执行——要么全部成功，要么全部回滚；失败时双方余额分毫不动
- **原子扣款**：用条件 `UPDATE ... WHERE balance >= amount` 保证并发转账也不会把余额扣成负数
- **三种转账方式**：`CASH` 现金（默认）/ `ALIPAY` 支付宝 / `WECHAT` 微信，请求体里可选传 `payMethod`，大小写（`cash`/`Cash`）和中文（`现金`/`支付宝`/`微信`）写法都兼容
- 标准分层架构：`Controller` → `Service` → `Mapper` → MySQL
- MyBatis-Plus 自动生成全部单表 SQL
- 统一返回体 `Result{code, msg, data}` + 全局异常处理（`@RestControllerAdvice`）
- 每个用户带 `balance`（存款）字段；测试数据内置三人（`zhangsan`、`lisi`、`wangwu`），初始余额随机不等
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

```bash
# 先建库，再对库执行项目自带的建表脚本
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS testdb DEFAULT CHARSET utf8mb4"
mysql -u root -p testdb < src/main/resources/sql/user_table.sql
```

会创建 `testdb` 库、`t_user` 表（含 `balance DECIMAL(12,2)` 存款列），并写入三条测试数据，初始余额随机不等：

| id | username | age | balance |
|---|---|---|---|
| 1 | zhangsan | 22 | 12800.50 |
| 2 | lisi | 25 | 9377.25 |
| 3 | wangwu | 30 | 15666.80 |

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
| GET | `/user/query` | 查询参数 username（可选）、minAge/maxAge（可选） | 用户名模糊 + 年龄区间查询 |
| POST | `/user` | JSON 请求体 | 新增用户 |
| PUT | `/user` | JSON 请求体（必须含 id） | 修改用户 |
| DELETE | `/user/{id}` | 路径参数 id | 删除用户 |
| POST | `/user/transfer` | JSON 请求体 `{fromId, toId, amount, payMethod?}` | 账户间转账（事务保证，方式可选） |

### 示例

**GET /user/list**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {"id": 1, "username": "zhangsan", "age": 22, "email": "zhangsan@test.com", "balance": 12800.50},
    {"id": 2, "username": "lisi", "age": 25, "email": "lisi@test.com", "balance": 9377.25},
    {"id": 3, "username": "wangwu", "age": 30, "email": "wangwu@test.com", "balance": 15666.80}
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

**GET /user/query?username=zhang&minAge=20&maxAge=30** — 通过 `LIKE '%zhang%'` 匹配到 `zhangsan`，并叠加年龄区间过滤。

**POST /user/transfer** — 从 zhangsan(1) 给 lisi(2) 转 500。`payMethod` 可选（默认 `CASH`），以下写法都行：

```json
{"fromId": 1, "toId": 2, "amount": 500}
{"fromId": 1, "toId": 2, "amount": 500, "payMethod": "ALIPAY"}
{"fromId": 1, "toId": 2, "amount": 500, "payMethod": "wechat"}
{"fromId": 1, "toId": 2, "amount": 500, "payMethod": "现金"}
```

响应：`{"code": 200, "msg": "操作成功", "data": true}`

失败场景返回 `code=400` + 明确原因，**所有余额保持不变**（事务回滚）：

```json
{"code": 400, "msg": "转出账户不存在或余额不足", "data": null}
{"code": 400, "msg": "转入账户不存在", "data": null}
{"code": 400, "msg": "不能给自己转账", "data": null}
{"code": 400, "msg": "转账金额必须大于 0", "data": null}
{"code": 400, "msg": "请求体格式错误：不支持的转账方式: PAYPAL", "data": null}
```

### 转账事务是怎么保证的

`POST /user/transfer` 由 `UserService.transfer()` 处理，方法标注 `@Transactional(rollbackFor = Exception.class)`：

1. 参数校验：`amount > 0`、`fromId != toId`、两个 id 均非空；`payMethod` 缺省默认 `CASH`（枚举取值由 Jackson 反序列化保证合法）。
2. **原子扣款**：`UPDATE t_user SET balance = balance - #{amount} WHERE id = #{fromId} AND balance >= #{amount}`——影响行数为 0 表示余额不足或转出账户不存在；`balance >= amount` 这个条件同时保证并发转账不会超扣。
3. **入账**：`UPDATE t_user SET balance = balance + #{amount} WHERE id = #{toId}`——影响行数为 0 表示转入账户不存在。

任一步失败都会抛出 `TransferException`，整个事务回滚，第 2 步已经执行的扣款被一并撤销——**两条 UPDATE 要么同时提交，要么同时回滚**。异常由 `GlobalExceptionHandler` 统一转成 `Result.fail(400, msg)`。

## 项目结构

```
apitest/
├── pom.xml                         # 依赖：web、mybatis-plus-boot3、springdoc、mysql、lombok
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java       # Spring Boot 启动类
│   ├── controller/UserController.java  # 接口层：REST 端点（含 /transfer）
│   ├── service/UserService.java   # 业务层（继承 AbstractRepository，含 transfer()）
│   ├── mapper/UserMapper.java     # 数据层（继承 BaseMapper，含 deductBalance/addBalance）
│   ├── enums/PayMethod.java       # 转账方式枚举：CASH / ALIPAY / WECHAT
│   ├── dto/TransferRequest.java   # 转账请求体（含 payMethod）
│   ├── exception/
│   │   ├── TransferException.java # 转账业务异常（触发回滚）
│   │   └── GlobalExceptionHandler.java  # @RestControllerAdvice 全局异常处理
│   └── entity/
│       ├── User.java              # 实体，映射 t_user 表（含 balance）
│       └── Result.java            # 统一返回体
└── src/main/resources/
    ├── application.yml            # 端口 8080、数据源、mybatis-plus 配置
    └── sql/user_table.sql         # 建表语句（含 balance）+ 3 条测试数据
```

## 常见问题

- **`Port 8080 was already in use`** → `ss -tlnp | grep 8080` 找到 PID，`kill <PID>`。
- **`Table 'testdb.t_user' doesn't exist`** 或 **`Unknown column 'balance'`** → 重新执行建表 SQL 脚本（脚本幂等，先 `DROP TABLE IF EXISTS`）。
- **编译报 `JCTree` 相关错误** → Lombok 需 ≥ 1.18.30 才支持 JDK 21（本项目已用 1.18.36）。
- **客户端连不上服务** → 在 WSL2 中运行时，若 localhost 转发失效，改用 WSL IP（`wsl hostname -I`）访问。
