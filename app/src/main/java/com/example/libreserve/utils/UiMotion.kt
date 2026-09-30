package com.example.libreserve.utils

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

/** Short, finite transitions that respect the device animation setting. */
object UiMotion {
    fun enter(view: View, delay: Long = 0) {
        if (!ValueAnimator.areAnimatorsEnabled()) return
        view.alpha = 0f
        view.translationY = 18f * view.resources.displayMetrics.density
        view.animate().alpha(1f).translationY(0f).setStartDelay(delay)
            .setDuration(420).setInterpolator(DecelerateInterpolator()).start()
    }

    fun select(view: View) {
        view.animate().cancel()
        if (!ValueAnimator.areAnimatorsEnabled()) return
        view.scaleX = 0.92f
        view.scaleY = 0.92f
        view.animate().scaleX(1f).scaleY(1f).setStartDelay(0)
            .setDuration(220).setInterpolator(DecelerateInterpolator()).start()
    }
}
