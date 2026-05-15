# 在线考试系统项目报告

## 1. 项目概述

本项目是一个基于 Java Web 技术栈实现的在线考试系统，项目名称在 Maven 中定义为 `W1 Maven Webapp`，构建产物为 `W1.war`。系统围绕“账号管理、题库管理、在线考试、成绩展示”四类核心功能展开，使用 JSP 负责页面渲染，Servlet 负责请求处理，MySQL 保存用户与试题数据，HikariCP 提供数据库连接池能力。

项目当前采用传统 MVC 思路组织代码：浏览器访问 JSP 或 Servlet 路由，Servlet 完成参数读取、权限校验、业务处理和数据库调用，再将结果放入 request 或 session 后转发到 JSP 页面展示。整体结构清晰，适合作为 Java Web 课程设计、Servlet/JSP 实践项目或小型在线考试系统原型。

从当前源码看，项目不仅包含基础的增删改查与考试判分，还加入了若干安全与健壮性设计，例如登录态校验、管理员权限控制、PBKDF2 密码哈希、CSRF token 校验、XSS 输出转义、数据库连接池、启动时自动建表等。这些内容使项目已经从“简单 JSP 表单演示”进化为一个较完整的 Web 应用雏形。

## 2. 技术栈与依赖

项目使用 Maven 管理依赖和构建，`pom.xml` 中的主要配置如下：

| 类别 | 技术/依赖 | 作用 |
| --- | --- | --- |
| 语言版本 | Java 17 | 通过 `maven.compiler.release` 指定编译目标 |
| Web 标准 | Jakarta Servlet API 6.0.0 | Servlet、Filter、Listener 等 Web 组件接口 |
| 页面技术 | JSP 3.1.1、JSTL 3.x | JSP 页面渲染、标签库输出、条件判断和循环 |
| 数据库 | MySQL Connector/J 9.3.0 | Java 连接 MySQL 数据库 |
| 连接池 | HikariCP 5.1.0 | 管理数据库连接，提高连接复用与性能 |
| 日志 | SLF4J Simple 2.0.13 | 简单日志输出 |
| 测试 | JUnit 3.8.1 | 测试依赖，目前项目没有发现实际测试用例 |
| 打包方式 | WAR | 适合部署到支持 Jakarta EE 10 / Servlet 6 的容器 |

需要注意的是，项目使用的是 `jakarta.servlet.*` 包名，不是旧版 `javax.servlet.*`。因此运行容器应选择支持 Jakarta Servlet 6 的版本，例如 Tomcat 10.1 或其他兼容 Jakarta EE 10 的 Web 容器。

## 3. 项目目录结构

项目采用 Maven Web 应用标准目录：

```text
ClassWork001
├── pom.xml
├── README.md
├── log.md
├── PROJECT_REPORT.md
├── src
│   └── main
│       ├── java
│       │   └── cloud
│       │       └── liang
│       │           ├── User
│       │           ├── Question
│       │           ├── Exam
│       │           ├── common
│       │           └── mySqlSetting
│       ├── resources
│       │   └── simplelogger.properties
│       └── webapp
│           ├── index.jsp
│           ├── WEB-INF
│           │   └── web.xml
│           └── jsp
│               ├── common
│               ├── css
│               ├── login.jsp
│               ├── register.jsp
│               ├── mainpage.jsp
│               ├── UserSearch.jsp
│               ├── questionPage.jsp
│               ├── Q2page.jsp
│               ├── examPage.jsp
│               └── scor.jsp
└── target
```

各目录职责如下：

- `src/main/java/cloud/liang/User`：用户实体、登录、注册、退出、用户查询与账号维护。
- `src/main/java/cloud/liang/Question`：试题实体、试题新增/编辑/删除、试题列表查询。
- `src/main/java/cloud/liang/Exam`：考试抽题、考试提交、成绩计算。
- `src/main/java/cloud/liang/common`：登录态、CSRF、密码哈希、登录过滤器、管理员过滤器等通用安全能力。
- `src/main/java/cloud/liang/mySqlSetting`：数据库配置、连接池、启动初始化、仓储层、建表逻辑。
- `src/main/webapp/jsp`：业务 JSP 页面。
- `src/main/webapp/jsp/common`：公共页面布局片段和提示消息片段。
- `src/main/webapp/jsp/css/app.css`：全站统一样式。
- `src/main/resources/simplelogger.properties`：SLF4J Simple 日志配置。
- `src/main/webapp/WEB-INF/web.xml`：Jakarta Web 应用描述文件，目前主要声明 Web App 版本，实际路由依赖注解。

## 4. 系统功能说明

### 4.1 首页入口

`index.jsp` 是项目入口页，提供到主要功能模块的链接：

- 账号管理：`/jsp/mainpage.jsp`
- 用户登录：`/login`
- 用户注册：`/register`
- 用户查询：`/UserSearch`
- 试题新增：`/question`
- 试题查询：`/QMatch`
- 参加考试：`/Exam`

该页面更像一个功能导航面板，便于在开发、演示或课程答辩时快速进入不同模块。

### 4.2 账号管理

账号模块由以下组件构成：

| 组件 | 路由/页面 | 主要职责 |
| --- | --- | --- |
| `LoginServlet` | `/login` | 打开登录页、校验用户名密码、写入登录 session |
| `RegisterServlet` | `/register` | 打开注册页、校验注册参数、创建用户并自动登录 |
| `LogoutServlet` | `/logout` | 销毁 session 并跳转登录页 |
| `UserSearch` | `/UserSearch` | 展示当前用户信息、管理员查看全部用户、更新本人信息、注销账号 |
| `User` | 实体类 | 保存用户 ID、用户名、密码、性别、邮箱、状态 |
| `login.jsp` | JSP | 登录表单 |
| `register.jsp` | JSP | 注册表单 |
| `mainpage.jsp` | JSP | 账号模块入口 |
| `UserSearch.jsp` | JSP | 当前用户信息、用户列表、编辑和注销入口 |

登录流程为：

1. 用户访问 `/login`。
2. `LoginServlet#doGet` 转发到 `login.jsp`。
3. 用户提交用户名和密码。
4. `LoginServlet#doPost` 查询数据库中的用户。
5. 如果用户不存在，返回失败提示。
6. 如果用户已注销，禁止登录。
7. 如果密码哈希校验通过，调用 `SecurityUtil.login` 将用户名写入 session。
8. 如果数据库中仍存在旧明文密码，且用户输入与明文一致，则自动升级为 PBKDF2 哈希后登录。

注册流程为：

1. 用户访问 `/register`。
2. `RegisterServlet#doGet` 转发到 `register.jsp`。
3. 用户提交用户名、密码、性别、邮箱。
4. 后端校验字段是否完整，并将性别规范化为 `男` 或 `女`。
5. 若用户名已存在，则阻止注册。
6. 若用户名对应账号已注销，也要求用户换用其他用户名。
7. 注册成功后，密码会被哈希存储，并自动登录当前用户。

用户信息维护流程为：

- 普通用户只能查看和修改自己的信息。
- 普通用户可修改用户名、邮箱、性别和密码。
- 普通用户不能把用户名改为 `admin`，避免绕过管理员判断。
- 用户可注销自己的账号，注销后 session 会失效并跳回登录页。
- 管理员可查看所有用户列表，并可注销其他用户。
- 管理员不能直接修改其他用户资料，页面中显示“仅本人可更新”。

### 4.3 题库管理

题库模块由以下组件构成：

| 组件 | 路由/页面 | 主要职责 |
| --- | --- | --- |
| `ServletQuestion` | `/question` | 新增、编辑、更新、删除试题 |
| `QuestionMatch` | `/QMatch` | 查询并展示试题列表 |
| `Questions` | 实体类 | 保存试题 ID、题干、四个选项、正确答案 |
| `questionPage.jsp` | JSP | 新增/编辑试题表单 |
| `Q2page.jsp` | JSP | 试题列表、更新和删除入口 |

题库管理受管理员过滤器保护。只有登录用户名为 `admin` 且该用户处于正常状态时，才能访问 `/question`、`/QMatch`、`/jsp/questionPage.jsp`。

新增试题流程为：

1. 管理员访问 `/question`。
2. `ServletQuestion#doGet` 创建或获取 CSRF token。
3. 转发到 `questionPage.jsp`。
4. 管理员填写题目、A/B/C/D 四个选项和正确答案。
5. 表单 POST 到 `/question`。
6. 后端校验 CSRF token。
7. 后端校验题目和选项非空，答案必须是 A、B、C、D 之一。
8. 调用 `SqlConnector.addQuestion` 写入数据库。

编辑试题流程为：

1. 管理员在 `/QMatch` 列表中点击“更新”。
2. 请求路径为 `/question?action=edit&questionId=...`。
3. 后端校验试题 ID 合法性。
4. 根据 ID 查询试题。
5. 将试题放入 `questionInfo` 并转发到 `questionPage.jsp`。
6. JSP 根据 `formAction=update` 切换为更新模式。
7. 提交后后端执行 `updateQuestion`。

删除试题流程为：

1. 管理员在试题列表点击删除按钮。
2. 页面弹出浏览器确认框。
3. 删除请求使用 POST 表单提交，不使用 GET 链接。
4. 表单携带 CSRF token 和试题 ID。
5. 后端校验 token 与 ID 后执行删除。

这种设计比“GET 删除”更安全，也更符合 HTTP 语义。

### 4.4 在线考试

考试模块由 `ServletExam`、`examPage.jsp` 和 `scor.jsp` 组成。

考试开始流程：

1. 用户访问 `/Exam`。
2. `LoginFilter` 先判断用户是否已经登录且账号处于正常状态。
3. `ServletExam#doGet` 从数据库随机抽取 4 道题。
4. 抽到的试题列表放入 request，供 `examPage.jsp` 展示。
5. 抽到的试题 ID 列表保存到 session 的 `examQuestionIds` 中。
6. 页面展示题目和四个单选选项。

考试提交流程：

1. 用户在页面中选择答案并提交。
2. `ServletExam#doPost` 从 session 中读取本次考试的试题 ID 列表。
3. 后端逐题读取数据库中的正确答案。
4. 根据请求参数 `answer_题号` 获取用户答案。
5. 忽略客户端隐藏字段，不信任前端传回的题目列表。
6. 统计答对题数。
7. 移除 session 中本次考试的题目 ID 列表。
8. 按 `答对题数 * 100 / 总题数` 计算分数。
9. 转发到 `scor.jsp` 展示得分、答对题数和总题数。

当前每次考试默认抽取 4 道题，由 `ServletExam` 中的 `EXAM_QUESTION_COUNT = 4` 控制。如果题库不足 4 道，实际展示数量会少于 4，道题数量由数据库查询结果决定。

## 5. 数据库设计

数据库默认连接地址为：

```text
jdbc:mysql://localhost:3306/q_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
```

项目启动时，`DatabaseInitializerListener` 会获取数据库连接并调用 `SqlConnector.createTables`，最终由 `DatabaseSchemaManager` 创建或更新表结构。

### 5.1 Users 表

用户表名为 `Users`，用于保存账号信息。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `userId` | `int primary key auto_increment` | 用户主键 |
| `userName` | `varchar(50)` | 用户名 |
| `password` | `varchar(255)` | 密码哈希或历史明文密码 |
| `sex` | `char(1)` | 性别，当前业务存储为 `男` 或 `女` |
| `email` | `varchar(50)` | 邮箱 |
| `status` | `varchar(20) not null default 'ACTIVE'` | 用户状态 |

用户状态当前包括：

- `ACTIVE`：正常账号，可以登录和使用系统。
- `CANCELLED`：已注销账号，不能登录。

项目启动时还会执行：

- `ALTER TABLE Users MODIFY password varchar(255)`：确保密码字段可容纳 PBKDF2 哈希。
- 若缺失 `status` 字段，则自动添加该字段。

### 5.2 question 表

试题表名为 `question`，用于保存选择题。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `questionId` | `int primary key auto_increment` | 试题主键 |
| `title` | `varchar(50)` | 题干 |
| `optionA` | `varchar(20)` | A 选项 |
| `optionB` | `varchar(20)` | B 选项 |
| `optionC` | `varchar(20)` | C 选项 |
| `optionD` | `varchar(20)` | D 选项 |
| `answer` | `char(1)` | 正确答案，A/B/C/D |

当前表结构适合保存短题干和短选项。如果后续要支持更长的题目、代码题干、图片题、解析文本，建议将 `title` 和选项字段改为更长的 `varchar` 或 `text`。

## 6. 数据访问层设计

数据库访问层经过拆分，职责比较明确：

| 类 | 职责 |
| --- | --- |
| `DatabaseConfig` | 读取数据库 URL、用户名、密码配置 |
| `DatabasePool` | 使用 HikariCP 创建和维护连接池 |
| `DatabaseSchemaManager` | 启动时创建表和补充字段 |
| `DatabaseInitializerListener` | Web 应用启动/销毁时初始化和关闭数据库资源 |
| `UserRepository` | 用户表增查改、密码更新、用户列表、账号注销 |
| `QuestionRepository` | 试题表新增、列表、随机抽题、按 ID 查询、答案查询、更新、删除 |
| `SqlConnector` | 对外兼容门面，业务层通过它调用仓储层 |

`SqlConnector` 目前相当于一个 facade。它隐藏了底层 repository 的拆分，使 Servlet 中仍然可以使用简单的静态方法，例如：

- `findUserByUsername`
- `addUser`
- `listUsers`
- `addQuestion`
- `listQuestions`
- `listRandomQuestions`
- `getQuestionAnswerById`

这种做法降低了 Servlet 层调用复杂度，但也让代码仍然保持静态工具类风格。对于课程项目来说足够直接；如果发展为更复杂的系统，可以进一步引入 Service 层和依赖注入。

## 7. 安全机制分析

### 7.1 登录态管理

登录态由 `SecurityUtil` 统一管理，session key 为 `loginUserName`。登录成功后把用户名写入 session，退出或账号异常时调用 session invalidate。

`LoginFilter` 拦截：

- `/Exam`
- `/UserSearch`

过滤器会检查：

1. session 中是否存在登录用户名。
2. 数据库中是否存在该用户。
3. 用户状态是否为 `ACTIVE`。

如果检查失败，会清理登录态并转发到登录页。

### 7.2 管理员权限控制

`AdminFilter` 拦截：

- `/question`
- `/QMatch`
- `/jsp/questionPage.jsp`

管理员判断规则为：当前登录用户名必须严格等于 `admin`，并且数据库中该用户存在且状态为 `ACTIVE`。

这种实现简单直观，适合课程项目。不过它将“管理员身份”绑定到了用户名，扩展性有限。未来如果要支持多个管理员、教师账号、学生账号等角色，建议在 `Users` 表中增加 `role` 字段，改为基于角色判断权限。

### 7.3 密码安全

项目使用 `PasswordUtil` 实现密码哈希，算法为 PBKDF2-HMAC-SHA256。哈希字符串格式为：

```text
pbkdf2_sha256:迭代次数:盐值Base64:哈希Base64
```

当前参数包括：

- 迭代次数：120000
- 盐长度：16 字节
- 派生密钥长度：256 bit
- 随机源：`SecureRandom`

登录校验时，项目会使用常量时间比较函数比较哈希结果，降低时序攻击风险。

此外，系统兼容历史明文密码：如果数据库中保存的不是 PBKDF2 格式，但用户输入与旧明文密码一致，登录成功后会立即将密码升级为哈希值。这种迁移方式适合从早期课程代码平滑过渡到更安全的实现。

### 7.4 CSRF 防护

`SecurityUtil` 负责生成和校验 CSRF token：

- token 保存在 session 的 `csrfToken` 中。
- 页面通过隐藏字段 `csrfToken` 提交。
- 后端在 POST 修改操作前校验 token。

当前使用 CSRF 的主要操作包括：

- 试题新增
- 试题更新
- 试题删除
- 用户资料更新
- 用户注销

删除试题和注销账号均使用 POST 表单，而不是 GET 链接，安全性和语义都更合理。

### 7.5 XSS 防护

JSP 页面普遍使用 JSTL 的 `c:out` 或 functions 标签库的 `fn:escapeXml` 输出动态数据，覆盖了：

- 题干
- 选项
- 正确答案
- 用户名
- 邮箱
- 用户状态
- 提示信息
- 当前登录用户名

这可以避免数据库内容或请求内容被原样输出为 HTML/JavaScript，从而降低反射型和存储型 XSS 风险。

### 7.6 防篡改判分

考试开始时，服务端将抽取到的题目 ID 保存到 session；提交试卷时，后端只按 session 中的题目 ID 判分，不信任客户端传回的题目列表。

这一点非常重要。如果直接相信前端隐藏字段，用户可以通过浏览器开发者工具篡改题号或题目数量，影响成绩。当前实现已经避免了这种问题。

## 8. 页面与前端设计

项目前端为 JSP 服务端渲染页面，使用统一 CSS 文件 `app.css`。整体页面采用侧边栏 + 主内容区布局：

- 左侧侧边栏显示系统名称、当前用户和导航菜单。
- 右侧主区域显示页面标题、提示消息和具体业务表单/表格。
- 页面通过 `pageTitle` 和 `pageId` 控制标题和导航高亮。
- 公共布局抽取到 `layout-start.jspf` 和 `layout-end.jspf`。
- 消息提示抽取到 `notice.jspf`。

主要页面如下：

| 页面 | 作用 |
| --- | --- |
| `index.jsp` | 功能入口页 |
| `mainpage.jsp` | 账号管理入口 |
| `login.jsp` | 用户登录 |
| `register.jsp` | 用户注册 |
| `UserSearch.jsp` | 用户资料、用户列表、更新和注销 |
| `questionPage.jsp` | 试题新增/编辑 |
| `Q2page.jsp` | 试题查询、更新、删除 |
| `examPage.jsp` | 考试答题 |
| `scor.jsp` | 成绩展示 |

样式上，系统使用较克制的管理后台风格：深色侧边栏、浅色背景、白色内容面板、表格、表单、按钮和提示框。CSS 中还包含响应式规则：当屏幕宽度低于 900px 时，页面从左右布局切换为单列布局；当屏幕低于 640px 时，主面板间距和表单选项布局进一步收窄。

## 9. 请求路由总览

| 路由 | HTTP 方法 | 处理类/页面 | 权限 | 说明 |
| --- | --- | --- | --- | --- |
| `/index.jsp` | GET | JSP | 无 | 首页入口 |
| `/jsp/mainpage.jsp` | GET | JSP | 无 | 账号管理入口 |
| `/login` | GET | `LoginServlet` | 无 | 打开登录页 |
| `/login` | POST | `LoginServlet` | 无 | 登录校验 |
| `/register` | GET | `RegisterServlet` | 无 | 打开注册页 |
| `/register` | POST | `RegisterServlet` | 无 | 注册用户 |
| `/logout` | GET | `LogoutServlet` | 登录后可用 | 退出登录 |
| `/UserSearch` | GET | `UserSearch` | 登录用户 | 查看用户信息或打开编辑模式 |
| `/UserSearch` | POST | `UserSearch` | 登录用户 + CSRF | 更新用户、注销用户 |
| `/question` | GET | `ServletQuestion` | admin | 打开新增页或编辑页 |
| `/question` | POST | `ServletQuestion` | admin + CSRF | 新增、更新、删除试题 |
| `/QMatch` | GET/POST | `QuestionMatch` | admin | 查询试题列表 |
| `/Exam` | GET | `ServletExam` | 登录用户 | 随机抽题并打开考试页 |
| `/Exam` | POST | `ServletExam` | 登录用户 | 判分并展示成绩 |

## 10. 运行与部署方式

### 10.1 数据库准备

项目默认使用 MySQL 数据库 `q_system`。运行前应确保本地 MySQL 已创建该数据库，例如：

```sql
CREATE DATABASE q_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

表结构不需要手动创建，应用启动时会自动创建 `Users` 和 `question` 表。

### 10.2 数据库连接配置

`DatabaseConfig` 支持通过环境变量或 JVM 参数覆盖数据库配置：

| 配置项 | 环境变量 | JVM 参数 |
| --- | --- | --- |
| 数据库 URL | `Q_SYSTEM_DB_URL` | `-Dq.system.db.url=...` |
| 用户名 | `Q_SYSTEM_DB_USER` | `-Dq.system.db.user=...` |
| 密码 | `Q_SYSTEM_DB_PASSWORD` | `-Dq.system.db.password=...` |

当前源码中 `DatabaseConfig.LOCAL_PASSWORD` 仍含有本地密码字符串，建议不要将真实数据库密码提交到仓库。更安全的方式是在运行环境中设置 `Q_SYSTEM_DB_PASSWORD`，并将源码中的默认密码留空或改为占位值。

### 10.3 构建命令

项目可以使用 Maven 构建：

```bash
mvn test
mvn package
```

`mvn package` 成功后会在 `target` 目录生成 WAR 包，名称通常为：

```text
target/W1.war
```

然后可部署到 Tomcat 10.1 等支持 Jakarta Servlet 6 的 Web 容器中。

## 11. 现有优点

1. 模块划分比较清楚  
   用户、试题、考试、公共安全、数据库层均放在不同包下，便于阅读和维护。

2. 使用注解式 Servlet 配置  
   大部分路由通过 `@WebServlet`、`@WebFilter`、`@WebListener` 声明，`web.xml` 保持简洁。

3. 数据库层有连接池  
   使用 HikariCP 替代每次 DriverManager 直连，性能和资源管理更合理。

4. 有启动自动建表能力  
   初次部署时可以自动创建基础表结构，降低演示和部署成本。

5. 密码存储方式较安全  
   使用 PBKDF2、随机盐、常量时间比较，并兼容旧明文密码迁移。

6. 已加入权限控制  
   登录用户和管理员权限分离，试题管理被限制给 admin。

7. 修改操作有 CSRF 防护  
   关键 POST 操作携带并校验 token。

8. 页面输出做了转义  
   JSP 输出动态内容时使用 `c:out` 或 `fn:escapeXml`，降低 XSS 风险。

9. 考试判分不信任客户端题目列表  
   题目 ID 保存到 session 中，避免用户篡改前端字段影响成绩。

10. 页面风格统一  
    公共布局和样式集中维护，整体体验比散乱 JSP 更完整。

## 12. 当前不足与风险

1. 数据库密码仍可能出现在源码中  
   虽然配置支持环境变量和 JVM 参数，但当前源码中仍有本地密码字符串。真实项目中这属于敏感信息泄露风险，应尽快移除并更换已暴露密码。

2. 管理员权限依赖用户名  
   当前只要用户名为 `admin` 就被视为管理员。虽然普通用户更新时禁止改成 admin，但角色模型仍然不够正规。建议增加 `role` 字段。

3. 数据库表缺少唯一约束  
   代码层检查用户名是否重复，但 `Users.userName` 没有数据库唯一索引。并发注册时可能出现重复用户名。建议添加唯一索引。

4. 试题字段长度偏短  
   `title varchar(50)`、选项 `varchar(20)` 对真实考试题过短，容易截断内容。建议改为 `varchar(255)` 或 `text`。

5. 缺少 Service 层  
   当前 Servlet 直接调用 `SqlConnector`。业务复杂后，验证逻辑、事务逻辑和权限逻辑会分散在 Servlet 中。建议引入 Service 层。

6. 异常处理不统一  
   一些地方抛出 `ServletException`，一些地方 `e.printStackTrace()` 后返回失败提示，还有 `QuestionMatch` 中将 SQLException 包装为 RuntimeException。建议统一错误页面和日志策略。

7. 缺少自动化测试  
   `pom.xml` 有 JUnit 依赖，但项目没有明显测试用例。建议至少补充密码工具、表单校验、Repository 基础操作和 Servlet 流程测试。

8. 缺少考试记录表  
   当前成绩只在请求中展示，没有保存考试记录。刷新或离开页面后成绩不会留存。建议增加 `exam_record` 和 `exam_answer` 表。

9. CSRF token 在 JSP 中有少量脚本片段生成  
   `questionPage.jsp` 和 `Q2page.jsp` 直接调用 Java 方法生成 token。更理想的方式是完全由 Servlet 准备 request attribute，JSP 只负责展示。

10. 账号注销是软删除但缺少恢复流程  
    `CANCELLED` 用户无法登录，也无法用同名重新注册。若误注销，需要管理员或数据库手动处理。可以增加恢复账号功能。

## 13. 改进建议

### 13.1 数据库与安全改进

- 移除源码中的真实数据库密码，并通过环境变量注入。
- 为 `Users.userName` 添加唯一索引。
- 增加 `role` 字段，例如 `ADMIN`、`TEACHER`、`STUDENT`。
- 增加 `createdAt`、`updatedAt` 字段，便于追踪数据变化。
- 增加登录失败次数限制或验证码，防止暴力破解。
- 对密码长度和复杂度做基础校验。
- 将注销账号、删除试题等危险操作记录审计日志。

### 13.2 业务功能改进

- 增加考试记录保存功能。
- 增加题目解析字段，考试结束后展示正确答案和解析。
- 支持题目分类、难度、章节。
- 支持按条件组卷，而不是纯随机抽题。
- 支持考试限时和自动交卷。
- 支持管理员恢复已注销用户。
- 支持分页查询用户和试题。
- 支持搜索试题和搜索用户。

### 13.3 架构改进

- 引入 Service 层，Servlet 只负责 HTTP 请求/响应。
- 将表单校验逻辑抽取为独立方法或校验类。
- 统一异常处理，避免重复的 try-catch 与 `printStackTrace`。
- 使用日志记录异常，而不是直接打印堆栈。
- 将 JSP 中的 Java scriptlet 完全移除，保持 JSP 只做视图。
- 如果后续规模扩大，可考虑迁移到 Spring MVC 或 Spring Boot。

### 13.4 测试改进

建议补充以下测试：

- `PasswordUtil`：哈希、校验、错误密码、非法哈希格式。
- `SecurityUtil`：登录态、退出、CSRF token 生成与校验。
- `UserRepository`：新增用户、查找用户、更新密码、注销用户。
- `QuestionRepository`：新增试题、查询试题、随机抽题、更新和删除。
- Servlet 流程测试：登录、注册、题库新增、考试判分。
- 权限测试：未登录访问 `/Exam`，普通用户访问 `/question`，admin 访问题库。

## 14. 总结

本项目是一个结构完整的 Java Web 在线考试系统，具备账号注册登录、用户信息维护、管理员题库管理、随机抽题考试、成绩计算展示等核心功能。它使用 JSP + Servlet + MySQL 的经典组合，适合展示 Java Web 基础知识和 MVC 分层思想。

从代码质量看，项目已经加入了许多课程项目中容易忽略的安全措施，包括密码哈希、CSRF 防护、XSS 输出转义、登录过滤、管理员过滤和服务端判分防篡改。这些设计显著提升了项目的完整性和可信度。

后续如果继续完善，最值得优先处理的是：移除源码中的数据库密码、增加数据库唯一约束、引入角色字段、保存考试记录、补充自动化测试。完成这些改进后，该项目可以从课程作业级系统进一步成长为一个更规范、更可维护的小型考试平台。
