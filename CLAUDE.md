# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./gradlew assembleDebug       # сборка debug APK
./gradlew assembleRelease     # сборка release APK (minify + shrink)
./gradlew test                # unit-тесты (JVM)
./gradlew connectedCheck      # инструментальные тесты на устройстве/эмуляторе
```

Тесты запускаются в `app/src/test/` (unit) и `app/src/androidTest/` (инструментальные).

## Architecture

Однoмодульное Android-приложение. Пакет: `com.krayapp.buffercompanion.bargen`.

### Слои

```
data/room/           — Room DB (BargenDB v2), DAO, Entity, Repository
domain/              — Use cases, Selectors, BarGenerator/BarReader (ZXing), DI-модули
presentation/mvi/    — Orbit MVI: State, Intent, SideEffect, MviProcessor
presentation/viewmodels/ — BargenViewModel, SettingsViewModel, TagsViewModel
presentation/ui/     — Compose: mainScreen, bottomsheets, dialogs, composables
theme/               — Dimens, Color, Type, Shapes, CustomThemes
```

### MVI-паттерн (Orbit)

`MviProcessor` реализует `ContainerHost<MviState, SideEffect>` и содержит `container` через `viewModel.container(...)`. Он не является ViewModel сам по себе — создаётся внутри `BargenViewModel` и делегирует ему жизненный цикл. Интенты обрабатываются в `MviProcessor.onIntent()` и разбиты на обработчики (`MviProcessorHandler`, `PinHandler`).

Паджинг (`barcodePagingData`) живёт в `BargenViewModel` — `flatMapLatest` по `FilterState + SortType` создаёт новый `Pager` при изменении фильтра.

### DI (Koin)

Корень: `appModule` в `KoinAppModule.kt` → включает `bargenCoreModule`, `repositoryModule`, `tagsModule`, `selectorModule`. Все ViewModel-ы регистрируются через `viewModel { ... }` в `bargenCoreModule`.

### Настройки

`GlobalPrefs` — DataStore с миграцией из SharedPreferences (имя: `mainSettings`). Все поля доступны синхронно через `runBlocking { flow.first() }` и асинхронно через `Flow`. Использовать `Flow`-версии в UI/ViewModel.

### Spacing

Константы в `theme/Dimens.kt`: `xxsSize`=2dp, `xsSize`=4dp, `sSize`=8dp, `mSize`=12dp, `lSize`=16dp. Горизонтальные отступы экранов/листов — `lSize`, вертикальные — `mSize`.

### База данных

`BargenDB` версии 2. Сущности: `BarcodeEntity`, `TagEntity`. Единственная миграция — добавление колонки `pinnedPosition` (v1→v2). `exportSchema = false`.

## Текущая задача

Ветка `landscape-dialog`. Детальный план реализации — в `tasks/plan.md` (FullScreen info panel + закрытие BS при ShowFullScreen + unit-тесты).

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **Bargen** (1314 symbols, 3194 relationships, 103 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/Bargen/context` | Codebase overview, check index freshness |
| `gitnexus://repo/Bargen/clusters` | All functional areas |
| `gitnexus://repo/Bargen/processes` | All execution flows |
| `gitnexus://repo/Bargen/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->
