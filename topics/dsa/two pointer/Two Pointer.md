DSA Pattern: Two Pointers

What is Two Pointers?

Use two indexes/pointers to traverse an array, string, or linked list efficiently instead of nested loops.

L →             ← R
[1, 2, 3, 4, 5, 6]

Goal: Often reduce O(n²) → O(n).

⸻

When to Use?

Think Two Pointers when you see:

* ✅ Sorted array
* ✅ Find a pair / two numbers
* ✅ Target sum
* ✅ Compare elements from both ends
* ✅ Palindrome
* ✅ Reverse array/string
* ✅ Remove duplicates in-place
* ✅ Move zeroes
* ✅ Modify/filter an array in-place
* ✅ 3Sum / pair-related problems

Golden Rule

Sorted Array + Pair/Target
↓
Think Two Pointers

⸻

Types of Two Pointers

1. Opposite Direction

Pointers start from both ends.

L →                 ← R
[1, 2, 3, 4, 5, 6]
var left = 0
var right = nums.lastIndex
while (left < right) {
// logic
left++
right--
}

Common Problems

* Palindrome
* Reverse Array
* Two Sum II
* 3Sum
* Container With Most Water

⸻

2. Same Direction — Slow/Fast

Both pointers move from left to right.

slow →
fast  →
[1, 2, 3, 4, 5, 6]

Usually:

* slow → position for valid/result data
* fast → scans the array

Common Problems

* Remove Duplicates
* Move Zeroes
* Remove Element
* In-place array modification
* Linked List Cycle

⸻

How to Identify?

Ask these questions:

1. Is it an Array/String?

If yes → Two Pointers may be possible.

2. Is the array sorted?

YES → 🚨 Strong Two Pointer candidate

3. Does the problem involve a pair?

Examples:

Pair
Two numbers
Target Sum
Difference
Closest Sum

→ Think Two Pointers.

4. Comparing from both ends?

YES → Opposite-direction pointers

5. Modifying/filtering in-place?

YES → Slow/Fast pointers

⸻

Example: Pair Sum

Given:

nums = [1, 2, 3, 4, 6, 8]
target = 10

Start:

L                   R
↓                   ↓
1  2  3  4  6  8

Calculate:

1 + 8 = 9

Too small → increase L.

2 + 8 = 10 ✅

Logic

var left = 0
var right = nums.lastIndex
while (left < right) {
val sum = nums[left] + nums[right]
when {
sum == target -> {
// found
break
}
sum < target -> left++
else -> right--
}
}

Complexity

Time:  O(n)
Space: O(1)

If sorting is required first:

Sorting: O(n log n)
Pointers: O(n)
Total: O(n log n)

⸻

Two Pointers vs Sliding Window

Two Pointers	Sliding Window
Pair/relationship between elements	Continuous range
Often sorted arrays	Subarray/substring
Can start from both ends	Usually left/right window
Two Sum	Longest substring
Palindrome	Max sum subarray
3Sum	Minimum window

Quick Rule

Pair / Sorted / Both Ends
↓
Two Pointers
Continuous Subarray / Substring
↓
Sliding Window

⸻

Interview Checklist

Before coding, ask:

1. Can I use two pointers?
2. Where should they start?
3. What does each pointer represent?
4. When should LEFT move?
5. When should RIGHT move?
6. Why is it safe to move that pointer?
7. Can I eliminate multiple possibilities?
8. What is the time complexity?
9. What is the space complexity?

⸻

⭐ Quick Cheat Sheet

SORTED ARRAY
↓
TWO POINTERS
↓
┌─────────────────────┐
│ Pair / Target       │ → L + R
│ Both Ends            │ → L → ← R
│ Palindrome           │ → L → ← R
│ Reverse              │ → L → ← R
│ Remove Duplicates    │ → Slow/Fast
│ Move Zeroes          │ → Slow/Fast
│ 3Sum                 │ → Sort + Two Pointers
└─────────────────────┘

Remember

Don’t memorize the solution. Identify why the pointer moves.

The key skill in Two Pointers is knowing which pointer to move and why.