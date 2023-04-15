package com.rijaldev.snapgram.presentation.customviews

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.rijaldev.snapgram.R

class SnapProgressButton : FrameLayout {

    private lateinit var backgroundDrawableFill: Drawable
    private lateinit var backgroundDrawableDisabled: Drawable
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rootLayout: FrameLayout

    private var buttonTitle: String = ""
    private var isButtonEnabled = true
    private var isLoaderShown = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        getAttribute(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        getAttribute(attrs)
        init()
    }

    private fun init() {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        LayoutInflater.from(context).inflate(R.layout.progress_button, this, true)

        rootLayout = findViewById(R.id.root_layout)
        textView = findViewById(R.id.tv_title)
        progressBar = findViewById(R.id.pb_circle)

        backgroundDrawableFill = ContextCompat.getDrawable(context, R.drawable.bg_snap_progress_button_filled) as Drawable
        backgroundDrawableDisabled = ContextCompat.getDrawable(context, R.drawable.bg_snap_progress_button_disabled) as Drawable

        textView.text = buttonTitle
        rootLayout.background = if (isButtonEnabled) backgroundDrawableFill else backgroundDrawableDisabled
    }

    private fun getAttribute(attrs: AttributeSet?) {
        val style = context.obtainStyledAttributes(attrs, R.styleable.SnapProgressButton)
        buttonTitle = style.getString(R.styleable.SnapProgressButton_text).toString()
        isButtonEnabled = style.getBoolean(R.styleable.SnapProgressButton_enabled, true)
        style.recycle()
    }

    fun showLoader() {
        isLoaderShown = true
        textView.isInvisible = true
        progressBar.isVisible = true
    }

    fun hideLoader() {
        isLoaderShown = false
        textView.isVisible = true
        progressBar.isVisible = false
    }
}