# LLD & Design Patterns — Easy Explanation

## 1. What is LLD?

**LLD = Low-Level Design**

LLD is the detailed design of a software system.

Think of LLD as:

> **"Before writing code, how will I structure my classes, objects, interfaces, and their relationships?"**

### Example

Suppose you are designing a Food Delivery App.

### HLD (High-Level Design)

At a high level, you may have:

```
User
Restaurant
Order
Payment
Delivery
```

### LLD (Low-Level Design)

In LLD, we go deeper:

```
User
 ├── name
 ├── email
 └── placeOrder()

Order
 ├── orderId
 ├── items
 ├── totalAmount
 └── calculateTotal()

Payment
 ├── paymentId
 ├── amount
 └── pay()
```

LLD helps us decide:

- Which classes are required?
- Which interfaces are required?
- What properties should each class have?
- What methods should each class have?
- Which class should communicate with which?
- What relationship exists between classes?
- Which design pattern should be used?

### Simple Definition

LLD is the blueprint of your code/classes before you actually write the code.

---

## 2. Why do we need LLD?

Imagine putting everything inside one class:

```kotlin
class FoodApp {
    fun login() {}
    fun makePayment() {}
    fun placeOrder() {}
    fun cancelOrder() {}
    fun sendNotification() {}
    fun calculateDelivery() {}
}
```

Initially, this may work.

But as the application grows, this class becomes:

- Very large
- Difficult to understand
- Difficult to test
- Difficult to maintain
- Difficult to modify

Instead, we can separate responsibilities:

```
AuthService
PaymentService
OrderService
NotificationService
DeliveryService
```

Each class has a clear responsibility.

This is one of the main purposes of LLD.

---

## 3. What are Design Patterns?

A Design Pattern is a commonly used solution to a common software design problem.

Think of Design Patterns as:

> Proven ways of structuring your code to solve common problems.

For example, suppose your application supports multiple payment methods:

```
UPI
Credit Card
PayPal
Cash
```

A simple implementation might be:

```kotlin
if (paymentType == "UPI") {
    // UPI payment
} else if (paymentType == "CARD") {
    // Card payment
} else if (paymentType == "PAYPAL") {
    // PayPal payment
}
```

As the number of payment methods increases, this can become difficult to maintain.

A design pattern such as **Strategy Pattern** can solve this problem:

```
PaymentStrategy
       |
       ├── UpiPayment
       ├── CardPayment
       └── PayPalPayment
```

Each payment method has its own implementation.

---

## 4. Design Pattern Categories

There are 3 major categories of Design Patterns:

```
Design Patterns
      |
      ├── Creational
      |
      ├── Structural
      |
      └── Behavioral
```

Easy way to remember:

**Create → Connect → Communicate**

---

## 5. Creational Design Patterns

Creational patterns deal with:

> How objects are created.

Common examples:

- Singleton
- Factory
- Abstract Factory
- Builder
- Prototype

### Example: Builder Pattern

Suppose we have a User:

```kotlin
User(
    name = "Aasim",
    age = 25,
    email = "abc@gmail.com",
    phone = "1234567890",
    address = "Delhi"
)
```

If there are many optional parameters, creating the object can become difficult.

A Builder can make it cleaner:

```kotlin
UserBuilder()
    .setName("Aasim")
    .setAge(25)
    .setEmail("abc@gmail.com")
    .build()
```

So: **Creational = Object creation**

---

## 6. Structural Design Patterns

Structural patterns deal with:

> How classes and objects are connected or combined.

Common examples:

- Adapter
- Decorator
- Facade
- Proxy
- Composite
- Bridge
- Flyweight

### Example: Adapter Pattern

Suppose your application expects:

```kotlin
interface Payment {
    fun pay()
}
```

But an external SDK provides:

```kotlin
class Razorpay {
    fun makePayment()
}
```

Your application expects `Payment`, but Razorpay provides `Razorpay`.

An Adapter can connect them:

```
Your App
   |
   ↓
Payment Adapter
   |
   ↓
Razorpay
```

So your application doesn't need to know the internal details of Razorpay.

Therefore: **Structural = How objects/classes are connected**

---

## 7. Behavioral Design Patterns

Behavioral patterns deal with:

> How objects communicate and behave.

Common examples:

- Observer
- Strategy
- Command
- State
- Template Method
- Chain of Responsibility
- Mediator
- Iterator

### Example: Observer Pattern

Suppose a user logs in.

Multiple things may need to happen:

```
User Login
    |
    ├── Analytics
    |
    ├── Notification
    |
    └── Logging
```

The login event can notify multiple objects.

So: **Behavioral = How objects communicate**

---

## 8. Easy Way to Remember Pattern Categories

**Creational** — Ask: *How do I create objects?*
Examples: Factory, Builder, Singleton

**Structural** — Ask: *How do I connect objects?*
Examples: Adapter, Facade, Decorator

**Behavioral** — Ask: *How do objects communicate?*
Examples: Observer, Strategy, Command, State

### Easy Mnemonic

**Create → Connect → Communicate**

---

## 9. What is an Is-a Relationship?

**Is-a relationship = Inheritance / Implementation**

It means: one class is a type of another class.

For example: `Dog IS-A Animal`

In Kotlin:

```kotlin
open class Animal
class Dog : Animal()
```

Here:

```
Dog
 ↓
Animal
```

A Dog is an Animal.

### More Examples

- Car IS-A Vehicle
- Bike IS-A Vehicle
- Developer IS-A Employee
- UPIPayment IS-A Payment

```kotlin
open class Vehicle
class Car : Vehicle()
class Bike : Vehicle()
```

Therefore: `Car IS-A Vehicle`, `Bike IS-A Vehicle`

---

## 10. What is a Has-a Relationship?

**Has-a relationship = Composition / Aggregation**

It means: one object contains or uses another object.

For example: `Car HAS-A Engine`

A Car is not an Engine. A Car has an Engine.

```kotlin
class Engine {
    fun start() {
        // Start engine
    }
}

class Car(
    private val engine: Engine
)
```

Relationship:

```
Car
 |
 └── Engine
```

Therefore: Car has an Engine.

---

## 11. Is-a vs Has-a

| Is-a | Has-a |
|---|---|
| Inheritance | Composition |
| "is a type of" | "contains/uses" |
| Parent-child relationship | Object relationship |
| `Dog : Animal` | `Car` contains `Engine` |
| Car IS-A Vehicle | Car HAS-A Engine |

---

## 12. Easy Way to Identify Is-a vs Has-a

Whenever you are confused, ask:

**Question 1: Can I say "X is a Y"?**

If yes → Is-a

- Dog is an Animal ✅
- Car is a Vehicle ✅
- Developer is an Employee ✅

**Question 2: Can I say "X has a Y"?**

If yes → Has-a

- Car has an Engine ✅
- House has a Room ✅
- Order has Payment ✅
- User has an Address ✅

---

## 13. Is-a Example

```kotlin
open class Animal {
    fun eat() {
        println("Eating")
    }
}

class Dog : Animal() {
    fun bark() {
        println("Barking")
    }
}
```

Here: `Dog IS-A Animal`, because Dog inherits from Animal.

---

## 14. Has-a Example

```kotlin
class Engine {
    fun start() {
        println("Engine started")
    }
}

class Car(
    private val engine: Engine
) {
    fun startCar() {
        engine.start()
    }
}
```

Here: `Car HAS-A Engine`, because Car contains an Engine object.

---

## 15. Prefer Composition Over Inheritance

A very important LLD principle is:

> Prefer composition over inheritance when inheritance doesn't represent a genuine "is-a" relationship.

For example, don't do this:

```kotlin
class Car : Engine()
```

Because `Car IS-A Engine` ❌ — that doesn't make logical sense.

Instead:

```kotlin
class Car(
    private val engine: Engine
)
```

Because `Car HAS-A Engine` ✅

This usually makes the design more flexible.

---

## 16. Android Example

Suppose you have:

```kotlin
interface ApiService {
    suspend fun getUser()
}

class UserRepository(
    private val apiService: ApiService
)
```

Here: `UserRepository HAS-A ApiService`, because the repository uses/contains an ApiService reference.

Now suppose:

```kotlin
interface PaymentProcessor {
    fun pay()
}

class UpiPaymentProcessor : PaymentProcessor {
    override fun pay() {
        // UPI payment
    }
}

class CardPaymentProcessor : PaymentProcessor {
    override fun pay() {
        // Card payment
    }
}
```

Here:

- `UpiPaymentProcessor IS-A PaymentProcessor`
- `CardPaymentProcessor IS-A PaymentProcessor`

Because they implement the interface.

---

## 17. How Everything Fits Together

```
                         LLD
                          |
              ┌───────────┴───────────┐
              |                       |
           Classes                Relationships
                                      |
                              ┌───────┴───────┐
                              |               |
                            Is-a            Has-a
                              |               |
                        Inheritance       Composition
```

Design Patterns:

```
                    Design Patterns
                          |
              ┌───────────┼───────────┐
              ↓           ↓           ↓
          Creational   Structural   Behavioral
              |           |           |
           Create      Connect    Communicate
              |           |           |
           Builder      Adapter     Strategy
           Factory      Facade      Observer
           Singleton    Decorator   State
```

---

## 18. Quick Revision

**LLD**
Detailed design of classes, objects, interfaces and their relationships.

**Design Pattern**
A proven solution to a common software design problem.

**Creational** — How objects are created.
Builder, Factory, Singleton

**Structural** — How objects/classes are connected.
Adapter, Facade, Decorator

**Behavioral** — How objects communicate.
Strategy, Observer, State, Command

**Is-a** — Inheritance / implementation.
`Dog IS-A Animal`, `Car IS-A Vehicle`

**Has-a** — Composition / aggregation.
`Car HAS-A Engine`, `Order HAS-A Payment`, `Repository HAS-A ApiService`

---

## 19. One-Line Memory Trick

```
LLD
 ↓
Design Classes
 ↓
Understand Relationships
 ↓
Choose Design Patterns
 ↓
Create → Connect → Communicate
```

And for relationships:

- **IS-A** → Inheritance
- **HAS-A** → Composition

If you can say "is a" → think inheritance.
If you can say "has a" → think composition.


# LLD & Design Patterns — Cheat Sheet

**LLD** = Blueprint of classes/objects before coding (structure, relationships, patterns).

**Why LLD?**
One giant class → hard to maintain. Split into services (`AuthService`, `PaymentService`, etc.) = single responsibility.

**Design Pattern** = Proven solution to a common design problem.

---

## 3 Categories — "Create → Connect → Communicate"

| Type | Question | Examples |
|---|---|---|
| Creational | How do I **create** objects? | Singleton, Factory, Builder |
| Structural | How do I **connect** objects? | Adapter, Facade, Decorator |
| Behavioral | How do objects **communicate**? | Observer, Strategy, State |

---

## Relationships

- **IS-A → Inheritance** — "X is a Y" → `Dog : Animal`
- **HAS-A → Composition** — "X has a Y" → `Car(engine: Engine)`

**Golden Rule:** Prefer **composition over inheritance** unless it's a genuine "is-a".

**Quick test:**
- Can say "is a" → Inheritance
- Can say "has a" → Composition

---

## One-Liner

```
LLD → Design Classes → Relationships (Is-a/Has-a) → Pick Pattern → Create/Connect/Communicate
```