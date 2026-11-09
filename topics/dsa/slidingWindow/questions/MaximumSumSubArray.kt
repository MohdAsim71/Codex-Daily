package dsa.slidingWindow.questions

/*
Maximum Sum of Distinct Subarrays With Length K
Medium

You are given an integer array nums and an integer k. Find the maximum subarray sum of all the
subarrays of nums that meet the following conditions:

The length of the subarray is k, and
All the elements of the subarray are distinct.
Return the maximum subarray sum of all the subarrays that meet the conditions.
If no subarray meets the conditions, return 0.

A subarray is a contiguous non-empty sequence of elements within an array.

Example 1:
Input: nums = [1,5,4,2,9,9,9], k = 3
Output: 15

Example 2:
Input: nums = [4,4,4], k = 3
Output: 0

Constraints:
1 <= k <= nums.length <= 10^5
1 <= nums[i] <= 10^5
*/

// ─── Solution ────────────────────────────────────────────────────────────────
// Approach : Sliding Window  |  Time: O(n)  |  Space: O(k)
//
// Key insight: maintain a fixed-size window of length k.
// Track element frequencies with a HashMap.
// A window is "all distinct" ⟺ freq.size == k (every key appears exactly once).
// Slide the window, keeping a running sum and updating the max when valid.
// ─────────────────────────────────────────────────────────────────────────────

fun maximumSubarraySum(nums: IntArray, k: Int): Int {
    var windowSum = 0
    var maxSum = 0

    for (i in 0 until k)
    {
        windowSum += nums[i]
    }

    maxSum = windowSum
    for (right in k until nums.size) {
        windowSum =  windowSum + nums[right]-nums[right-k]
        maxSum  = maxOf(windowSum,maxSum)
    }
    return maxSum
}

// ─── Main ─────────────────────────────────────────────────────────────────────
fun main() {
    // Test 1 → expected: 15  ([4,2,9] = 15)
    println("Test 1: ${maximumSubarraySum(intArrayOf(1, 5, 4, 2, 9, 9, 9), 3)}")

    // Test 2 → expected: 0   (all windows have duplicates)
    println("Test 2: ${maximumSubarraySum(intArrayOf(4, 4, 4), 3)}")

    // Test 3 → expected: 5   (k=1, max single element)
    println("Test 3: ${maximumSubarraySum(intArrayOf(1, 2, 5, 3), 1)}")

    // Test 4 → expected: 15  (entire array [1,2,3,4,5] is distinct)
    println("Test 4: ${maximumSubarraySum(intArrayOf(1, 2, 3, 4, 5), 5)}")
}
