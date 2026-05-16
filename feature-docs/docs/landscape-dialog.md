# Landscape Info Dialog

## Назначение

Диалог полноэкранного просмотра штрихкода в горизонтальной ориентации. Открывается из `MainBottomSheet` по нажатию на иконку горизонтального просмотра. Боттомшит при этом остаётся открытым — диалог показывается поверх него.

---

## Спецификация

### Точка входа

**Файл:** `ImageBlock.kt`  
**Компонент:** `FilledTonalIconButton` с иконкой `ic_landscape`, расположен в ряду рядом с кнопками «Сохранить» и «Поделиться».  
**Условие отображения:** кнопка видна только при наличии сгенерированного bitmap (то есть при непустом `model.content`).

### Управление состоянием

Состояние открытия/закрытия — локальный `remember { mutableStateOf(false) }` в `MainBottomSheet`. MVI не затрагивается, так как диалог является чисто UI-событием без изменения бизнес-данных.

```
MainBottomSheet
    └── infoDialogOpened: MutableState<Boolean>
            ├── ImageBlock(onOpenInfoDialog = { infoDialogOpened.value = true })
            └── if (infoDialogOpened.value) BarcodeInfoDialog(onDismiss = { ... = false })
```

### Диалог `BarcodeInfoDialog`

**Файл:** `presentation/ui/dialogs/BarcodeInfoDialog.kt`

**Размер:** 95% ширины × 80% высоты экрана.

**Layout:**

```
┌─────────────────────────────────────┐
│                                 [X] │
│                                     │
│       ╔═══════════════════╗         │
│       ║   Barcode image   ║         │
│       ║   (rotate 90°,    ║         │
│       ║    scale 2x)      ║         │
│       ╚═══════════════════╝         │
│                                     │
└─────────────────────────────────────┘
```

**Компоненты:**
- `BasicAlertDialog` + `DialogProperties(usePlatformDefaultWidth = false)`
- `Surface(shape = lRoundedCornerShape)` — `fillMaxWidth(0.95f)` × `fillMaxHeight(0.8f)`
- Изображение штрихкода: `rotate(90f)` + `scale(2f)` + `ContentScale.Fit`. До готовности — `CircularProgressIndicator`
- Кнопка закрытия: `IconButton` с иконкой `outline_cancel_24` в правом верхнем углу

### Генерация bitmap

Внутри `BarcodeInfoDialog` bitmap генерируется через `koinInject<BarGenerator>()` и `LaunchedEffect(model.content, model.barcodeType)`. При изменении контента или типа баркода bitmap перегенерируется автоматически.

---

## Затронутые файлы

| Файл | Изменение |
|------|-----------|
| `ImageBlock.kt` | Параметр `onOpenInfoDialog: () -> Unit`, новая кнопка `FilledTonalIconButton` |
| `MainBottomSheet.kt` | `infoDialogOpened` state, показ `BarcodeInfoDialog` |
| `BarcodeInfoDialog.kt` | Новый файл |
| `res/values/strings.xml` | Строка `landscape_view` |
| `res/drawable/ic_landscape.xml` | Добавлен в git |

---

### Опциональный флаг 1: автооткрытие при открытии BS

**Настройка:** «Открывать горизонтальный режим при открытии карточки» / «Show barcode in landscape on open»  
**По умолчанию:** выключен.

Когда флаг включён, `BarcodeInfoDialog` открывается автоматически сразу после появления `MainBottomSheet`. Реализован через `LaunchedEffect(Unit)` — при `autoOpenLandscape = true` устанавливает `infoDialogOpened = true`.

### Опциональный флаг 2: автоскрытие BS после закрытия диалога

**Настройка:** «Скрывать карточку после закрытия горизонтального режима» / «Hide card after closing landscape view»  
**По умолчанию:** выключен.

Когда флаг включён, при закрытии `BarcodeInfoDialog` вызывается `onDismiss()` боттомшита, скрывая его вместе с диалогом.

Оба флага передаются по одинаковой цепочке:
```
GlobalPrefs.[флаг]
    └── SettingsViewModel (подписка на Flow + SettingsIntent)
    └── CheckboxSection (UI)
    └── prefs.[флаг] → MainActivity.ShowMainBottomSheet()
            └── MainBottomSheet(autoOpenLandscape, hideBsAfterLandscapeClose)
```

---

## Затронутые файлы

| Файл | Изменение |
|------|-----------|
| `ImageBlock.kt` | Параметр `onOpenInfoDialog: () -> Unit`, новая кнопка `FilledTonalIconButton` |
| `MainBottomSheet.kt` | `infoDialogOpened` state, параметры `autoOpenLandscape` и `hideBsAfterLandscapeClose`, `LaunchedEffect` |
| `MainActivity.kt` | Передача обоих флагов из `prefs` |
| `BarcodeInfoDialog.kt` | Новый файл |
| `GlobalPrefs.kt` | Ключи `OPEN_LANDSCAPE_ON_BS_OPEN`, `HIDE_BS_AFTER_LANDSCAPE_CLOSE`, Flow и свойства |
| `SettingsState.kt` | Поля `openLandscapeOnBsOpen`, `hideBsAfterLandscapeClose` |
| `SettingsIntent.kt` | `UpdateOpenLandscapeOnBsOpen`, `UpdateHideBsAfterLandscapeClose` |
| `SettingsViewModel.kt` | Подписки на Flow, обработчики интентов |
| `CheckboxSection.kt` | Два новых `TextCheckbox` |
| `res/values/strings.xml` | `landscape_view`, `open_landscape_on_open`, `hide_bs_after_landscape_close` |
| `res/values-ru/strings.xml` | Переводы всех трёх строк |
| `res/drawable/ic_landscape.xml` | Добавлен в git |

---

## Ограничения

- Bitmap в диалоге генерируется независимо от уже сгенерированного в `ImageBlock` — незначительный overhead при открытии.
