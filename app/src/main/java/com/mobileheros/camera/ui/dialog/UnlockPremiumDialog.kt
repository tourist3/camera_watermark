package com.mobileheros.camera.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.mobileheros.camera.R
import com.mobileheros.camera.databinding.DialogUnlockPremiumBinding

class UnlockPremiumDialog :
    DialogFragment() {
    private lateinit var binding: DialogUnlockPremiumBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogUnlockPremiumBinding.inflate(layoutInflater)
        binding.confirm.setOnClickListener {
//            Navigation.findNavController(it).navigate(R.id.action_dialog_to_subscribe)
            findNavController().navigate(R.id.action_dialog_to_subscribe, Bundle().apply {
                putBoolean("isSingle", true)
            })
//            dismiss()
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}
