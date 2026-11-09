# Two Pointers

## What is the Two Pointers Pattern?

Two Pointers uses two indices that move through a data structure, usually an array, string or linked list, to avoid a nested loop. Each pointer only moves in one direction, so the total work is O(n) instead of O(n²). It works best when the data is sorted or when the problem depends on a relationship between two positions.

## When to Use It

- The array or string is sorted (or can be sorted)
- You need to find a pair, triplet or subarray with a given sum
- You must compare elements from both ends (palindromes)
- You need to modify an array in place (remove duplicates, move zeros)
- You need to merge two sorted sequences
- The problem asks for O(1) extra space

## Three Types of Two Pointers

| Type | Start | Movement | Example |
|---|---|---|---|
| Opposite Ends | left = 0, right = n - 1 | Move toward each other | Two Sum II, Container With Most Water |
| Same Direction (Read/Write) | slow = 0, fast = 0 | Both move forward, fast leads | Remove Duplicates, Move Zeros |
| Two Sequences | i = 0 in A, j = 0 in B | Advance whichever is smaller | Merge Sorted Arrays, Intersection |

## Template 1: Opposite Ends

```kotlin
fun oppositeEnds(a: IntArray, target: Int): IntArray {
    var left = 0
    var right = a.size - 1
    while (left < right) {
        val sum = a[left] + a[right]
        when {
            sum == target -> return intArrayOf(left, right)
            sum < target -> left++     // need a bigger sum
            else -> right--            // need a smaller sum
        }
    }
    return intArrayOf(-1, -1)
}
// Time: O(n)   Space: O(1)
```

## Template 2: Same Direction (Read / Write)

```kotlin
fun sameDirection(nums: IntArray): Int {
    var slow = 0

    for (fast in nums.indices) {
        if (/* condition to keep nums[fast] */) {
            nums[slow] = nums[fast]
            slow++
        }
    }

    return slow
}

// Time: O(n)
// Space: O(1)
```

## Template 3: Two Sequences

```kotlin
fun twoSequences(a: IntArray, b: IntArray) {
    var i = 0
    var j = 0
    while (i < a.size && j < b.size) {
        when {
            a[i] < b[j] -> i++
            a[i] > b[j] -> j++
            else -> { /* match found */ i++; j++ }
        }
    }
}
// Time: O(n + m)   Space: O(1)
```

## Example 1: Two Sum II (Sorted Array)

```kotlin
fun twoSumSorted(numbers: IntArray, target: Int): IntArray {
    var l = 0
    var r = numbers.size - 1
    while (l < r) {
        val sum = numbers[l] + numbers[r]
        if (sum == target) return intArrayOf(l + 1, r + 1)  // 1-indexed
        if (sum < target) l++ else r--
    }
    return intArrayOf()
}
// Time: O(n)   Space: O(1)
```

## Example 2: Valid Palindrome

```kotlin
fun isPalindrome(s: String): Boolean {
    var l = 0
    var r = s.length - 1
    while (l < r) {
        while (l < r && !s[l].isLetterOrDigit()) l++
        while (l < r && !s[r].isLetterOrDigit()) r--
        if (s[l].lowercaseChar() != s[r].lowercaseChar()) return false
        l++
        r--
    }
    return true
}
// Time: O(n)   Space: O(1)
```

## Example 3: Remove Duplicates from Sorted Array

```kotlin
fun removeDuplicates(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    var write = 1
    for (read in 1 until nums.size) {
        if (nums[read] != nums[read - 1]) {
            nums[write] = nums[read]
            write++
        }
    }
    return write
}
// Time: O(n)   Space: O(1)
```

## Example 4: Move Zeroes

```kotlin
fun moveZeroes(nums: IntArray) {
    var write = 0
    for (read in nums.indices) {
        if (nums[read] != 0) {
            val tmp = nums[write]
            nums[write] = nums[read]
            nums[read] = tmp
            write++
        }
    }
}
// Time: O(n)   Space: O(1)
```

## Example 5: 3Sum

```kotlin
fun threeSum(nums: IntArray): List<List<Int>> {
    nums.sort()
    val result = mutableListOf<List<Int>>()
    for (i in 0 until nums.size - 2) {
        if (i > 0 && nums[i] == nums[i - 1]) continue   // skip duplicates
        var l = i + 1
        var r = nums.size - 1
        while (l < r) {
            val sum = nums[i] + nums[l] + nums[r]
            when {
                sum == 0 -> {
                    result.add(listOf(nums[i], nums[l], nums[r]))
                    l++
                    r--
                    while (l < r && nums[l] == nums[l - 1]) l++
                    while (l < r && nums[r] == nums[r + 1]) r--
                }
                sum < 0 -> l++
                else -> r--
            }
        }
    }
    return result
}
// Time: O(n²)   Space: O(1) extra (ignoring output)
```

## Example 6: Container With Most Water

```kotlin
fun maxArea(height: IntArray): Int {
    var l = 0
    var r = height.size - 1
    var best = 0
    while (l < r) {
        val area = minOf(height[l], height[r]) * (r - l)
        best = maxOf(best, area)
        // Move the shorter wall; moving the taller one cannot help
        if (height[l] < height[r]) l++ else r--
    }
    return best
}
// Time: O(n)   Space: O(1)
```

## Example 7: Merge Sorted Array (In Place)

```kotlin
fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int) {
    var i = m - 1          // last real element in nums1
    var j = n - 1          // last element in nums2
    var k = m + n - 1      // last position in nums1
    while (j >= 0) {
        if (i >= 0 && nums1[i] > nums2[j]) {
            nums1[k--] = nums1[i--]
        } else {
            nums1[k--] = nums2[j--]
        }
    }
}
// Time: O(m + n)   Space: O(1)
// Trick: fill from the back so nothing is overwritten
```

## Example 8: Longest Palindromic Substring (Expand from Center)

```kotlin
fun longestPalindrome(s: String): String {
    var start = 0
    var maxLen = 0

    fun expand(left: Int, right: Int) {
        var l = left
        var r = right
        while (l >= 0 && r < s.length && s[l] == s[r]) {
            l--
            r++
        }
        val len = r - l - 1
        if (len > maxLen) {
            maxLen = len
            start = l + 1
        }
    }

    for (i in s.indices) {
        expand(i, i)       // odd length
        expand(i, i + 1)   // even length
    }
    return s.substring(start, start + maxLen)
}
// Time: O(n²)   Space: O(1)
```

## Example 9: Sort Colors (Dutch National Flag)

```kotlin
fun sortColors(nums: IntArray) {
    var low = 0
    var mid = 0
    var high = nums.size - 1
    while (mid <= high) {
        when (nums[mid]) {
            0 -> { swap(nums, low, mid); low++; mid++ }
            1 -> mid++
            else -> { swap(nums, mid, high); high-- }   // do not move mid
        }
    }
}

fun swap(a: IntArray, i: Int, j: Int) {
    val t = a[i]; a[i] = a[j]; a[j] = t
}
// Time: O(n)   Space: O(1)
```

## Example 10: Trapping Rain Water

```kotlin
fun trap(height: IntArray): Int {
    var l = 0
    var r = height.size - 1
    var leftMax = 0
    var rightMax = 0
    var water = 0
    while (l < r) {
        if (height[l] < height[r]) {
            if (height[l] >= leftMax) leftMax = height[l]
            else water += leftMax - height[l]
            l++
        } else {
            if (height[r] >= rightMax) rightMax = height[r]
            else water += rightMax - height[r]
            r--
        }
    }
    return water
}
// Time: O(n)   Space: O(1)
```

## Problem Cheat Sheet

| Problem | Type | Key Idea |
|---|---|---|
| Two Sum II (sorted) | Opposite ends | Sum too small: move left; too big: move right |
| 3Sum / 4Sum | Sort + opposite ends | Fix one (or two) numbers, two pointers for the rest, skip duplicates |
| Valid Palindrome | Opposite ends | Compare ends, skip non-alphanumeric |
| Container With Most Water | Opposite ends | Always move the shorter wall |
| Trapping Rain Water | Opposite ends | Track max from the smaller side |
| Remove Duplicates | Read / write | Write only when the value changes |
| Move Zeroes | Read / write | Write non-zeros forward |
| Sort Colors | Three pointers | Dutch flag: low, mid, high |
| Merge Sorted Array | Two sequences | Fill from the back |
| Longest Palindromic Substring | Expand from center | Try each center, odd and even |
| Intersection of Two Arrays | Two sequences | Advance the smaller pointer |

## Why Moving the Pointer is Safe

- In Two Sum II, if the sum is too small, a[left] paired with any smaller right value will be even smaller, so left can never be part of the answer
- In Container With Most Water, the area is limited by the shorter wall, so moving the taller wall can only shrink the width without raising the height
- Always be able to explain why discarding a position cannot lose the answer
- This reasoning is what makes the O(n) solution correct

## Common Mistakes

- Using two pointers on an unsorted array when order matters (sort first or use a HashMap)
- Using left <= right when left < right is needed (and the other way around)
- Not skipping duplicates in 3Sum, which gives repeated triplets
- Moving the wrong pointer when values are equal
- Forgetting to handle empty arrays or a single element
- Overwriting values in an in-place merge by filling from the front
- Sorting changes indices, so use a HashMap if the original indices are required

## Complexity Summary

| Approach | Time | Space |
|---|---|---|
| Brute force nested loops | O(n²) | O(1) |
| Two pointers on sorted data | O(n) | O(1) |
| Sort + two pointers | O(n log n) | O(1) or O(log n) |
| Sort + fix one + two pointers (3Sum) | O(n²) | O(1) |
| HashMap alternative (Two Sum) | O(n) | O(n) |

## Practice Problems

- Two Sum II - Input Array Is Sorted
- Valid Palindrome / Valid Palindrome II
- Remove Duplicates from Sorted Array I and II
- Move Zeroes
- 3Sum, 3Sum Closest, 4Sum
- Container With Most Water
- Trapping Rain Water
- Sort Colors
- Merge Sorted Array
- Squares of a Sorted Array
- Reverse String / Reverse Words in a String
- Backspace String Compare
- Longest Palindromic Substring

## Key Takeaways

- Two pointers turns many O(n²) problems into O(n)
- Opposite ends works on sorted data and symmetric checks
- Read / write pointers modify arrays in place
- Two sequences merge or compare sorted inputs
- Always know why moving a pointer cannot skip the answer
- Sort first when the problem allows it, and skip duplicates