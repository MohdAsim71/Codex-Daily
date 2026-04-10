// Reusable coroutine helpers


// Run a block safely and return Result
suspend fun <T> safeCall(block: suspend () -> T): Result<T> = runCatching { block() }

// Retry a suspending operation N times
suspend fun <T> retry(times: Int = 3, block: suspend () -> T): T {
    repeat(times - 1) {
        runCatching { return block() }
    }
    return block()
}
