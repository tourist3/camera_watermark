package com.mobileheros.camera.ui.dialog

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.animation.LinearInterpolator
import com.drake.net.time.Interval
import com.mobileheros.camera.R
import com.mobileheros.camera.databinding.DialogProgressBinding
import java.util.concurrent.TimeUnit

class ProgressDialog(mContext: Context, isUpload: Boolean = true) :
    Dialog(mContext) {
    private var ctx = mContext
    private lateinit var binding: DialogProgressBinding
    private var single = isUpload
    private var interval: Interval? = null
    private var index: Int = 0
    private val textList = listOf(
        ctx.getString(R.string.loading_tip1),
        ctx.getString(R.string.loading_tip2),
        ctx.getString(R.string.loading_tip3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        updateUI()
    }

    override fun show() {
        super.show()
        startAnimation()
    }

    private fun updateUI() {
        if (!single) {
            interval?.cancel()
            interval = Interval(2, TimeUnit.SECONDS).subscribe {
                binding.text.text = textList[index % 3]
                index++
            }.start()
        }
    }

    fun changeState(flag: Boolean) {
        single = flag
        updateUI()
    }

    private lateinit var animator: ObjectAnimator
    private fun startAnimation() {
        animator = ObjectAnimator.ofFloat(binding.image, "rotation", 0f, 359f)
        animator.apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        animator.start()
    }

    override fun dismiss() {
        super.dismiss()
        animator.cancel()
        interval?.cancel()
    }
}
