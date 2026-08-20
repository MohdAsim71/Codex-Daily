# SOLID Principles — Explained Simply (For Android/Kotlin Developers)

SOLID is a set of **5 design principles** that help you write code that is easy to understand, easy to change, and easy to test. These principles were introduced by Robert C. Martin (Uncle Bob) and are one of the most commonly asked topics in **Low Level Design (LLD)** interviews.

If your code follows SOLID, it usually means:
- Adding new features doesn't break old features
- Classes are small and focused
- Code is easier to test with mocks/fakes
- Bugs are easier to find and fix

Let's go through each one, one at a time.

---

## SOLID Overview

| Principle | Full Form | Simple Meaning |
|---|---|---|
| **S** | Single Responsibility Principle | One class should have one responsibility |
| **O** | Open/Closed Principle | Open for extension, closed for modification |
| **L** | Liskov Substitution Principle | Child classes should be usable wherever the parent is expected |
| **I** | Interface Segregation Principle | Don't force classes to implement methods they don't need |
| **D** | Dependency Inversion Principle | Depend on abstractions, not concrete implementations |

---

# 1. S — Single Responsibility Principle (SRP)

### What it means (in very simple language)
**A class should have only one reason to change.**

In simpler words: one class should do **one job**, not many jobs mixed together. If a class handles UI, saves data to a database, AND calls a network API — that's too much responsibility for one class.

### The problem it solves
When a class does many things:
- A change in one feature can accidentally break another feature.
- The class becomes big, messy, and hard to read.
- Testing becomes hard because you can't test one thing without dragging in everything else.

### Real-world example
Think of a restaurant.
- The **chef** cooks food.
- The **waiter** serves food.
- The **cashier** handles billing.

Imagine if one person had to cook, serve, AND handle billing at the same time — mistakes would happen constantly, and training a new person would be very hard. Each person has **one responsibility**.

### Bad Kotlin Example

```kotlin
class UserManager {

    fun registerUser(name: String, email: String) {
        // 1. Validation logic
        if (email.isEmpty()) {
            throw IllegalArgumentException("Email cannot be empty")
        }

        // 2. Save to database
        val db = openDatabaseConnection()
        db.insertUser(name, email)

        // 3. Send welcome email
        val emailSender = EmailSender()
        emailSender.send(email, "Welcome, $name!")

        // 4. Logging
        println("User $name registered successfully")
    }
}
```

### Why the bad code violates SRP
This one class is doing **4 different jobs**:
1. Validating input
2. Talking to the database
3. Sending emails
4. Logging

If the email logic changes, you touch this class. If the database logic changes, you touch this class again. If validation rules change, again the same class. This class has **many reasons to change**, which is exactly what SRP says to avoid.

### Good Kotlin Example

```kotlin
class UserValidator {
    fun validate(email: String) {
        if (email.isEmpty()) throw IllegalArgumentException("Email cannot be empty")
    }
}

class UserRepository {
    fun saveUser(name: String, email: String) {
        // handles only database logic
        val db = openDatabaseConnection()
        db.insertUser(name, email)
    }
}

class WelcomeEmailSender {
    fun sendWelcomeEmail(email: String, name: String) {
        // handles only email logic
        EmailSender().send(email, "Welcome, $name!")
    }
}

class UserRegistrationService(
    private val validator: UserValidator,
    private val repository: UserRepository,
    private val emailSender: WelcomeEmailSender
) {
    fun registerUser(name: String, email: String) {
        validator.validate(email)
        repository.saveUser(name, email)
        emailSender.sendWelcomeEmail(email, name)
    }
}
```

### Why the good code follows SRP
Each class now has **exactly one job**:
- `UserValidator` → only validates
- `UserRepository` → only saves data
- `WelcomeEmailSender` → only sends emails
- `UserRegistrationService` → only coordinates the steps

If email logic changes, only `WelcomeEmailSender` changes. Nothing else is affected.

### Advantages
- Easier to read and understand
- Easier to test each part in isolation
- Changes are isolated and safe
- Classes are reusable in other places

### Common mistakes developers make
- Putting business logic, networking, and UI code all in one `Activity`/`Fragment`/`ViewModel`
- Naming a class `Manager`, `Helper`, or `Util` and dumping unrelated logic into it
- Thinking "it's just one extra function" and slowly turning a class into a God Object

### When it's useful in real Android projects
- Splitting `ViewModel` logic (state handling) from `Repository` logic (data fetching)
- Separating API response mapping (`Mapper` class) from the `ApiService`
- Keeping `Activity`/`Fragment` only responsible for UI, not business logic

### Interview-friendly definition
> "A class should have only one reason to change — meaning it should do only one job."

---

# 2. O — Open/Closed Principle (OCP)

### What it means (in very simple language)
**Classes should be open for extension but closed for modification.**

This means: when you want to add new behavior, you should be able to **add new code**, not **change existing, already-tested code**.

### The problem it solves
If you keep modifying an existing class every time a new requirement comes in:
- You risk breaking features that were already working and tested.
- The class keeps growing with `if-else` or `when` blocks for every new case.
- Old, stable code becomes fragile.

### Real-world example
Think of a **power strip (extension board)**. If you want to plug in a new device, you don't rewire the wall socket — you just plug the new device into the extension board. The wall socket (existing system) doesn't change; you just **extend** it with a new plug.

### Bad Kotlin Example

```kotlin
class PaymentProcessor {

    fun processPayment(type: String, amount: Double) {
        if (type == "CREDIT_CARD") {
            println("Processing credit card payment of $amount")
        } else if (type == "UPI") {
            println("Processing UPI payment of $amount")
        } else if (type == "PAYPAL") {
            println("Processing PayPal payment of $amount")
        }
        // Every time a new payment method is added,
        // this function must be MODIFIED
    }
}
```

### Why the bad code violates OCP
Every time a new payment method (like "Wallet" or "Crypto") is added, you must **open and edit** this existing class. This is risky because:
- You might accidentally break credit card or UPI logic while adding wallet logic.
- The function keeps growing bigger and bigger with more `if-else` branches.

### Good Kotlin Example

```kotlin
interface PaymentMethod {
    fun pay(amount: Double)
}

class CreditCardPayment : PaymentMethod {
    override fun pay(amount: Double) {
        println("Processing credit card payment of $amount")
    }
}

class UpiPayment : PaymentMethod {
    override fun pay(amount: Double) {
        println("Processing UPI payment of $amount")
    }
}

class PayPalPayment : PaymentMethod {
    override fun pay(amount: Double) {
        println("Processing PayPal payment of $amount")
    }
}

class PaymentProcessor {
    fun processPayment(paymentMethod: PaymentMethod, amount: Double) {
        paymentMethod.pay(amount)
    }
}

// Adding a NEW payment method means creating a NEW class,
// not touching PaymentProcessor at all.
class WalletPayment : PaymentMethod {
    override fun pay(amount: Double) {
        println("Processing wallet payment of $amount")
    }
}
```

### Why the good code follows OCP
`PaymentProcessor` never needs to change again. To add a new payment type, we just create a **new class** implementing `PaymentMethod`. The existing, tested code stays untouched — we only **extend** the system by adding new code.

### Advantages
- Existing, tested code stays safe from new bugs
- New features can be added quickly
- Encourages use of interfaces/abstract classes
- Reduces regression testing effort

### Common mistakes developers make
- Using long `if-else` or `when` chains based on type strings/enums for behavior
- Modifying a shared class every sprint instead of designing for extension
- Over-engineering simple logic with too many interfaces where it's not needed (this principle should be applied where change is *expected*, not everywhere)

### When it's useful in real Android projects
- Adding new payment gateways, login providers (Google, Facebook, Email) without touching existing auth code
- Supporting new analytics providers (Firebase, Mixpanel, custom) via a common interface
- Adding new item types in a `RecyclerView` adapter using sealed classes/interfaces instead of giant `when` blocks

### Interview-friendly definition
> "Software entities should be open for extension but closed for modification — add new behavior via new code, not by editing existing code."

---

# 3. L — Liskov Substitution Principle (LSP)

### What it means (in very simple language)
**A child class should be usable anywhere the parent class is expected, without breaking the program.**

If `Child` extends `Parent`, then anywhere in code that uses `Parent`, you should be able to plug in `Child` and everything should still work correctly.

### The problem it solves
If a subclass doesn't behave like its parent (e.g., it throws an unexpected error or changes expected behavior), then code that relies on the parent's contract will **break** when a subclass is passed in. This makes inheritance unsafe and unpredictable.

### Real-world example
Think of a **Bird** class with a `fly()` method. Now if you create a subclass `Penguin : Bird`, but penguins can't fly — calling `fly()` on a `Penguin` would break the promise made by `Bird`. Any code written to work with "any Bird" would now crash or misbehave for penguins.

### Bad Kotlin Example

```kotlin
open class Bird {
    open fun fly() {
        println("Flying in the sky")
    }
}

class Sparrow : Bird()

class Penguin : Bird() {
    override fun fly() {
        // Penguins can't fly!
        throw UnsupportedOperationException("Penguins can't fly")
    }
}

fun makeBirdFly(bird: Bird) {
    bird.fly()
}

fun main() {
    val birds = listOf(Sparrow(), Penguin())
    birds.forEach { makeBirdFly(it) } // Crashes on Penguin!
}
```

### Why the bad code violates LSP
`Penguin` is a subtype of `Bird`, but it cannot be safely substituted wherever a `Bird` is expected — calling `fly()` breaks the program. Code that trusted "every `Bird` can fly" is now unreliable.

### Good Kotlin Example

```kotlin
interface Bird

interface FlyingBird : Bird {
    fun fly()
}

interface SwimmingBird : Bird {
    fun swim()
}

class Sparrow : FlyingBird {
    override fun fly() {
        println("Flying in the sky")
    }
}

class Penguin : SwimmingBird {
    override fun swim() {
        println("Swimming in water")
    }
}

fun makeBirdFly(bird: FlyingBird) {
    bird.fly()
}

fun main() {
    makeBirdFly(Sparrow()) // Works correctly
    // makeBirdFly(Penguin()) -> Won't even compile, which is GOOD
}
```

### Why the good code follows LSP
We split `Bird` into more specific types: `FlyingBird` and `SwimmingBird`. Now `Penguin` doesn't pretend to be something it's not. Any `FlyingBird` passed into `makeBirdFly()` is guaranteed to actually be able to fly — no surprises, no crashes.

### Advantages
- Prevents unexpected runtime crashes
- Makes inheritance hierarchies trustworthy
- Code relying on a parent type behaves predictably for all children
- Encourages better class hierarchy design

### Common mistakes developers make
- Overriding a method to throw `UnsupportedOperationException` or do nothing
- Making a subclass that weakens the parent's guarantees (e.g., parent returns a non-null list, child sometimes returns null)
- Using inheritance just for code reuse, even when the "is-a" relationship doesn't truly hold

### When it's useful in real Android projects
- Designing base `Fragment`/`Activity` classes — a subclass shouldn't break behavior the base class guarantees (like lifecycle callbacks)
- Custom `View` subclasses — a subclass shouldn't crash or behave unexpectedly when used where the base `View` is expected
- Repository interfaces — every implementation (e.g., `LocalUserRepository`, `RemoteUserRepository`) must honor the same contract, like never returning `null` when the interface says it won't

### Interview-friendly definition
> "Subtypes must be substitutable for their base types without breaking the correctness of the program."

---

# 4. I — Interface Segregation Principle (ISP)

### What it means (in very simple language)
**Don't force a class to implement methods it doesn't need.**

Instead of one big "fat" interface with many methods, split it into smaller, focused interfaces. A class should only implement what it actually uses.

### The problem it solves
Large interfaces force implementing classes to write empty or fake implementations for methods they don't need. This creates confusing, unused, and error-prone code.

### Real-world example
Think of a **job application form** that asks about "Driving License Number" even for a desk job that doesn't require driving. Most applicants don't have this info and are forced to write "N/A." A better form would only ask relevant questions based on the job type — smaller, focused forms instead of one giant form for everyone.

### Bad Kotlin Example

```kotlin
interface Worker {
    fun work()
    fun eat()
    fun sleep()
}

class HumanWorker : Worker {
    override fun work() = println("Working")
    override fun eat() = println("Eating lunch")
    override fun sleep() = println("Sleeping at night")
}

class RobotWorker : Worker {
    override fun work() = println("Working nonstop")
    override fun eat() {
        // Robots don't eat!
        throw UnsupportedOperationException("Robots don't eat")
    }
    override fun sleep() {
        // Robots don't sleep!
        throw UnsupportedOperationException("Robots don't sleep")
    }
}
```

### Why the bad code violates ISP
`RobotWorker` is forced to implement `eat()` and `sleep()` even though it doesn't need them. This leads to fake/empty implementations or thrown exceptions — a clear sign the interface is too "fat" and doing too much.

### Good Kotlin Example

```kotlin
interface Workable {
    fun work()
}

interface Eatable {
    fun eat()
}

interface Sleepable {
    fun sleep()
}

class HumanWorker : Workable, Eatable, Sleepable {
    override fun work() = println("Working")
    override fun eat() = println("Eating lunch")
    override fun sleep() = println("Sleeping at night")
}

class RobotWorker : Workable {
    override fun work() = println("Working nonstop")
    // No need to implement eat() or sleep() at all
}
```

### Why the good code follows ISP
Each interface is small and focused on one capability. `RobotWorker` only implements `Workable`, which is exactly what it needs — nothing more, nothing forced.

### Advantages
- No fake/empty method implementations
- Classes only depend on what they actually use
- Easier to understand what a class can actually do
- Reduces the "ripple effect" when interfaces change

### Common mistakes developers make
- Creating one huge `Repository` or `Manager` interface with 15+ methods for everything
- Adding a method to a shared interface "just in case," forcing all implementers to handle it
- Not splitting interfaces by client needs (different callers need different subsets of behavior)

### When it's useful in real Android projects
- Splitting a huge `UserRepository` interface into `UserReadRepository` and `UserWriteRepository` when some classes only need to read data
- Defining separate interfaces for `ClickListener`, `LongClickListener`, `SwipeListener` in a `RecyclerView` adapter instead of one giant listener interface
- Creating focused ViewModel-facing interfaces so the UI layer only sees what it needs (e.g., `LoginActions` vs `LoginState`)

### Interview-friendly definition
> "Clients should not be forced to depend on interfaces/methods they do not use — prefer many small, specific interfaces over one large, general one."

---

# 5. D — Dependency Inversion Principle (DIP)

### What it means (in very simple language)
**Depend on abstractions (interfaces), not on concrete (specific) implementations.**

High-level code (business logic) should not directly depend on low-level code (like a specific database or network library). Both should depend on an interface (abstraction) in between.

### The problem it solves
If your business logic directly creates and uses a specific class (like `RetrofitApiService` or `SqliteDatabase`), then:
- You can't easily swap it out for testing (e.g., replace with a fake/mock)
- Changing the low-level implementation forces changes in high-level business logic too
- Code becomes tightly coupled and hard to maintain

### Real-world example
Think of a **wall socket and an electric appliance** (like a phone charger). The appliance doesn't care whether electricity comes from solar power, a generator, or the city power grid — it just plugs into a **standard socket (abstraction)**. The power source can change without the appliance needing to change at all.

### Bad Kotlin Example

```kotlin
class RetrofitApiService {
    fun fetchUser(id: String): String {
        return "User data from network for $id"
    }
}

class UserRepository {
    // Directly depends on a CONCRETE class
    private val apiService = RetrofitApiService()

    fun getUser(id: String): String {
        return apiService.fetchUser(id)
    }
}
```

### Why the bad code violates DIP
`UserRepository` (high-level logic) is directly tied to `RetrofitApiService` (a low-level, specific implementation). If you want to:
- Switch to a different networking library, or
- Write a unit test using a fake API service

...you cannot do it without modifying `UserRepository` itself. They are **tightly coupled**.

### Good Kotlin Example

```kotlin
// Abstraction (interface)
interface UserApiService {
    fun fetchUser(id: String): String
}

// Low-level implementation depends on the abstraction
class RetrofitApiService : UserApiService {
    override fun fetchUser(id: String): String {
        return "User data from network for $id"
    }
}

// A fake implementation, useful for testing
class FakeApiService : UserApiService {
    override fun fetchUser(id: String): String {
        return "Fake user data for $id"
    }
}

// High-level class depends on the ABSTRACTION, not the concrete class
class UserRepository(private val apiService: UserApiService) {
    fun getUser(id: String): String {
        return apiService.fetchUser(id)
    }
}

fun main() {
    val realRepo = UserRepository(RetrofitApiService())
    val testRepo = UserRepository(FakeApiService()) // Easy to swap for testing
}
```

### Why the good code follows DIP
`UserRepository` now depends only on the `UserApiService` **interface**, not on any specific implementation. We can pass in `RetrofitApiService` for production or `FakeApiService` for testing — without changing `UserRepository` at all. This is also what makes **Dependency Injection** (like Hilt/Dagger) possible.

### Advantages
- Easy to swap implementations (network library, database, etc.)
- Makes unit testing much easier (inject fakes/mocks)
- Reduces tight coupling between layers
- Forms the foundation for Dependency Injection frameworks

### Common mistakes developers make
- Creating objects directly with `= SomeClass()` inside a business logic class instead of injecting them
- Confusing "Dependency Inversion" with "Dependency Injection" — DI (injection) is a *technique*, DIP is the *principle* that makes DI valuable
- Depending on a concrete class even though an interface exists, defeating the purpose

### When it's useful in real Android projects
- `ViewModel` depending on a `Repository` **interface**, not a concrete `RepositoryImpl`, so it can be tested with fake repositories
- Using Hilt/Dagger to inject `interface` types (like `AuthRepository`) and binding them to real implementations (`AuthRepositoryImpl`) using `@Binds`
- Swapping a real payment gateway with a sandbox/mock gateway during testing, without touching business logic

### Interview-friendly definition
> "High-level modules should not depend on low-level modules; both should depend on abstractions. Abstractions should not depend on details; details should depend on abstractions."

---

# Android Examples — SOLID in Real Projects

This section shows how all 5 principles show up together in a typical Android app architecture.

## 1. Repository Pattern

```kotlin
// DIP + ISP: small, abstract interface
interface UserRepository {
    suspend fun getUser(id: String): User
}

// SRP: this class only handles fetching user data from network
class UserRepositoryImpl(
    private val apiService: UserApiService,
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getUser(id: String): User {
        return try {
            val user = apiService.fetchUser(id)
            userDao.cacheUser(user) // caching logic stays simple here
            user
        } catch (e: Exception) {
            userDao.getCachedUser(id) // fallback to local cache
        }
    }
}
```
The `ViewModel` only knows about the `UserRepository` interface (DIP), so it never needs to know if data comes from network or a local database.

## 2. ViewModel

```kotlin
// SRP: ViewModel only manages UI state, not business/network logic
class UserProfileViewModel(
    private val userRepository: UserRepository // DIP: depends on abstraction
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun loadUser(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(userRepository.getUser(id))
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

## 3. API / Retrofit Service

```kotlin
// ISP: focused interface, only user-related endpoints
interface UserApiService {
    @GET("users/{id}")
    suspend fun fetchUser(@Path("id") id: String): User
}

// OCP: adding a new endpoint group means adding a new interface,
// not modifying this one
interface OrderApiService {
    @GET("orders/{id}")
    suspend fun fetchOrder(@Path("id") id: String): Order
}
```

## 4. Use Cases (Clean Architecture)

```kotlin
// SRP: one use case = one specific business action
class GetUserProfileUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: String): User {
        return userRepository.getUser(id)
    }
}

class UpdateUserProfileUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User) {
        // validation logic specific to updating a profile
        require(user.name.isNotBlank()) { "Name cannot be blank" }
        userRepository.updateUser(user)
    }
}
```
Use cases keep the `ViewModel` thin and each business rule isolated — a direct application of SRP and OCP (new use cases can be added without touching old ones).

## 5. Dependency Injection (Hilt Example)

```kotlin
// DIP: bind interface to implementation, so classes only ever
// see the abstraction (UserRepository), never the concrete class
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
```
This is DIP in action — Hilt hands out `UserRepositoryImpl` wherever `UserRepository` is requested, but every consuming class (like `ViewModel`) never needs to know the concrete type.

## 6. UI / Business Logic Separation

```kotlin
// BAD: business logic living inside a Composable/Activity (violates SRP)
@Composable
fun BadLoginScreen() {
    Button(onClick = {
        if (email.contains("@") && password.length >= 8) {
            // direct API call logic here... not testable, not reusable
        }
    }) { Text("Login") }
}

// GOOD: UI only triggers an action, logic lives in ViewModel/UseCase
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    Button(onClick = { viewModel.login(email, password) }) {
        Text("Login")
    }
}
```

## 7. Payment / Authentication Flow

```kotlin
// OCP + DIP: new payment or login methods can be added
// without touching existing, tested code
interface AuthProvider {
    suspend fun login(credentials: Credentials): AuthResult
}

class GoogleAuthProvider : AuthProvider {
    override suspend fun login(credentials: Credentials): AuthResult {
        // Google Sign-In logic
        return AuthResult.Success
    }
}

class EmailAuthProvider : AuthProvider {
    override suspend fun login(credentials: Credentials): AuthResult {
        // Email/password logic
        return AuthResult.Success
    }
}

// LSP: any AuthProvider can be substituted here safely
class AuthManager(private val provider: AuthProvider) {
    suspend fun authenticate(credentials: Credentials): AuthResult {
        return provider.login(credentials)
    }
}
```
Adding Facebook or OTP login later = just a new class implementing `AuthProvider`. `AuthManager` never changes.

---

# Relationships Between Principles

The 5 principles are not isolated rules — they support and build on each other:

- **SRP → Separate responsibilities**
  Once each class has one clear job, it becomes much easier to apply the other principles to it.

- **OCP → Make behavior extendable**
  Once responsibilities are separated (SRP), you can extend each piece independently by adding new classes instead of editing old ones.

- **LSP → Keep inheritance safe**
  When you extend behavior (OCP) using subclasses, LSP makes sure those subclasses can be trusted and used safely wherever the parent type is expected.

- **ISP → Keep interfaces focused**
  Small, focused interfaces make it easier to create new implementations (OCP) and safer substitutions (LSP), since there's less to implement and less to go wrong.

- **DIP → Reduce tight coupling**
  Depending on abstractions (interfaces) ties everything together — it's what actually makes OCP, LSP, and ISP possible in practice, since you can plug in new implementations without breaking high-level code.

**Simple way to remember it:** SRP breaks your code into small focused pieces → OCP lets you add new pieces safely → LSP makes sure those pieces behave correctly when swapped → ISP keeps the "contracts" between pieces small and clean → DIP ties everything together loosely, through abstractions, instead of hardcoded connections.

---

# Common Interview Questions

**1. What is SOLID?**
SOLID is a set of 5 object-oriented design principles (SRP, OCP, LSP, ISP, DIP) that help write maintainable, flexible, and testable code.

**2. Why is SOLID important?**
It reduces tight coupling, makes code easier to extend without breaking existing features, and makes unit testing much easier — all of which matter a lot in real-world, long-lived projects.

**3. Explain SRP with an example.**
A class should have only one reason to change. Example: a `UserRepository` should only fetch/save user data — it shouldn't also handle UI or send emails. Those should be separate classes.

**4. What is the difference between SRP and ISP?**
SRP is about **classes** — a class should do one job. ISP is about **interfaces** — an interface should expose only the methods a client actually needs. SRP focuses on responsibility; ISP focuses on the shape of the contract (interface).

**5. What does Open/Closed mean?**
Code should be **open for extension** (you can add new behavior) but **closed for modification** (you shouldn't need to edit existing, tested code to do it). Usually achieved using interfaces/abstract classes + polymorphism.

**6. What is LSP?**
Liskov Substitution Principle says a subclass must be usable anywhere its parent class is expected, without breaking correctness. If substituting a child class causes unexpected behavior or crashes, LSP is violated.

**7. How does DIP help Dependency Injection?**
DIP says depend on abstractions, not concrete classes. This is exactly what makes DI frameworks like Hilt/Dagger useful — they inject a concrete implementation wherever an abstraction (interface) is expected, so your business logic never needs to know or care about the concrete class.

**8. Is SOLID mandatory in every project?**
No. For small scripts, prototypes, or throwaway code, applying full SOLID can be overkill. It's most valuable in medium-to-large codebases that will be maintained and extended over time.

**9. Can SOLID be overused?**
Yes. Over-applying SOLID (e.g., creating an interface for every single class "just in case") can lead to unnecessary complexity — too many small files and layers of abstraction for problems that don't need it. This is sometimes called over-engineering.

**10. How would you apply SOLID in an Android application?**
- SRP: Separate `ViewModel`, `Repository`, `UseCase`, and `ApiService` layers.
- OCP: Use interfaces for payment methods, auth providers, or analytics so new options don't require editing old code.
- LSP: Make sure all `Repository` implementations honor the same contract/behavior.
- ISP: Split large repository/listener interfaces into smaller, focused ones.
- DIP: Depend on repository/use-case interfaces in ViewModels, and use Hilt/Dagger to inject concrete implementations.

**11. What is a "God Object" and how does SOLID prevent it?**
A God Object is a class that knows/does too much (violates SRP badly). SOLID, especially SRP, encourages breaking such classes into smaller, focused pieces.

**12. Give an example of violating OCP in Android.**
A `RecyclerView` adapter with a huge `when (viewType)` block that must be edited every time a new item type is added — instead of using a polymorphic approach with sealed classes/interfaces.

**13. How does ISP apply to Kotlin interfaces specifically?**
Kotlin interfaces can have default method implementations, which can tempt developers to create large "fat" interfaces. ISP reminds us to still split responsibilities even though Kotlin makes it easy not to.

---

# Quick Revision (One-Page Summary)

| Principle | One-Line Definition | Simple Example | Common Mistake |
|---|---|---|---|
| **SRP** | A class should have only one reason to change. | Separate `UserValidator`, `UserRepository`, `EmailSender` instead of one giant `UserManager`. | Dumping unrelated logic into one "Manager"/"Helper" class. |
| **OCP** | Open for extension, closed for modification. | Use a `PaymentMethod` interface instead of `if-else` on payment type strings. | Long `if-else`/`when` chains that must be edited for every new case. |
| **LSP** | Subclasses must be safely substitutable for their parent. | Don't make `Penguin` inherit `Bird.fly()` if it can't fly — split into `FlyingBird`/`SwimmingBird`. | Overriding a method to throw an exception or do nothing. |
| **ISP** | Don't force classes to implement methods they don't need. | Split `Workable`, `Eatable`, `Sleepable` instead of one fat `Worker` interface. | One huge interface with 15+ unrelated methods. |
| **DIP** | Depend on abstractions, not concrete classes. | `ViewModel` depends on `UserRepository` interface, not `UserRepositoryImpl`. | Creating concrete objects directly (`= RetrofitApiService()`) instead of injecting an interface. |

**Golden Rule to Remember:**
> Small, focused classes (SRP) → extendable via new code, not edits (OCP) → subclasses behave safely (LSP) → interfaces stay lean and specific (ISP) → everything connects loosely through abstractions (DIP).

---

*Use this document as a quick refresher before LLD/system design interviews. Focus especially on being able to explain the "bad vs good" code differences out loud — that's usually what interviewers probe on.*