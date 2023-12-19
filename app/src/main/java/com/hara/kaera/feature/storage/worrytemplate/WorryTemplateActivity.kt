package com.hara.kaera.feature.storage.worrytemplate

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hara.kaera.R
import com.hara.kaera.databinding.ActivityWorryTemplateBinding
import com.hara.kaera.domain.entity.TemplateTypesEntity
import com.hara.kaera.feature.base.BindingActivity
import com.hara.kaera.feature.util.UiState
import com.hara.kaera.feature.util.makeToast
import com.hara.kaera.feature.util.visible
import com.hara.kaera.feature.write.WriteActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class WorryTemplateActivity :
    BindingActivity<ActivityWorryTemplateBinding>(R.layout.activity_worry_template) {
    private lateinit var worryTemplateAdapter: WorryTemplateAdapter
    private val viewModel by viewModels<WorryTemplateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
        setOnClickListeners()
        collectFlows()
    }

    private fun setOnClickListeners() {
        binding.appbarDetail.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initAdapter() {
        worryTemplateAdapter = WorryTemplateAdapter { templateId ->
            startActivity(
                Intent(this, WriteActivity::class.java).apply {
                    putExtra("templateId", templateId)
                },
            )
        }
        binding.rcvWorryTemplate.adapter = worryTemplateAdapter
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.worryTemplateStateFlow.collect { uiState ->
                    render(uiState)
                }
            }
        }
    }

    private fun render(uiState: UiState<TemplateTypesEntity>) {
        when (uiState) {
            is UiState.Loading -> binding.layoutLoading.root.visible(true)


            is UiState.Success<TemplateTypesEntity> -> {
                binding.layoutLoading.root.visible(false)
                worryTemplateAdapter.submitList(uiState.data.templateTypeList)
            }

            is UiState.Error -> {
                binding.layoutLoading.root.visible(false)
                binding.root.makeToast(message = uiState.error)
            }

            else -> {
                Timber.e("else")
            }
        }
    }
}
