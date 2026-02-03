---
name: planning-with-files
description: "实现Manus风格基于文件的复杂任务规划。创建task_plan.md、findings.md和progress.md文件。适用于开始复杂多步骤任务、研究项目或任何需要超过5次工具调用的任务。现在支持在/clear后自动会话恢复。"
---

# 基于文件的规划

像Manus一样工作：使用持久的markdown文件作为你的"磁盘工作记忆"。

## 第一步：检查之前的会话 (v2.2.0)

**在开始工作之前**，检查之前会话中未同步的上下文：

```bash
# Linux/macOS (自动检测python3或python)
$(command -v python3 || command -v python) ${CLAUDE_PLUGIN_ROOT}/scripts/session-catchup.py "$(pwd)"
```

```powershell
# Windows PowerShell
python "$env:USERPROFILE\.cursor\skills\planning-with-files\scripts\session-catchup.py" (Get-Location)
```

如果catchup报告显示未同步的上下文：
1. 运行 `git diff --stat` 查看实际的代码更改
2. 读取当前的规划文件
3. 基于catchup + git diff更新规划文件
4. 然后继续任务

## 重要：文件存放位置

- **模板** 位于 `${CLAUDE_PLUGIN_ROOT}/templates/`
- **你的规划文件** 放在 **你的项目目录中**

| 位置 | 存放内容 |
|----------|-----------------|
| 技能目录 (`${CLAUDE_PLUGIN_ROOT}/`) | 模板、脚本、参考文档 |
| 你的项目目录 | `task_plan.md`、`findings.md`、`progress.md` |

## 快速开始

在任何复杂任务之前：

1. **创建 `task_plan.md`** — 使用 [templates/task_plan.md](templates/task_plan.md) 作为参考
2. **创建 `findings.md`** — 使用 [templates/findings.md](templates/findings.md) 作为参考
3. **创建 `progress.md`** — 使用 [templates/progress.md](templates/progress.md) 作为参考
4. **在决策前重新阅读计划** — 刷新注意力窗口中的目标
5. **在每个阶段后更新** — 标记完成，记录错误

> **注意：** 规划文件放在你的项目根目录，而不是技能安装文件夹。

## 核心模式

```
上下文窗口 = 内存（易失性，有限）
文件系统 = 磁盘（持久性，无限）

→ 任何重要内容都写入磁盘。
```

## 文件用途

| 文件 | 用途 | 何时更新 |
|------|---------|----------------|
| `task_plan.md` | 阶段、进度、决策 | 每个阶段后 |
| `findings.md` | 研究、发现 | 任何发现后 |
| `progress.md` | 会话日志、测试结果 | 整个会话期间 |

## 关键规则

### 1. 先创建计划
没有 `task_plan.md` 绝不开始复杂任务。不可协商。

### 2. 2-操作规则
> "每2次查看/浏览/搜索操作后，立即将关键发现保存到文本文件。"

这防止视觉/多模态信息丢失。

### 3. 决策前阅读
在重大决策前，阅读计划文件。这使目标保持在你的注意力窗口中。

### 4. 行动后更新
完成任何阶段后：
- 标记阶段状态：`in_progress` → `complete`
- 记录遇到的任何错误
- 注意创建/修改的文件

### 5. 记录所有错误
每个错误都进入计划文件。这建立知识并防止重复。

```markdown
## 遇到的错误
| 错误 | 尝试次数 | 解决方案 |
|-------|---------|------------|
| FileNotFoundError | 1 | 创建默认配置 |
| API超时 | 2 | 添加重试逻辑 |
```

### 6. 绝不重复失败
```
如果操作失败：
    下一个操作 ≠ 相同操作
```
跟踪你尝试过的内容。改变方法。

## 3次错误协议

```
尝试1：诊断与修复
  → 仔细阅读错误
  → 识别根本原因
  → 应用针对性修复

尝试2：替代方法
  → 相同错误？尝试不同方法
  → 不同工具？不同库？
  → 绝不重复完全相同的失败操作

尝试3：更广泛重新思考
  → 质疑假设
  → 搜索解决方案
  → 考虑更新计划

3次失败后：向用户升级
  → 解释你尝试了什么
  → 分享具体错误
  → 请求指导
```

## 读 vs 写决策矩阵

| 情况 | 操作 | 原因 |
|-----------|--------|--------|
| 刚写完文件 | 不要读 | 内容仍在上下文中 |
| 查看图像/PDF | 立即写发现 | 多模态 → 文本在丢失前 |
| 浏览器返回数据 | 写入文件 | 截图不持久 |
| 开始新阶段 | 阅读计划/发现 | 如果上下文陈旧则重新定位 |
| 发生错误 | 阅读相关文件 | 需要当前状态来修复 |
| 中断后恢复 | 阅读所有规划文件 | 恢复状态 |

## 5问题重启测试

如果你能回答这些问题，你的上下文管理就很扎实：

| 问题 | 答案来源 |
|----------|---------------|
| 我在哪里？ | task_plan.md中的当前阶段 |
| 我要去哪里？ | 剩余阶段 |
| 目标是什么？ | 计划中的目标声明 |
| 我学到了什么？ | findings.md |
| 我做了什么？ | progress.md |

## 何时使用此模式

**适用于：**
- 多步骤任务（3+步骤）
- 研究任务
- 构建/创建项目
- 需要大量工具调用的任务
- 任何需要组织的任务

**跳过：**
- 简单问题
- 单文件编辑
- 快速查找

## 模板

复制这些模板开始：

- [templates/task_plan.md](templates/task_plan.md) — 阶段跟踪
- [templates/findings.md](templates/findings.md) — 研究存储
- [templates/progress.md](templates/progress.md) — 会话日志

## 脚本

用于自动化的辅助脚本：

- `scripts/init-session.sh` — 初始化所有规划文件
- `scripts/check-complete.sh` — 验证所有阶段完成
- `scripts/session-catchup.py` — 从之前会话恢复上下文 (v2.2.0)

## 高级主题

- **Manus原则：** 参见 [reference.md](reference.md)
- **真实示例：** 参见 [examples.md](examples.md)

## 反模式

| 不要 | 改为 |
|-------|------------|
| 使用TodoWrite实现持久性 | 创建task_plan.md文件 |
| 陈述目标一次就忘记 | 决策前重新阅读计划 |
| 隐藏错误并静默重试 | 记录错误到计划文件 |
| 将所有内容塞入上下文 | 将大内容存储在文件中 |
| 立即开始执行 | 首先创建计划文件 |
| 重复失败的操作 | 跟踪尝试，改变方法 |
| 在技能目录中创建文件 | 在你的项目中创建文件 |