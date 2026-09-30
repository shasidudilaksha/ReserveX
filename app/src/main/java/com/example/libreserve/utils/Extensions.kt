package com.example.libreserve.utils
import android.view.View
import android.content.Context
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }
fun Context.showToast(message: String) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) { Snackbar.make(this, message, duration).show() }
fun View.showSnackbarWithAction(message: String, actionLabel: String, action: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).setAction(actionLabel) { action() }.show()
}
