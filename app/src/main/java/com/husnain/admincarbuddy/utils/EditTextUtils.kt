package com.husnain.admincarbuddy.utils

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.husnain.admincarbuddy.R

@SuppressLint("ClickableViewAccessibility")
fun setPasswordVisibilityToggle(editText: EditText, ) { editText.setOnTouchListener { view, motionEvent ->
    val drawableEnd = editText.compoundDrawablesRelative[2]
    if (drawableEnd != null && motionEvent.action == MotionEvent.ACTION_UP) {
        val bounds = drawableEnd.bounds
        val x = motionEvent.x.toInt()

        if (x >= view.width - view.paddingRight - bounds.width()) {
            val cursorPosition = editText.selectionStart
            val isPasswordVisible =
                editText.transformationMethod == PasswordTransformationMethod.getInstance()

            editText.transformationMethod =
                if (isPasswordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()

            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (isPasswordVisible) R.drawable.icon_password_visibility_off else R.drawable.icon_password_visibility_on,
                0
            )

            editText.setSelection(cursorPosition)
            return@setOnTouchListener true
        }
    }
    false
} }
fun EditText.ifEmpty(errorMessage: String): Boolean {
    val text = this.text?.toString()
    if (text.isNullOrEmpty()) {
        this.error = errorMessage
        this.requestFocus()
        return true
    }
    return false
}

fun EditText.ifEmailNotMatches(errorMessage: String):Boolean{
    val email = this.text?.toString()
    if (!email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }!!) {
        this.error = errorMessage
        this.requestFocus()
        return true
    }
    return false
}




