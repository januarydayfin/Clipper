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

## Ограничения

- Bitmap в диалоге генерируется независимо от уже сгенерированного в `ImageBlock` — незначительный overhead при открытии.
