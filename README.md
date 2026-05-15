# 修复与功能完善汇总

## 本次完成内容

1. 管理员访问控制
   - 新增 `cloud.liang.common.AdminFilter`，拦截 `/Exam`、`/question`、`/jsp/questionPage.jsp`。
   - 只有 session 中登录用户名为 `admin` 的用户可以进入考试和试题新增、编辑、删除功能。
   - 当前项目没有独立登录页，因此现有 `/login001` 表单被完善为“登录 / 注册”入口：用户不存在则注册，用户存在则校验密码并登录。

2. 密码安全
   - 新增 `cloud.liang.common.PasswordUtil`，使用 PBKDF2-HMAC-SHA256 加盐哈希保存密码。
   - 用户列表不再查询和展示密码。
   - 已存在的旧明文密码在登录成功时会自动升级为哈希存储。
   - `Users.password` 字段启动时会被扩展为 `varchar(255)`，避免哈希值被截断。

3. 数据库凭据配置
   - 移除源码中的明文数据库密码。
   - 数据库连接支持通过环境变量或 JVM 参数配置：
     - `Q_SYSTEM_DB_URL` 或 `-Dq.system.db.url`
     - `Q_SYSTEM_DB_USER` 或 `-Dq.system.db.user`
     - `Q_SYSTEM_DB_PASSWORD` 或 `-Dq.system.db.password`
   - 默认数据库地址仍为 `jdbc:mysql://localhost:3306/q_system`，默认用户为 `root`，密码字符串位置预留在 `DatabaseConfig.LOCAL_PASSWORD`，默认留空，需要在运行时填入真实密码或改用环境变量。

4. XSS 防护
   - 试题列表、考试页、用户列表、成绩页、公共标题和提示信息都使用 `c:out` 或 `fn:escapeXml` 输出动态内容。
   - 题目、选项、用户名、邮箱等来自数据库的字段不再原样拼进 HTML。

5. CSRF 与删除方式修复
   - 删除试题从 GET 链接改为 POST 表单。
   - 新增、更新、删除试题都校验 session 中的 CSRF token。
   - 试题列表和试题表单页面会自动生成并携带 CSRF token。

6. 试题新增与更新完善
   - `/question` 的 GET 请求只负责打开新增页或编辑页，不再误触发新增逻辑。
   - 新增、更新会校验题目、四个选项、正确答案和试题编号。
   - 更新时会先检查试题是否存在。
   - 表单提交失败时会保留用户已填写内容并显示错误提示。
   - 编辑模式增加“取消编辑”入口，返回试题列表。

7. 考试防篡改
   - 考试开始时，服务端抽取的题目 ID 会保存到 session。
   - 提交试卷时不再信任客户端隐藏字段中的题目列表，而是按 session 中的题单判分。
   - 判分结束后会清理本次 session 题单。

8. 页面文案与入口整理
   - 修复主要 JSP 页面中的乱码文案。
   - 公共导航显示当前登录用户名。
   - 入口页和导航中的“试题新增”统一跳转到 `/question`，由过滤器和 servlet 统一处理。

9. Maven 配置
   - 指定源码编码为 UTF-8。
   - 指定 Java 编译 release 为 17。
   - MySQL Connector/J 依赖坐标更新为 `com.mysql:mysql-connector-j:8.0.33`。

## 管理员使用方式

1. 访问 `/jsp/mainpage.jsp`。
2. 使用用户名 `admin` 提交表单。
3. 如果数据库中还没有 `admin` 用户，会自动注册并登录。
4. 如果已有 `admin` 用户，需要输入正确密码后才能登录。
5. 登录为 `admin` 后，才能访问考试和试题维护功能。

## 验证结果

已执行：

```bash
mvn test
mvn package
```

结果：构建成功，当前项目没有测试用例，因此 Maven 显示 `No tests to run`。

## 注意事项

- 如果数据库原来存在明文密码用户，首次用正确旧密码登录后会自动迁移为哈希密码。
- 建议在运行环境中设置 `Q_SYSTEM_DB_PASSWORD`，不要使用代码中的默认占位密码。
- 管理员权限目前按用户名 `admin` 判断，后续如果要支持更多角色，建议给用户表增加角色字段。

## 2026-05-11 数据库层重构日志

- 将 `cloud.liang.mySqlSetting` 重构为 `DatabaseConfig`、`DatabasePool`、`InitSql`、`SqlConnector` 四个职责更清晰的类。
- 数据库连接从 `DriverManager` 直连改为 `HikariCP` 连接池，便于后续扩展和性能优化。
- 启动阶段会校验数据库密码配置，要求通过 `Q_SYSTEM_DB_PASSWORD` 或 `-Dq.system.db.password` 提供真实密码。
- 代码中保留了一个明确的密码字符串位置 `DatabaseConfig.LOCAL_PASSWORD`，你可以先留空，之后直接在该位置补上数据库密码。
- `SqlConnector` 对外方法名统一为更常见的 camelCase 风格，业务层调用方式保持简单。
- 进一步将 `mySqlSetting` 拆分为 `config`、`datasource`、`repository`、`schema` 四层目录，`SqlConnector` 仅保留兼容门面职责。
- 将数据库启动监听器更名为 `DatabaseInitializerListener`，命名更贴近常见 Web 项目约定。

## 2026-05-11 权限与登录流程调整日志

- 将考试权限改为“登录用户可访问”，新增 `LoginFilter` 拦截 `/Exam`。
- 将试题管理权限收紧为“仅 admin 可访问”，`AdminFilter` 现在拦截 `/question`、`/QMatch`、`/jsp/questionPage.jsp`。
- 原先混合的“登录 / 注册”流程已拆分为独立页面和独立 servlet：
  - `/login` 对应 `LoginServlet`
  - `/register` 对应 `RegisterServlet`
  - `/logout` 对应 `LogoutServlet`
- 登录页现在只需要用户名和密码，不再要求邮箱。
- `jsp/mainpage.jsp` 已调整为账号管理页，单独提供登录、注册、用户查询入口。
