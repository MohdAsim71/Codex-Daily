# Kotlin/Compose → Swift/SwiftUI Reference Guide

A side-by-side cheat sheet for Android devs learning iOS development.

---

## 1. Language Basics (Kotlin vs Swift)

### Variables

```kotlin
// Kotlin
val name = "John"       // immutable (like Swift's let)
var age = 25             // mutable (like Swift's var)
val price: Double = 9.99 // explicit type
```

```swift
// Swift
let name = "John"        // immutable
var age = 25              // mutable
let price: Double = 9.99  // explicit type
```
👉 `val` = `let`, `var` = `var`. Basically identical.

---

### Functions

```kotlin
// Kotlin
fun greet(name: String): String {
    return "Hello, $name"
}

fun add(a: Int, b: Int = 0): Int = a + b   // default param + expression body
```

```swift
// Swift
func greet(name: String) -> String {
    return "Hello, \(name)"
}

func add(a: Int, b: Int = 0) -> Int {
    return a + b
}
```
👉 `fun` → `func`, `->` return type stays similar, string templates: `$name` → `\(name)`.

⚠️ Swift has **argument labels** — a quirk Kotlin doesn't have:
```swift
func greet(name: String) { ... }
greet(name: "John")   // must include "name:" when calling, by default!

func greet(_ name: String) { ... }
greet("John")          // "_" removes the label requirement
```

---

### For Loop

```kotlin
// Kotlin
for (i in 1..5) {
    println(i)
}

for (i in 0 until 10) {
    println(i)
}

val items = listOf("a", "b", "c")
for (item in items) {
    println(item)
}

items.forEach { println(it) }
```

```swift
// Swift
for i in 1...5 {
    print(i)
}

for i in 0..<10 {
    print(i)
}

let items = ["a", "b", "c"]
for item in items {
    print(item)
}

items.forEach { print($0) }
```
👉 `1..5` (inclusive) → `1...5`. `0 until 10` (exclusive) → `0..<10`. `it` → `$0`.

---

### While Loop

```kotlin
var i = 0
while (i < 5) {
    i++
}
```

```swift
var i = 0
while i < 5 {
    i += 1     // no ++ operator in Swift!
}
```
⚠️ Swift removed `++` / `--`. Use `i += 1`.

---

### If / Else & When / Switch

```kotlin
// Kotlin
val result = if (score > 90) "A" else "B"

when (score) {
    in 90..100 -> println("A")
    in 80..89 -> println("B")
    else -> println("F")
}
```

```swift
// Swift
let result = score > 90 ? "A" : "B"   // no if-as-expression, use ternary

switch score {
case 90...100:
    print("A")
case 80...89:
    print("B")
default:
    print("F")
}
```
👉 `when` → `switch`, `else ->` → `default:`, Kotlin's `in 90..100` → Swift's `90...100`.

---

### Null Safety

```kotlin
var name: String? = null
name?.let { println(it) }
val length = name?.length ?: 0    // Elvis operator
val forced = name!!               // force unwrap
```

```swift
var name: String? = nil
if let name = name {
    print(name)
}
let length = name?.count ?? 0     // nil-coalescing operator
let forced = name!                // force unwrap
```
👉 `?.let { }` → `if let`, `?:` → `??`. Both languages are equally strict about null/nil safety — this is the concept you already understand deeply, just different keywords.

---

### Classes / Data Classes

```kotlin
data class User(val name: String, val age: Int)

val user = User("John", 25)
val older = user.copy(age = 26)
```

```swift
struct User {
    let name: String
    let age: Int
}

let user = User(name: "John", age: 25)
var older = user
older.age = 26   // structs need `var` and a `mutating` change, no built-in copy()
```
⚠️ Big mental shift: in SwiftUI, prefer **`struct`** over `class` for models (value types). Classes exist too (reference types, like Kotlin `class`), but SwiftUI leans heavily on structs + `Codable` instead of Kotlin's `data class`.

---

### Collections

```kotlin
val list = listOf(1, 2, 3)
val mutableList = mutableListOf(1, 2, 3)
val mapped = list.map { it * 2 }
val filtered = list.filter { it > 1 }
```

```swift
let list = [1, 2, 3]
var mutableList = [1, 2, 3]      // var makes it mutable, no separate type
let mapped = list.map { $0 * 2 }
let filtered = list.filter { $0 > 1 }
```
👉 Swift arrays don't have separate `List`/`MutableList` types — mutability comes from `let` vs `var`.

---

## 2. UI Framework Basics (Compose vs SwiftUI)

### Layout Containers

```kotlin
// Compose
Column {
    Text("Top")
    Text("Bottom")
}

Row {
    Text("Left")
    Text("Right")
}

Box {
    Text("Overlay")
}
```

```swift
// SwiftUI
VStack {
    Text("Top")
    Text("Bottom")
}

HStack {
    Text("Left")
    Text("Right")
}

ZStack {
    Text("Overlay")
}
```
👉 `Column` → `VStack`, `Row` → `HStack`, `Box` → `ZStack` (Z = depth/overlap axis).

---

### Text & Buttons

```kotlin
Text(
    text = "Hello",
    fontSize = 20.sp,
    color = Color.Blue
)

Button(onClick = { println("Clicked") }) {
    Text("Click Me")
}
```

```swift
Text("Hello")
    .font(.system(size: 20))
    .foregroundColor(.blue)

Button(action: { print("Clicked") }) {
    Text("Click Me")
}
```
👉 Compose passes params inline `Text(text = ...)`. SwiftUI uses **modifier chaining**: `.font()`, `.foregroundColor()` etc., applied after the view — this is the single biggest mental shift coming from Compose.

---

### Modifiers

```kotlin
Text(
    "Hello",
    modifier = Modifier
        .padding(16.dp)
        .background(Color.Gray)
        .fillMaxWidth()
)
```

```swift
Text("Hello")
    .padding(16)
    .background(Color.gray)
    .frame(maxWidth: .infinity)
```
👉 Compose bundles everything into a single `Modifier` object passed as a parameter. SwiftUI just **chains methods directly on the view** — no separate Modifier object. Order matters in both (padding-then-background ≠ background-then-padding).

---

### State Management

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Column {
        Text("Count: $count")
        Button(onClick = { count++ }) {
            Text("Increment")
        }
    }
}
```

```swift
struct Counter: View {
    @State private var count = 0

    var body: some View {
        VStack {
            Text("Count: \(count)")
            Button("Increment") {
                count += 1
            }
        }
    }
}
```
👉 `remember { mutableStateOf(0) }` → `@State private var count = 0`. Both trigger a re-render/recompose automatically when the value changes.

---

### Composable Function vs View Struct

```kotlin
@Composable
fun Greeting(name: String) {
    Text("Hello, $name!")
}
```

```swift
struct Greeting: View {
    let name: String

    var body: some View {
        Text("Hello, \(name)!")
    }
}
```
👉 A `@Composable fun` maps to a `struct: View` with a computed `body` property. Every SwiftUI view **must** have `var body: some View { }`.

---

### Passing Data Down / State Up

```kotlin
@Composable
fun Parent() {
    var text by remember { mutableStateOf("") }
    Child(value = text, onValueChange = { text = it })
}

@Composable
fun Child(value: String, onValueChange: (String) -> Unit) {
    TextField(value = value, onValueChange = onValueChange)
}
```

```swift
struct Parent: View {
    @State private var text = ""

    var body: some View {
        Child(value: $text)   // note the $ — creates a Binding
    }
}

struct Child: View {
    @Binding var value: String   // two-way binding, like a shared source of truth

    var body: some View {
        TextField("Enter text", text: $value)
    }
}
```
👉 Compose passes value + lambda callback manually. SwiftUI has a built-in **`@Binding`** + `$` prefix that does two-way binding for you — less boilerplate, but a new concept to learn.

---

### Lists

```kotlin
LazyColumn {
    items(itemList) { item ->
        Text(item)
    }
}
```

```swift
List(itemList, id: \.self) { item in
    Text(item)
}

// or, more manual/performant equivalent of LazyColumn:
ScrollView {
    LazyVStack {
        ForEach(itemList, id: \.self) { item in
            Text(item)
        }
    }
}
```
👉 `LazyColumn` → `List` (comes with built-in styling/dividers) or `ScrollView { LazyVStack { ForEach } }` for a bare-bones equivalent.

---

### Navigation

```kotlin
// Compose (Navigation-Compose)
NavHost(navController, startDestination = "home") {
    composable("home") { HomeScreen(navController) }
    composable("detail") { DetailScreen() }
}
// navigate:
navController.navigate("detail")
```

```swift
// SwiftUI
NavigationStack {
    HomeScreen()
}

// inside HomeScreen:
NavigationLink("Go to Detail") {
    DetailScreen()
}
// or programmatically:
NavigationStack(path: $path) {
    HomeScreen()
        .navigationDestination(for: String.self) { value in
            DetailScreen()
        }
}
```

---

### Side Effects / Lifecycle

```kotlin
LaunchedEffect(key1 = true) {
    // runs once when composable enters composition
    fetchData()
}
```

```swift
.onAppear {
    // runs when view appears
    fetchData()
}

.task {
    // preferred for async work — auto-cancels when view disappears
    await fetchData()
}
```

---

## 3. Quick Lookup Table

| Concept | Kotlin | Swift |
|---|---|---|
| Immutable var | `val` | `let` |
| Mutable var | `var` | `var` |
| Function | `fun` | `func` |
| String template | `"$name"` | `"\(name)"` |
| Nullable type | `String?` | `String?` |
| Null-coalescing | `?:` | `??` |
| Lambda param | `it` | `$0` |
| Range (inclusive) | `1..5` | `1...5` |
| Range (exclusive) | `0 until 10` | `0..<10` |
| Switch statement | `when` | `switch` |
| Data class | `data class` | `struct` |

| Concept | Compose | SwiftUI |
|---|---|---|
| Vertical layout | `Column` | `VStack` |
| Horizontal layout | `Row` | `HStack` |
| Overlay layout | `Box` | `ZStack` |
| Styling | `Modifier` param | chained `.modifier()` |
| Local state | `remember { mutableStateOf() }` | `@State` |
| Passed-down state | value + lambda | `@Binding` + `$` |
| Lazy list | `LazyColumn` | `List` / `LazyVStack` |
| Function-based UI | `@Composable fun` | `struct: View { body }` |
| Run once on appear | `LaunchedEffect` | `.onAppear` / `.task` |

---

## 4. Suggested Learning Order
1. Swift basics (variables, functions, optionals, structs) — 2-3 days, will feel very familiar
2. SwiftUI layout basics (VStack/HStack/ZStack, Text, Button, modifiers) — instantly transferable from Compose intuition
3. `@State` and `@Binding` — the trickiest new concept, spend extra time here
4. Lists and `ForEach`
5. Navigation (`NavigationStack`)
6. Networking with `async/await` (very similar to Kotlin coroutines!) + `Codable` (like `@Serializable`)

Good news: `async/await` in Swift works almost identically to Kotlin coroutines conceptually, so that transition will be smooth once you get there.