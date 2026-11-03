<p align="center">
  <img src="docs/logo.png" alt="TIANZE 天泽" width="128" height="128" />
</p>

<h1 align="center">TIANZE · 天泽</h1>

<p align="center">
  高质量多租户中后台管理系统<br/>
  开箱即用 · 前后端分离 · 持续迭代
</p>

<p align="center">
  <a href="https://github.com/Shichuan-Hao/tianze"><img src="https://img.shields.io/badge/RELEASE-v4.2.0-%230B6E6E.svg" alt="Release" /></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.5-6CB52D.svg?logo=Spring-Boot" alt="Spring Boot" /></a>
  <a href="https://vuejs.org/"><img src="https://img.shields.io/badge/Vue-3.5-42b883.svg?logo=Vue.js" alt="Vue" /></a>
  <a href="https://github.com/Shichuan-Hao/tianze/blob/master/tianze-admin/LICENSE"><img src="https://img.shields.io/badge/License-Apache--2.0-blue.svg" alt="License" /></a>
</p>

---

## 简介

**天泽（TIANZE）** 是一套面向企业场景的多租户中后台管理系统，前后端分离，强调代码规范与开发体验。

当前仓库为单体源码仓，包含：

| 目录 | 说明 |
| :--- | :--- |
| [`tianze-admin`](./tianze-admin) | 后端：Spring Boot 3 / Java 17 / Maven 多模块 |
| [`tianze-admin-ui`](./tianze-admin-ui) | 前端：Vue 3 / Arco Design / TypeScript / Vite |

技术栈基线包括：Sa-Token、MyBatis Plus、Redisson、JetCache、JustAuth、FastExcel、Hutool，以及 ContiNew Starter 等。

## 仓库结构

```text
tianze/
├── tianze-admin/          # 后端服务
│   ├── tianze-server/     # 启动与打包模块
│   ├── tianze-system/     # 系统管理业务
│   ├── tianze-common/     # 公共能力
│   ├── tianze-plugin/     # 租户 / 调度 / 开放能力 / 代码生成
│   └── tianze-extension/  # 扩展服务（如调度服务端）
├── tianze-admin-ui/       # 前端应用
└── docs/                  # 品牌资源等
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+（推荐使用仓库自带 `mvnw`）
- Node.js 18+ / pnpm 9+
- MySQL、Redis（按本地配置）

### 后端

```bash
cd tianze-admin
./mvnw -pl tianze-server -am spring-boot:run
```

默认开发环境端口：`8000`。

### 前端

```bash
cd tianze-admin-ui
pnpm bootstrap
pnpm dev
```

更多细节见：

- 后端说明：[tianze-admin/README.md](./tianze-admin/README.md)
- 前端说明：[tianze-admin-ui/README.md](./tianze-admin-ui/README.md)
- 后端文档：[`tianze-admin/docs`](./tianze-admin/docs)

## 品牌含义

**天泽** 取自《易经》卦象 **天泽履**：上乾为天，下兑为泽。寓意行于天地之间，履道坦坦。英文标识统一为 **tianze**。

## Logo / 吉祥物

吉祥物为 **小鹿**（棉花粘土卡通风），无背景，对应「天泽」温和、吉祥的意象。

| 文件 | 用途 |
| :--- | :--- |
| [`docs/logo.png`](./docs/logo.png) | README / 主 Logo |
| [`docs/logo.jpg`](./docs/logo.jpg) | 原图备份 |

主题色：暖棕与米白，青绿点缀鹿角。

## 版本与许可证

- 当前版本：`4.2.0`
- 许可证：Apache-2.0（见各子项目 `LICENSE`）
- 远程仓库：[https://github.com/Shichuan-Hao/tianze](https://github.com/Shichuan-Hao/tianze)

## 问题反馈

欢迎通过 [GitHub Issues](https://github.com/Shichuan-Hao/tianze/issues) 提交问题与建议。
