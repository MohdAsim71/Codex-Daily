# SwiftUI Basics for Android (Jetpack Compose) Developers

This guide explains common SwiftUI concepts by comparing them with Jetpack Compose.

---

# 1. Static Color

### SwiftUI

```swift
struct AppColors {
    static let blue = Color("blue")
}
```

## What is `static`?

`static` means the property belongs to the type itself instead of an object.

You don't need to create an instance.

```swift
AppColors.blue
```

Instead of

```swift
let colors = AppColors()
colors.blue // ❌
```

---

## Android Equivalent

```kotlin
object AppColors {
    val blue = Color(0xFF374163)
}
```

or

```kotlin
class AppColors {
    companion object {
        val blue = Color.Blue
    }
}
```

---

## What is `Color("blue")`?

It loads a color from the **Assets Catalog**.

Android

```
res/
    values/
        colors.xml
```

iOS

```
Assets.xcassets
    blue
    orange
    gray
```

Then simply use

```swift
Color("blue")
```

---

# 2. Custom Font Helper

### SwiftUI

```swift
struct AppFont {

    static func thin(_ size: CGFloat) -> Font {
        .custom("Figtree-Thin", size: size)
    }

}
```

Now instead of writing

```swift
Font.custom("Figtree-Thin", size: 18)
```

everywhere,

you simply write

```swift
AppFont.thin(18)
```

---

## Android Equivalent

```kotlin
object AppFont {

    fun thin(size: TextUnit): TextStyle {
        return TextStyle(
            fontFamily = Figtree,
            fontWeight = FontWeight.Thin,
            fontSize = size
        )
    }

}
```

---

## What is CGFloat?

`CGFloat` is Apple's floating-point type used for UI measurements.

Examples:

- Width
- Height
- Corner Radius
- Padding
- Animation Values

Android equivalent:

```kotlin
Float
```

---

# 3. @State

### SwiftUI

```swift
@State
private var animate = false
```

`@State` stores local UI state.

Whenever its value changes,

SwiftUI automatically redraws the View.

---

## Android Equivalent

```kotlin
var animate by remember {
    mutableStateOf(false)
}
```

Without `@State`

```swift
var animate = false
```

changing

```swift
animate = true
```

does **not** update the UI.

With

```swift
@State
```

SwiftUI watches the variable and refreshes the View.

---

# 4. BallPulseIndicator Explained

### SwiftUI

```swift
struct BallPulseIndicator: View {

    @State private var animate = false

    var body: some View {

        HStack(spacing: 8) {

            ForEach(0..<3) { index in

                Circle()
                    .fill(AppColors.blue)
                    .frame(width: 10, height: 10)
                    .scaleEffect(animate ? 1 : 0.3)
                    .animation(
                        .easeInOut(duration: 0.6)
                        .repeatForever(autoreverses: true)
                        .delay(Double(index) * 0.15),
                        value: animate
                    )
            }
        }
        .onAppear {
            animate = true
        }
    }
}
```

---

## Breakdown

### `struct BallPulseIndicator: View`

Equivalent

```kotlin
@Composable
fun BallPulseIndicator()
```

---

### `body`

```swift
var body: some View
```

Equivalent

```kotlin
@Composable
fun BallPulseIndicator() {
}
```

Everything inside `body` represents the UI.

---

### HStack

SwiftUI

```swift
HStack(spacing: 8)
```

Compose

```kotlin
Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp)
)
```

---

### Circle

SwiftUI

```swift
Circle()
```

Compose

```kotlin
Box(
    modifier = Modifier
        .size(10.dp)
        .clip(CircleShape)
)
```

---

### fill

SwiftUI

```swift
.fill(AppColors.blue)
```

Compose

```kotlin
.background(AppColors.blue)
```

---

### frame

SwiftUI

```swift
.frame(width: 10, height: 10)
```

Compose

```kotlin
.size(10.dp)
```

---

### scaleEffect

SwiftUI

```swift
.scaleEffect(animate ? 1 : 0.3)
```

Compose

```kotlin
graphicsLayer {
    scaleX = if (animate) 1f else 0.3f
    scaleY = if (animate) 1f else 0.3f
}
```

---

### animation

SwiftUI

```swift
.animation(...)
```

Compose

```kotlin
animateFloatAsState()
```

or

```kotlin
rememberInfiniteTransition()
```

---

### repeatForever

SwiftUI

```swift
.repeatForever()
```

Compose

```kotlin
infiniteRepeatable()
```

---

### delay

SwiftUI

```swift
.delay(Double(index) * 0.15)
```

Compose

```kotlin
StartOffset(index * 150)
```

---

### onAppear

SwiftUI

```swift
.onAppear {
    animate = true
}
```

Compose

```kotlin
LaunchedEffect(Unit) {
    animate = true
}
```

Runs only when the View first appears.

---

# 5. @StateObject

### SwiftUI

```swift
@StateObject
var viewModel = SplashViewModel()
```

Creates a ViewModel owned by the View.

SwiftUI keeps it alive even when the View redraws.

---

## Android Equivalent

```kotlin
val viewModel: SplashViewModel = viewModel()
```

or

```kotlin
val viewModel: SplashViewModel = hiltViewModel()
```

---

Without `@StateObject`

A new ViewModel may be created every redraw.

With `@StateObject`

Only one instance is created.

---

# 6. Random Loading Text

### SwiftUI

```swift
@State
private var loadingText = loadingMessages.randomElement()!
```

Chooses one random message from an array.

Example

```swift
let loadingMessages = [
    "Loading...",
    "Please wait...",
    "Fetching Data..."
]
```

One value is selected.

Example

```
Fetching Data...
```

---

## Android Equivalent

```kotlin
var loadingText by remember {
    mutableStateOf(
        loadingMessages.random()
    )
}
```

---

## Why `!`?

`randomElement()` returns

```swift
String?
```

because the array might be empty.

Using

```swift
!
```

means

> "I am sure this value is not nil."

If it is nil,

the app crashes.

Safer version

```swift
loadingMessages.randomElement() ?? "Loading..."
```

---

# 7. @Published

### SwiftUI

```swift
@Published
var isReady = false
```

Used inside a ViewModel.

Whenever the value changes,

SwiftUI updates every observing View.

---

## Android Equivalent

```kotlin
var isReady by mutableStateOf(false)
```

or

```kotlin
MutableStateFlow(false)
```

or

```kotlin
MutableLiveData(false)
```

Changing

```swift
isReady = true
```

automatically refreshes the UI.

---

# 8. DispatchQueue.main.asyncAfter

### SwiftUI

```swift
DispatchQueue.main.asyncAfter(deadline: .now() + 3) {

    self.isReady = true

}
```

Waits for 3 seconds,

then executes the code on the Main Thread.

---

## Android Equivalent

Old Way

```kotlin
Handler(Looper.getMainLooper())
    .postDelayed({
        isReady = true
    }, 3000)
```

Modern Way

```kotlin
viewModelScope.launch {

    delay(3000)

    isReady = true

}
```

---

# SwiftUI vs Jetpack Compose Cheat Sheet

| SwiftUI | Jetpack Compose |
|----------|-----------------|
| View | @Composable |
| body | Composable Function |
| HStack | Row |
| VStack | Column |
| ZStack | Box |
| Text | Text |
| Image | Image |
| Circle | Box + CircleShape |
| Color("blue") | colorResource() |
| Font.custom() | FontFamily |
| @State | remember { mutableStateOf() } |
| @Binding | Mutable State Parameter |
| @StateObject | viewModel() |
| @ObservedObject | Collect State from ViewModel |
| @Published | mutableStateOf / StateFlow |
| .frame() | .size() |
| .padding() | .padding() |
| .background() | .background() |
| .clipShape() | .clip() |
| .onAppear | LaunchedEffect(Unit) |
| .animation() | animate*AsState |
| .repeatForever() | infiniteRepeatable() |
| DispatchQueue.main.asyncAfter | delay() |
| NavigationStack | NavHost |
| NavigationLink | navController.navigate() |

---

# Quick Memory Tips

- **@State** → Local UI state.
- **@Binding** → Pass state to child Views.
- **@StateObject** → Create and own a ViewModel.
- **@ObservedObject** → Observe an existing ViewModel.
- **@Published** → Notify UI when data changes.
- **Color("blue")** → Load a color from Assets.
- **Font.custom()** → Load a custom font.
- **body** → The UI definition of a View.
- **onAppear** → Runs when a View appears.
- **DispatchQueue.main.asyncAfter** → Execute code after a delay on the main thread.

---

# Final Takeaway

If you already know Jetpack Compose, learning SwiftUI is much easier because both frameworks are declarative and state-driven.

The biggest differences are:

- Property wrappers (`@State`, `@Binding`, `@StateObject`, `@ObservedObject`, `@Published`)
- Apple naming conventions
- iOS framework APIs (`Color`, `Font`, `NavigationStack`, `DispatchQueue`, etc.)

Once you're comfortable with these concepts, you'll be able to read and write most SwiftUI code confidently.