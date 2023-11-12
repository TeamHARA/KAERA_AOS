package com.hara.kaera.feature.write.custom

import android.os.Bundle
import android.view.View
import com.hara.kaera.R
import com.hara.kaera.databinding.DialogFragmentDeadlineBinding
import com.hara.kaera.feature.base.BindingDialogFragment
import com.hara.kaera.feature.util.onSingleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogWriteComplete(
    private val onBtnCompleteListener: (day: Int) -> Unit
) : BindingDialogFragment<DialogFragmentDeadlineBinding>(R.layout.dialog_fragment_deadline,16)
{
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setNumberPicker()
    }

    private fun setOnClickListener() {
        binding.btnComplete.onSingleClick(1000) {
            onBtnCompleteListener(binding.pickerDeadlines.value)
            dismiss()
        }
        binding.btnNoDeadline.onSingleClick(1000) {
            onBtnCompleteListener(-1)
            dismiss()
        }
        //TODO POST 서버통신
    }

    private fun setNumberPicker() {
        binding.pickerDeadlines.apply {
            minValue = 1
            maxValue = 30
        }
    }

}