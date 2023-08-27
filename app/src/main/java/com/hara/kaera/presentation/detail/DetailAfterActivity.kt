package com.hara.kaera.presentation.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hara.kaera.R
import com.hara.kaera.databinding.ActivityDetailAfterBinding
import com.hara.kaera.domain.entity.WorryDetailEntity
import com.hara.kaera.presentation.base.BindingActivity
import com.hara.kaera.presentation.util.UiState
import com.hara.kaera.presentation.util.makeToast
import com.hara.kaera.presentation.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailAfterActivity :
    BindingActivity<ActivityDetailAfterBinding>(R.layout.activity_detail_after) {
    private val viewModel by viewModels<DetailAfterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWorryById()
        collectFlows()
    }

    private fun getWorryById() {
        val worryId = intent.getIntExtra("worryId", 0)
        viewModel.getWorryDetail(worryId)
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailStateFlow.collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState<WorryDetailEntity>) {
        when (uiState) {
            is UiState.Init -> Unit
            is UiState.Loading -> Unit
            is UiState.Success<WorryDetailEntity> -> {
                val worryDetail = uiState.data
                binding.worryDetail = worryDetail
                with(binding) {
                    layoutAnswer1.tvTitle.text = worryDetail.subtitles[0]
                    layoutAnswer1.tvContent.text = worryDetail.answers[0]
                }

                if (worryDetail.templateId == 1) { // freeflow
                    with(binding) {
                        layoutAnswer2.root.visible(false)
                        layoutAnswer3.root.visible(false)
                        layoutAnswer4.root.visible(false)
                    }
                } else {
                    val layouts =
                        listOf(binding.layoutAnswer2, binding.layoutAnswer3, binding.layoutAnswer4)
                    layouts.forEachIndexed { index, layout ->
                        if (index < worryDetail.subtitles.size && index < worryDetail.answers.size) {
                            layout.tvTitle.text = worryDetail.subtitles[index]
                            layout.tvContent.text = worryDetail.answers[index]
                        }
                    }
                }
            }

            is UiState.Error -> {
                binding.root.makeToast(uiState.error)
            }
        }
    }
}
