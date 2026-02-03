# 示例：基于文件的规划实战

## 示例1：研究任务

**用户请求：** "研究晨练的好处并写一份摘要"

### 循环1：创建计划
```bash
Write task_plan.md
```

```markdown
# 任务计划：晨练好处研究

## 目标
创建关于晨练好处的研究摘要。

## 阶段
- [ ] 阶段1：创建此计划 ✓
- [ ] 阶段2：搜索和收集来源
- [ ] 阶段3：综合发现
- [ ] 阶段4：交付摘要

## 关键问题
1. 身体健康的益处有哪些？
2. 心理健康的益处有哪些？
3. 哪些科学研究支持这一点？

## 状态
**当前在阶段1** - 创建计划
```

### 循环2：研究
```bash
Read task_plan.md           # 刷新目标
WebSearch "morning exercise benefits"
Write notes.md              # 存储发现
Edit task_plan.md           # 标记阶段2完成
```

### 循环3：综合
```bash
Read task_plan.md           # 刷新目标
Read notes.md               # 获取发现
Write morning_exercise_summary.md
Edit task_plan.md           # 标记阶段3完成
```

### 循环4：交付
```bash
Read task_plan.md           # 验证完成
Deliver morning_exercise_summary.md
```

---

## 示例2：Bug修复任务

**用户请求：** "修复认证模块中的登录bug"

### task_plan.md
```markdown
# 任务计划：修复登录Bug

## 目标
识别并修复阻止成功登录的bug。

## 阶段
- [x] 阶段1：理解bug报告 ✓
- [x] 阶段2：定位相关代码 ✓
- [ ] 阶段3：识别根本原因（当前）
- [ ] 阶段4：实施修复
- [ ] 阶段5：测试和验证

## 关键问题
1. 出现什么错误消息？
2. 哪个文件处理认证？
3. 最近有什么变化？

## 做出的决策
- 认证处理程序在 src/auth/login.ts
- 错误发生在 validateToken() 函数中

## 遇到的错误
- [初始] TypeError: Cannot read property 'token' of undefined
  → 根本原因：user对象没有正确等待

## 状态
**当前在阶段3** - 找到根本原因，准备修复
```

---

## 示例3：功能开发

**用户请求：** "在设置页面添加深色模式切换"

### 3文件模式实战

**task_plan.md:**
```markdown
# 任务计划：深色模式切换

## 目标
在设置中添加功能性深色模式切换。

## 阶段
- [x] 阶段1：研究现有主题系统 ✓
- [x] 阶段2：设计实现方法 ✓
- [ ] 阶段3：实现切换组件（当前）
- [ ] 阶段4：添加主题切换逻辑
- [ ] 阶段5：测试和优化

## 做出的决策
- 使用CSS自定义属性实现主题
- 在localStorage中存储偏好
- 切换组件在 SettingsPage.tsx

## 状态
**当前在阶段3** - 构建切换组件
```

**notes.md:**
```markdown
# 笔记：深色模式实现

## 现有主题系统
- 位置：src/styles/theme.ts
- 使用：CSS自定义属性
- 当前主题：仅浅色

## 要修改的文件
1. src/styles/theme.ts - 添加深色主题颜色
2. src/components/SettingsPage.tsx - 添加切换
3. src/hooks/useTheme.ts - 创建新钩子
4. src/App.tsx - 用ThemeProvider包装

## 颜色决策
- 深色背景：#1a1a2e
- 深色表面：#16213e
- 深色文本：#eaeaea
```

**dark_mode_implementation.md:** (可交付成果)
```markdown
# 深色模式实现

## 做出的更改

### 1. 添加了深色主题颜色
文件：src/styles/theme.ts
...

### 2. 创建了useTheme钩子
文件：src/hooks/useTheme.ts
...
```

---

## 示例4：错误恢复模式

当某些事情失败时，不要隐藏它：

### 之前（错误）
```
Action: Read config.json
Error: File not found
Action: Read config.json  # 静默重试
Action: Read config.json  # 再次重试
```

### 之后（正确）
```
Action: Read config.json
Error: File not found

# 更新 task_plan.md:
## 遇到的错误
- config.json 未找到 → 将创建默认配置

Action: Write config.json (默认配置)
Action: Read config.json
Success!
```

---

## 决策前阅读模式

**在重大决策前总是阅读你的计划：**

```
[发生了许多工具调用...]
[上下文变得很长...]
[原始目标可能被遗忘...]

→ Read task_plan.md          # 这使目标重新进入注意力！
→ 现在做决定                 # 目标在上下文中是新鲜的
```