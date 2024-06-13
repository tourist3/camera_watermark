package com.mobileheros.camera.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import com.mobileheros.camera.R
import com.mobileheros.camera.databinding.DialogRateBinding
import com.mobileheros.camera.utils.CommonUtils

class RateDialog(mContext: Context) :
    Dialog(mContext) {
    private var ctx = mContext
    private lateinit var binding: DialogRateBinding
    private var star = 5f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogRateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.btnOk.setOnClickListener {
            if (star == 5f) {
                CommonUtils.openGooglePlay(ctx, ctx.packageName)
            } else {
                Toast.makeText(ctx, ctx.getString(R.string.thanks_for_rating), Toast.LENGTH_LONG).show()
            }
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener{_, rating, _ -> star = rating}


    }
}
