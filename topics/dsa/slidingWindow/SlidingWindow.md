# Sliding Window (Fixed & Variable)

## What is the Sliding Window Pattern?

Sliding Window keeps a contiguous range (the window) over an array or string and moves it forward one step at a time. Instead of recomputing the answer for every range from scratch, you update it by adding the new element and removing the old one. This turns O(n * k) or O(n²) solutions into O(n).

## When to Use It

- The problem asks about a contiguous subarray or substring
- Keywords: longest, shortest, maximum, minimum, at most K, exactly K, window of size K, Sum, Count, Avg 
- You can update the answer cheaply when one element enters or leaves
- Input is an array or string (all positives for sum-based shrinking)
- You want O(n) time instead of checking every subarray

## Two Types of Sliding Window

| Type | Window | Movement | Example |
|---|---|---|---|
| Fixed Size | Always exactly k elements | Add right, remove left, both move together | Max sum of subarray of size k |
| Variable Size | Grows and shrinks based on a condition | Expand right always, shrink left while invalid | Longest substring without repeating characters |

## Template 1: Fixed Size Window

```kotlin
fun fixedWindow(a: IntArray, k: Int): Int {
    var windowSum = 0
    var best = Int.MIN_VALUE
    for (right in a.indices) {
        windowSum += a[right]                 // add new element
        if (right >= k - 1) {                 // window is full
            best = maxOf(best, windowSum)
            windowSum -= a[right - (k - 1)]   // remove the oldest element
        }
    }
    return best
}
// Time: O(n)   Space: O(1)
```

## Template 2: Variable Window (Longest Valid)

```kotlin
fun longestValid(a: IntArray): Int {
    var left = 0
    var best = 0
    for (right in a.indices) {
        // 1. add a[right] to the window state

        while (/* window is invalid */ false) {
            // 2. remove a[left] from the window state
            left++
        }
        // 3. window is valid here, update the answer
        best = maxOf(best, right - left + 1)
    }
    return best
}
// Time: O(n)   Space: depends on window state
```

## Template 3: Variable Window (Shortest Valid)

```kotlin
fun shortestValid(a: IntArray): Int {
    var left = 0
    var best = Int.MAX_VALUE
    for (right in a.indices) {
        // 1. add a[right] to the window state

        while (/* window is valid */ false) {
            best = minOf(best, right - left + 1)   // update before shrinking
            // 2. remove a[left] from the window state
            left++
        }
    }
    return if (best == Int.MAX_VALUE) 0 else best
}
```

## Longest vs Shortest: Where to Update the Answer

| Goal | Shrink while | Update answer |
|---|---|---|
| Longest valid window | Window is invalid | After the while loop |
| Shortest valid window | Window is valid | Inside the while loop |
| Count of valid windows | Window is invalid | Add (right - left + 1) after the loop |

## Example 1: Maximum Sum Subarray of Size K

```kotlin
fun maxSumSubarray(nums: IntArray, k: Int): Int {
    var sum = 0
    for (i in 0 until k) sum += nums[i]
    var best = sum
    for (i in k until nums.size) {
        sum += nums[i] - nums[i - k]
        best = maxOf(best, sum)
    }
    return best
}
// Time: O(n)   Space: O(1)
```

## Example 2: Longest Substring Without Repeating Characters

```kotlin
fun lengthOfLongestSubstring(s: String): Int {
    val seen = HashSet<Char>()
    var left = 0
    var best = 0
    for (right in s.indices) {
        while (s[right] in seen) {
            seen.remove(s[left])
            left++
        }
        seen.add(s[right])
        best = maxOf(best, right - left + 1)
    }
    return best
}
// Time: O(n)   Space: O(min(n, alphabet))
```

## Example 3: Minimum Size Subarray Sum

```kotlin
fun minSubArrayLen(target: Int, nums: IntArray): Int {
    var left = 0
    var sum = 0
    var best = Int.MAX_VALUE
    for (right in nums.indices) {
        sum += nums[right]
        while (sum >= target) {
            best = minOf(best, right - left + 1)
            sum -= nums[left]
            left++
        }
    }
    return if (best == Int.MAX_VALUE) 0 else best
}
// Time: O(n)   Space: O(1)
// Works because all numbers are positive
```

## Example 4: Longest Substring with At Most K Distinct Characters

```kotlin
fun longestKDistinct(s: String, k: Int): Int {
    val count = HashMap<Char, Int>()
    var left = 0
    var best = 0
    for (right in s.indices) {
        count[s[right]] = (count[s[right]] ?: 0) + 1
        while (count.size > k) {
            val c = s[left]
            count[c] = count[c]!! - 1
            if (count[c] == 0) count.remove(c)
            left++
        }
        best = maxOf(best, right - left + 1)
    }
    return best
}
// Time: O(n)   Space: O(k)
```

## Example 5: Longest Repeating Character Replacement

```kotlin
fun characterReplacement(s: String, k: Int): Int {
    val count = IntArray(26)
    var left = 0
    var maxFreq = 0
    var best = 0
    for (right in s.indices) {
        count[s[right] - 'A']++
        maxFreq = maxOf(maxFreq, count[s[right] - 'A'])
        // replacements needed = window size - most frequent char
        while ((right - left + 1) - maxFreq > k) {
            count[s[left] - 'A']--
            left++
        }
        best = maxOf(best, right - left + 1)
    }
    return best
}
// Time: O(n)   Space: O(1)
```

## Example 6: Permutation in String (Fixed Window)

```kotlin
fun checkInclusion(s1: String, s2: String): Boolean {
    if (s1.length > s2.length) return false
    val need = IntArray(26)
    val have = IntArray(26)
    for (c in s1) need[c - 'a']++
    for (i in s2.indices) {
        have[s2[i] - 'a']++
        if (i >= s1.length) have[s2[i - s1.length] - 'a']--
        if (need.contentEquals(have)) return true
    }
    return false
}
// Time: O(n * 26)   Space: O(1)
```

## Example 7: Find All Anagrams in a String

```kotlin
fun findAnagrams(s: String, p: String): List<Int> {
    val result = mutableListOf<Int>()
    if (p.length > s.length) return result
    val need = IntArray(26)
    val have = IntArray(26)
    for (c in p) need[c - 'a']++
    for (i in s.indices) {
        have[s[i] - 'a']++
        if (i >= p.length) have[s[i - p.length] - 'a']--
        if (i >= p.length - 1 && need.contentEquals(have)) {
            result.add(i - p.length + 1)
        }
    }
    return result
}
// Time: O(n * 26)   Space: O(1)
```

## Example 8: Max Consecutive Ones III

```kotlin
fun longestOnes(nums: IntArray, k: Int): Int {
    var left = 0
    var zeros = 0
    var best = 0
    for (right in nums.indices) {
        if (nums[right] == 0) zeros++
        while (zeros > k) {
            if (nums[left] == 0) zeros--
            left++
        }
        best = maxOf(best, right - left + 1)
    }
    return best
}
// Time: O(n)   Space: O(1)
```

## Example 9: Fruit Into Baskets

```kotlin
fun totalFruit(fruits: IntArray): Int {
    val basket = HashMap<Int, Int>()
    var left = 0
    var best = 0
    for (right in fruits.indices) {
        basket[fruits[right]] = (basket[fruits[right]] ?: 0) + 1
        while (basket.size > 2) {
            val f = fruits[left]
            basket[f] = basket[f]!! - 1
            if (basket[f] == 0) basket.remove(f)
            left++
        }
        best = maxOf(best, right - left + 1)
    }
    return best
}
// Time: O(n)   Space: O(1)
```

## Example 10: Minimum Window Substring

```kotlin
fun minWindow(s: String, t: String): String {
    if (t.isEmpty() || s.length < t.length) return ""
    val need = HashMap<Char, Int>()
    for (c in t) need[c] = (need[c] ?: 0) + 1
    val window = HashMap<Char, Int>()
    var have = 0
    val required = need.size
    var left = 0
    var bestLen = Int.MAX_VALUE
    var bestStart = 0

    for (right in s.indices) {
        val c = s[right]
        window[c] = (window[c] ?: 0) + 1
        if (c in need && window[c] == need[c]) have++

        while (have == required) {
            if (right - left + 1 < bestLen) {
                bestLen = right - left + 1
                bestStart = left
            }
            val l = s[left]
            window[l] = window[l]!! - 1
            if (l in need && window[l]!! < need[l]!!) have--
            left++
        }
    }
    return if (bestLen == Int.MAX_VALUE) "" else s.substring(bestStart, bestStart + bestLen)
}
// Time: O(n + m)   Space: O(m)
```

## Example 11: Subarrays with Exactly K Distinct (At Most Trick)

```kotlin
fun subarraysWithKDistinct(nums: IntArray, k: Int): Int =
    atMost(nums, k) - atMost(nums, k - 1)

fun atMost(nums: IntArray, k: Int): Int {
    val count = HashMap<Int, Int>()
    var left = 0
    var total = 0
    for (right in nums.indices) {
        count[nums[right]] = (count[nums[right]] ?: 0) + 1
        while (count.size > k) {
            val x = nums[left]
            count[x] = count[x]!! - 1
            if (count[x] == 0) count.remove(x)
            left++
        }
        total += right - left + 1   // all windows ending at right
    }
    return total
}
// Time: O(n)   Space: O(k)
```

## Example 12: Sliding Window Maximum (Deque)

```kotlin
fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
    val result = IntArray(nums.size - k + 1)
    val dq = ArrayDeque<Int>()          // stores indices, values decreasing
    for (i in nums.indices) {
        if (dq.isNotEmpty() && dq.first() <= i - k) dq.removeFirst()
        while (dq.isNotEmpty() && nums[dq.last()] < nums[i]) dq.removeLast()
        dq.addLast(i)
        if (i >= k - 1) result[i - k + 1] = nums[dq.first()]
    }
    return result
}
// Time: O(n)   Space: O(k)
// See the Monotonic Queue topic for details
```

## Window State: What to Track

| Problem Type | State |
|---|---|
| Sum of window | Single Int (running sum) |
| No repeating characters | HashSet or last-seen index map |
| At most K distinct | HashMap of counts, check map.size |
| Anagram / permutation | IntArray(26) counts, compare arrays |
| Count zeros / flips allowed | Counter of invalid items |
| Cover all required characters | Need map + have counter |
| Max or min of window | Monotonic deque |

## Problem Cheat Sheet

| Problem | Type | Key Idea |
|---|---|---|
| Max Sum Subarray of Size K | Fixed | Add new, subtract old |
| Longest Substring Without Repeating | Variable (longest) | Shrink while duplicate exists |
| Minimum Size Subarray Sum | Variable (shortest) | Shrink while sum >= target |
| Longest with At Most K Distinct | Variable (longest) | Map size > K means shrink |
| Character Replacement | Variable (longest) | Window size - max freq <= K |
| Permutation in String | Fixed | Compare 26-letter counts |
| Minimum Window Substring | Variable (shortest) | Track have vs required |
| Max Consecutive Ones III | Variable (longest) | At most K zeros |
| Subarrays with Exactly K | At most trick | atMost(k) - atMost(k - 1) |
| Sliding Window Maximum | Fixed + deque | Monotonic decreasing deque |

## Sliding Window vs Prefix Sum

- Sliding window needs a condition that gets better or worse as the window grows (monotonic)
- With only positive numbers, a larger window always has a larger sum, so shrinking works
- With negative numbers, shrinking does not always reduce the sum, so use Prefix Sum + HashMap
- For 'subarray sum equals K' with negatives, do not use sliding window

## Common Mistakes

- Using sliding window on arrays with negative numbers for sum problems
- Updating the answer in the wrong place (longest: after loop, shortest: inside loop)
- Forgetting to remove a key from the map when its count reaches 0
- Off by one: window size is right - left + 1
- Using if instead of while when shrinking, so the window stays invalid
- Not handling k larger than the array length
- Forgetting that exactly K is atMost(K) - atMost(K - 1)

## Complexity Summary

| Approach | Time | Space |
|---|---|---|
| Brute force (all subarrays) | O(n²) or O(n³) | O(1) |
| Fixed window | O(n) | O(1) |
| Variable window with counter or set | O(n) | O(k) or O(alphabet) |
| Window with deque (max/min) | O(n) | O(k) |

## Why Variable Window is O(n)

It looks like a nested loop, but each element enters the window once (right pointer) and leaves at most once (left pointer). That is at most 2n pointer moves in total, so the time is O(n).

## Practice Problems

- Maximum Average Subarray I
- Longest Substring Without Repeating Characters
- Minimum Size Subarray Sum
- Longest Substring with At Most K Distinct Characters
- Longest Repeating Character Replacement
- Permutation in String
- Find All Anagrams in a String
- Max Consecutive Ones III
- Fruit Into Baskets
- Minimum Window Substring
- Subarrays with K Different Integers
- Sliding Window Maximum
- Substring with Concatenation of All Words

## Key Takeaways

- Sliding window is for contiguous subarrays and substrings
- Fixed window: add one, remove one, update the answer
- Variable window: expand right, shrink left while the condition is broken
- Longest: update after shrinking. Shortest: update while valid
- Exactly K equals atMost(K) minus atMost(K - 1)
- Each element enters and leaves once, so the time is O(n)