package com.husnain.admincarbuddy.utils

import android.view.View
import androidx.core.view.isVisible

fun View.animateShow(offsetY: Float) {
    this.apply {
        alpha = 0f
        isVisible = true
        translationY = 0f
        animate()
            .alpha(1f)
            .translationY(-offsetY)
            .setDuration(300)
            .start()
    }
}

fun View.animateHide(offsetY: Float) {
    this.apply {
        alpha = 1f
        translationY = -offsetY
        animate()
            .alpha(0f)
            .translationY(0f)
            .setDuration(300)
            .withEndAction { isVisible = false }
            .start()
    }
}

