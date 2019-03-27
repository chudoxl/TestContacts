package com.github.chudoxl.testcontacts.tools

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import timber.log.Timber


fun Fragment.showToast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    showKeyboard(this, 5)
}

private val showKeyboardHandler = Handler(Looper.getMainLooper())

private fun showKeyboard(v: View, tries: Int) {
    if (tries <= 0)
        return;
    v.requestFocus()
    v.postDelayed({
        showKeyboard(v, object : ResultReceiver(showKeyboardHandler) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                Timber.d("showKeyboard... onReceiveResult: %d", resultCode);
                if (resultCode != InputMethodManager.RESULT_UNCHANGED_SHOWN && resultCode != InputMethodManager.RESULT_SHOWN) {
                    v.postDelayed({ showKeyboard(v, tries - 1) }, 100)
                }
            }
        })
    }, 100)
}

private fun showKeyboard(v: View, resultReceiver: ResultReceiver) {
    Timber.d("showKeyboard with resultReceiver..");
    val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(v, 0, resultReceiver)
}