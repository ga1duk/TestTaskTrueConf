package ru.netology.testtasktrueconf

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.netology.testtasktrueconf.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var textViewPosition: Float = 0.0f

    companion object {
        private const val ANIMATION_DURATION = 3000L
        private const val ANIMATION_START_DELAY = 5000L
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var animator1: Animator
        var animator2: Animator
        var animatorSet: AnimatorSet? = null

        var isAnimationStartedFirstTime = true

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        val text = binding.tvHello.text as String
        val textBound = Rect()
        binding.tvHello.paint.getTextBounds(text, 0, text.length, textBound)
        val textHeight: Int = textBound.height()
        val textCenterX = textBound.centerX()
        val textCenterY = textBound.centerY()

        binding.tvHello.setOnClickListener {
            animatorSet?.let {
                it.cancel()
                it.removeAllListeners()
            }
        }

        binding.root.setOnTouchListener { _, event ->
            val x = event.x - textCenterX
            val y = event.y - textCenterY

            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    animatorSet?.let {
                        it.cancel()
                        it.removeAllListeners()
                    }
                    with(binding) {
                        tvHello.height
                        tvHello.translationX = x
                        tvHello.translationY = y
                        tvHello.setTextColor(resources.getColor(R.color.textview_hello_on_click_color))
                        textViewPosition = tvHello.translationY
                    }
                }
                MotionEvent.ACTION_MOVE -> {

                }
                MotionEvent.ACTION_UP -> {
                    animator1 = ObjectAnimator.ofFloat(
                        binding.tvHello,
                        View.TRANSLATION_Y,
                        textViewPosition,
                        screenHeight - getStatusBarHeight() - getActionBarHeight() -
                                AndroidUtils.convertDpToPx(this, textHeight.toFloat())
                    ).apply {
                        duration = ANIMATION_DURATION
                        if (isAnimationStartedFirstTime) {
                            startDelay = ANIMATION_START_DELAY
                            isAnimationStartedFirstTime = false
                        }
                    }

                    animator2 = ObjectAnimator.ofFloat(
                        binding.tvHello,
                        View.TRANSLATION_Y,
                        screenHeight - getStatusBarHeight() - getActionBarHeight() -
                                AndroidUtils.convertDpToPx(this, textHeight.toFloat()),
                        0F
                    ).apply {
                        duration = ANIMATION_DURATION
                        repeatMode = ObjectAnimator.REVERSE
                        repeatCount = ObjectAnimator.INFINITE
                    }

                    animatorSet = AnimatorSet()
                    animatorSet?.apply {
                        playSequentially(animator1, animator2)
                    }?.start()
                }

                MotionEvent.ACTION_CANCEL -> {

                }
            }
            return@setOnTouchListener true
        }
    }

    private fun getStatusBarHeight(): Float {
        var result = 0F
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimension(resourceId)
        }
        return result
    }

    private fun getActionBarHeight(): Int {
        val styledAttributes: TypedArray =
            theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return actionBarSize
    }
}