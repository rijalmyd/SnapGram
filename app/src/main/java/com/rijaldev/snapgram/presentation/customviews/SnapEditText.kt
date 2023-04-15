package com.rijaldev.snapgram.presentation.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.getIntOrThrow
import androidx.core.widget.addTextChangedListener
import com.rijaldev.snapgram.R

class SnapEditText : AppCompatEditText, View.OnTouchListener {

    private var startIconDrawable: Drawable? = null
    private var showPasswordIconDrawable: Drawable? = null
    private var hidePasswordIconDrawable: Drawable? = null

    private lateinit var backgroundDrawable: Drawable
    private lateinit var backgroundErrorDrawable: Drawable
    private var backgroundCorrectDrawable: Drawable? = null

    private var minimumPasswordLength = 8
    private var etHint: String = ""
    private var isPasswordShown = false
    private var snapInputType = SnapInputType.PASSWORD

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setDrawableIcon(
            left = startIconDrawable,
            right = if (isPasswordShown) showPasswordIconDrawable else hidePasswordIconDrawable
        )
        background = if (error.isNullOrEmpty()) backgroundCorrectDrawable ?: backgroundDrawable else backgroundErrorDrawable
        hint = etHint
    }

    private fun init() {
        when (snapInputType) {
            SnapInputType.EMAIL -> {
                inputType = INPUT_TYPE_EMAIL
                etHint = resources.getString(R.string.email_hint)

                startIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_email)

                addTextChangedListener(onTextChanged = { email, _, _, _ ->
                    if (!isValidEmail(email)) setError(resources.getString(R.string.email_error), null)
                    backgroundCorrectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_snap_edit_text_correct)
                })
            }
            SnapInputType.PASSWORD -> {
                inputType = INPUT_TYPE_PASSWORD
                etHint = resources.getString(R.string.password_hint)

                startIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_password)
                showPasswordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_show_password)
                hidePasswordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_hide_password)

                addTextChangedListener(onTextChanged = { password, _, _, _ ->
                    if (!isValidPassword(password)) setError(resources.getString(R.string.password_error), null)
                    backgroundCorrectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_snap_edit_text_correct)
                })

                setOnTouchListener(this)
            }
            SnapInputType.PASSWORD_CONFIRMATION -> {
                inputType = INPUT_TYPE_PASSWORD
                etHint = resources.getString(R.string.password_confirmation_hint)

                startIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_password)
                showPasswordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_show_password)
                hidePasswordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_hide_password)

                addTextChangedListener(onTextChanged = { password, _, _, _ ->
                    if (!isValidPassword(password)) setError(resources.getString(R.string.password_error), null)
                    backgroundCorrectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_snap_edit_text_correct)
                })

                setOnTouchListener(this)
            }
            SnapInputType.NAME -> {
                inputType = INPUT_TYPE_TEXT_NORMAL
                etHint = resources.getString(R.string.name)

                startIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_person)

                addTextChangedListener(onTextChanged = { name, _, _, _ ->
                    if (!isValidName(name)) setError(context.getString(R.string.name_error), null)
                    backgroundCorrectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_snap_edit_text_correct)
                })
            }
        }
        backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_snap_edit_text) as Drawable
        backgroundErrorDrawable = ContextCompat.getDrawable(context, R.drawable.bg_snap_edit_text_error) as Drawable
    }

    private fun getAttribute(attrs: AttributeSet?) {
        val style = context.obtainStyledAttributes(attrs, R.styleable.SnapEditText)
        minimumPasswordLength = style.getInt(R.styleable.SnapEditText_min_password_length, 8)
        snapInputType = when (style.getIntOrThrow(R.styleable.SnapEditText_custom_type)) {
            SnapInputType.PASSWORD.value -> SnapInputType.PASSWORD
            SnapInputType.EMAIL.value -> SnapInputType.EMAIL
            SnapInputType.PASSWORD_CONFIRMATION.value -> SnapInputType.PASSWORD_CONFIRMATION
            SnapInputType.NAME.value -> SnapInputType.NAME
            else -> throw IllegalArgumentException("Invalid custom_type value")
        }
        style.recycle()
    }

    private fun setDrawableIcon(
        left: Drawable? = null,
        top: Drawable? = null,
        right: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            left, top, right, bottom
        )
        compoundDrawablePadding = 16
    }

    fun isValidPassword(password: CharSequence?) =
        !password.isNullOrEmpty() && password.length >= minimumPasswordLength

    fun isValidEmail(email: CharSequence?) =
        !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidName(name: CharSequence?) = !name.isNullOrEmpty()

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val showHideButtonStart: Float
            val showHideButtonEnd: Float
            var isShowHideButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                showHideButtonEnd = ((hidePasswordIconDrawable?.intrinsicWidth ?: 0) + paddingStart).toFloat()
                when {
                    event.x < showHideButtonEnd -> isShowHideButtonClicked = true
                }
            } else {
                showHideButtonStart = (width - paddingEnd - (hidePasswordIconDrawable?.intrinsicWidth ?: 0)).toFloat()
                when {
                    event.x > showHideButtonStart -> isShowHideButtonClicked = true
                }
            }

            return if (isShowHideButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        isPasswordShown = !isPasswordShown
                        updatePasswordVisibility()
                        true
                    }
                    else -> false
                }
            } else false
        }
        return false
    }

    private fun updatePasswordVisibility() {
        inputType = if (isPasswordShown) INPUT_TYPE_VISIBLE_PASSWORD else INPUT_TYPE_PASSWORD
        setSelection(text?.length ?: 0)
    }

    enum class SnapInputType(val value: Int) {
        EMAIL(0),
        PASSWORD(1),
        PASSWORD_CONFIRMATION(2),
        NAME(3)
    }

    companion object {
        private const val INPUT_TYPE_EMAIL = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        private const val INPUT_TYPE_PASSWORD = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        private const val INPUT_TYPE_VISIBLE_PASSWORD = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        private const val INPUT_TYPE_TEXT_NORMAL = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
    }
}