# CMS Pro

> 一个面向合同业务场景的管理系统原型，覆盖登录认证、仪表盘、合同管理、客商管理与系统设置。

## 项目简介

CMS Pro 是一个前后端分离的合同管理系统，当前版本已经具备完整的核心业务链路，适合用于：

- 课程设计 / 毕业设计
- 业务原型演示
- 中小型后台管理系统练习项目
- 作为后续审批流、附件上传、权限系统的基础模板

当前项目采用 `Spring Boot 3 + MyBatis-Plus + MySQL + Vue 3 + Vite + TypeScript` 实现，界面支持主题色配置，并已接通主要动态数据。

## 在线功能概览

### 已完成模块

- 登录 / 注册
- JWT 鉴权
- 仪表盘动态统计
- 合同管理
- 客商管理
- 系统设置
- 全局搜索
- 主题色切换

### 合同管理当前能力

- 合同列表卡片化展示
- 合同新增、编辑、删除
- 三步式合同录入流程
- 按状态筛选
- 按合同类型筛选
- 按合同编号 / 合同名称 / 相对方搜索
- 合同详情处理
- 合同续约入口
- 同一用户内合同编号唯一

### 客商管理当前能力

- 客商新增、编辑、删除
- 删除二次确认
- 客商状态展示
- 按名称、编号、联系人、电话、地址模糊搜索
- 空数据占位提示

### 系统设置当前能力

- 系统名称配置
- 主题色预设选择
- 自定义主题色
- 保存后全站即时生效

## 技术栈

### 后端

- Java 17
- Spring Boot 3.2.4
- MyBatis-Plus 3.5.5
- MySQL 8.x
- JJWT 0.12.5
- Lombok

### 前端

- Vue 3
- TypeScript
- Vite
- Vue Router
- Pinia
- Axios
- Naive UI
- Tailwind CSS

## 项目结构

```text
contract-code/
├─ contract-api/         # Spring Boot 后端
├─ contract-ui/          # Vue 3 前端
├─ sql/                  # 数据库脚本
├─ doc/                  # 文档目录
├─ contract.html         # 原型静态页面
└─ README.md
```

## 页面模块

### 1. 登录页

- 用户登录
- 登录后保存 token
- 自动跳转业务页

### 2. 仪表盘

- 合同总数
- 待处理合同数
- 本月签署金额
- 即将到期合同数

接口：

- `GET /api/dashboard/summary`

### 3. 合同管理

合同当前支持字段：

- `contractNo` 合同编号
- `contractName` 合同名称
- `contractType` 合同类型
- `counterparty` 相对方
- `amount` 合同金额
- `signDate` 签订日期
- `expireDate` 到期日期
- `status` 合同状态
- `attachment` 附件说明

合同当前状态：

- `待签署`
- `待审批`
- `履行中`
- `已归档`
- `已完成`
- `将过期`

### 4. 客商管理

支持字段：

- 客商名称
- 客商编号
- 联系人
- 联系电话
- 地址
- 状态

### 5. 系统设置

支持配置：

- 系统名称
- 系统主题色

## 快速开始

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd contract-code
```

### 2. 初始化数据库

执行数据库脚本：

```sql
source sql/schema.sql
```

默认数据库名：

```text
contract_cms
```

### 3. 配置后端

配置文件：

- `contract-api/src/main/resources/application.yml`

当前默认数据库连接：

```yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/contract_cms?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

如果你的本地 MySQL 用户名或密码不同，请按实际环境修改。

### 4. 启动后端

```bash
cd contract-api
mvn spring-boot:run
```

默认后端地址：

```text
http://localhost:8080/api
```

### 5. 启动前端

```bash
cd contract-ui
npm install
npm run dev
```

默认前端地址：

```text
http://localhost:5173
```

## 常用命令

### 后端测试

```bash
cd contract-api
mvn test
```

### 前端类型检查

```bash
cd contract-ui
npm exec vue-tsc -- --noEmit
```

### 前端生产构建

```bash
cd contract-ui
npm run build
```

## 核心接口

### 认证

- `POST /api/auth/login`
- `POST /api/auth/register`

### 仪表盘

- `GET /api/dashboard/summary`

### 合同

- `GET /api/contracts`
- `GET /api/contracts/{id}`
- `POST /api/contracts`
- `PUT /api/contracts/{id}`
- `DELETE /api/contracts/{id}`
- `POST /api/contracts/batchDelete`

### 客商

- `GET /api/merchant/list`
- `POST /api/merchant/add`
- `PUT /api/merchant/update`
- `DELETE /api/merchant/delete/{id}`

### 系统配置

- `GET /api/config/all`
- `PUT /api/config/update`

## 数据规则

- 合同数据按 `user_id` 隔离
- 客商数据按 `user_id` 隔离
- 合同编号规则：同一用户内唯一

## 项目亮点

- 前后端主链路已打通
- 主要业务页面不是静态假数据
- 支持主题色配置，UI 可快速换肤
- 合同页已经具备实际业务字段和状态流转展示
- 结构适合继续扩展审批流、附件上传、权限控制

## 后续可扩展方向

- 审批流配置
- 合同附件上传
- 合同导出 PDF
- 角色与权限管理
- 消息提醒
- 到期预警任务
- 操作日志
- 图表化统计报表

## 说明

当前仓库定位为业务原型项目，重点在功能演示与页面呈现。如果后续准备作为正式项目继续推进，建议继续补充：

- 密码加密存储
- 更细粒度的权限控制
- 更完整的异常处理
- 单元测试 / 集成测试
- 文件上传与对象存储
- 更规范的数据库迁移方案

