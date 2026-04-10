// Handy Android extension functions — add yours here over time

// Show a toast in one line
fun Context.toast(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

// Hide the soft keyboard
fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

// dp to px conversion
fun Context.dpToPx(dp: Float): Int =
    (dp * resources.displayMetrics.density).toInt()
