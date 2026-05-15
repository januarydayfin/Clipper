# Feature Plan: Landscape Info Dialog

## Описание фичи

Из `MainBottomSheet` добавляется кнопка, которая открывает полноразмерный диалог с read-only информацией о баркоде в горизонтальном layout (изображение слева, текст справа). Боттомшит при этом остаётся открытым.

---

## Декомпозиция задач

### Task 1 — Добавить иконку в git

**Файлы:**
- `app/src/main/res/drawable/ic_landscape.xml` (уже существует, не в git)

**Что сделать:**
- Добавить `ic_landscape.xml` в git: `git add app/src/main/res/drawable/ic_landscape.xml`

**Критерии готовности:**
- Файл отслеживается git

**Зависимости:** нет

**Размер:** XS

---

### Task 2 — Кнопка в `ImageBlock`

**Файлы:**
- `app/src/main/java/.../presentation/ui/bottomsheets/mainBottomSheet/ImageBlock.kt`

**Что сделать:**
- Добавить параметр `onOpenInfoDialog: () -> Unit = {}` к `ImageBlock`
- Добавить третью кнопку в `Row` рядом с «Скачать» и «Поделиться»: маленькая круглая `IconButton` (без текста) с иконкой `ic_landscape`
- При нажатии вызывать `onOpenInfoDialog()` — БШ остаётся открытым, состояние диалога живёт снаружи

**Критерии готовности:**
- Три кнопки выровнены горизонтально в `Arrangement.Center`
- Нажатие не закрывает `ModalBottomSheet`
- Кнопка видна только когда `bmp != null` (как Save/Share)

**Зависимости:** Task 1

**Размер:** XS

---

### Task 3 — Локальное состояние диалога в `MainBottomSheet`

**Файлы:**
- `app/src/main/java/.../presentation/ui/bottomsheets/mainBottomSheet/MainBottomSheet.kt`

**Что сделать:**
- Добавить `val infoDialogOpened = remember { mutableStateOf(false) }`
- Пробросить в `ImageBlock`: `onOpenInfoDialog = { infoDialogOpened.value = true }`
- При `infoDialogOpened.value == true` показывать `BarcodeInfoDialog(model = modelState.value, onDismiss = { infoDialogOpened.value = false })`

**Критерии готовности:**
- Диалог открывается поверх BS, BS остаётся под ним
- После закрытия диалога BS снова активен

**Зависимости:** Task 2, Task 4

**Размер:** XS

---

### Task 4 — Создать `BarcodeInfoDialog`

**Файлы:**
- `app/src/main/java/.../presentation/ui/dialogs/BarcodeInfoDialog.kt` *(новый файл)*

**Сигнатура:**
```kotlin
@Composable
fun BarcodeInfoDialog(model: BarcodeUiModel, onDismiss: () -> Unit)
```

**Layout (горизонтальный, landscape-style):**
```
┌─────────────────────────────────────────────────┐
│  [X]                                            │
│  ┌──────────┐  ┌──────────────────────────────┐ │
│  │          │  │ QR_CODE                      │ │
│  │  Barcode │  │ Content: ...                 │ │
│  │  Image   │  │ Name: ...                    │ │
│  │          │  │ Description: ...             │ │
│  └──────────┘  │ [tag1] [tag2]                │ │
│                └──────────────────────────────┘ │
└─────────────────────────────────────────────────┘
```

**Детали реализации:**
- Использовать `BasicAlertDialog` + `Card` (как в `BarcodeFormatDialog`) с `DialogProperties(usePlatformDefaultWidth = false)`
- Внутри `Card`: `Column` → Row с `[X]`-кнопкой вверху справа + горизонтальный `Row` с двумя колонками
- Левая колонка (`weight(0.45f)`): изображение баркода. Bitmap генерируется через `koinInject<BarGenerator>()` + `LaunchedEffect(model.content, model.barcodeType)` — аналогично `ImageBlock`. Если bitmap null — показывать `CircularProgressIndicator`
- Правая колонка (`weight(0.55f)`): `VerticalScroll` с:
  - `Text` — тип баркода (`model.barcodeType`), `labelMedium` стиль
  - `Text` — content (если непусто)
  - `Text` — name (если непусто)
  - `Text` — description (если непусто)
  - `FlowRow` — теги через `BargenChip(model = tag, selectable = false)`
- Отступы: `lSize` горизонтально, `mSize` вертикально (по стандарту проекта)
- Кнопка закрыть: `IconButton` с иконкой `outline_cancel_24`, расположен в `Box` с `Alignment.TopEnd` или в Row над основным контентом

**Критерии готовности:**
- Все поля read-only (Text, не TextField)
- Пустые поля (name/description = "") не отображаются
- Теги с `selectable = false` — нет hover/click эффекта
- Bitmap генерируется и отображается
- Диалог закрывается по кнопке X
- Корректные отступы от краёв и между элементами

**Зависимости:** нет

**Размер:** M

---

### Task 5 — Строковые ресурсы

**Файлы:**
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-ru/strings.xml` *(если есть)*

**Что добавить:**
- Строки для label-ов полей если ещё не существуют (`content`, `name`, `description` скорее всего уже есть)
- `contentDescription` для кнопки-иконки (accessibility)

**Зависимости:** нет

**Размер:** XS

---

## Граф зависимостей

```
Task 1 (иконка в git)
    └── Task 2 (кнопка в ImageBlock)
            └── Task 3 (состояние в MainBottomSheet)
                    └── (ждёт Task 4)

Task 4 (BarcodeInfoDialog) ──────────────────────────────┘
Task 5 (строки) ──────────────────── Task 2 (строка кнопки)
```

**Порядок реализации:** Task 1 → Task 4 → Task 5 → Task 2 → Task 3

---

## Архитектурные решения

| Решение | Обоснование |
|---|---|
| Состояние диалога — локальный `remember` в `MainBottomSheet`, не MVI | Диалог — чисто UI-событие без изменения бизнес-данных. MVI не нужен |
| Bitmap генерируется в самом диалоге через koinInject | Не требует пробрасывания через callback-цепочку; паттерн уже используется в ImageBlock |
| `BasicAlertDialog` + `Card` + `DialogProperties(usePlatformDefaultWidth = false)` | Соответствует стилю BarcodeFormatDialog; `usePlatformDefaultWidth = false` нужен для широкого горизонтального layout |
| Горизонтальный `Row` (image left, info right) | Отвечает ТЗ «горизонтальная ориентация»; даёт место для bitmap |
| `selectable = false` у `BargenChip` | Уже поддерживается компонентом — отключает клики и haptic |

---

## Файлы, которые будут затронуты

| Файл | Изменение |
|---|---|
| `ImageBlock.kt` | + параметр `onOpenInfoDialog`, + третья кнопка |
| `MainBottomSheet.kt` | + `infoDialogOpened` state, + условный показ диалога |
| `BarcodeInfoDialog.kt` | **новый файл** |
| `res/values/strings.xml` | + новые строки |
| `res/drawable/ic_landscape.xml` | добавить в git |

---

## Чеклист проверки

- [ ] `./gradlew assembleDebug` без ошибок
- [ ] Открыть BS → нажать кнопку → диалог открыт, BS виден под ним
- [ ] В диалоге: изображение, тип, content, name, description, теги — всё read-only
- [ ] Пустые поля не отображаются
- [ ] Закрытие по кнопке X — BS остаётся открытым
- [ ] Теги некликабельны (нет визуального feedback)
