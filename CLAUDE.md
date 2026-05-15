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
