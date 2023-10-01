package com.hara.kaera.feature.home.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hara.kaera.databinding.ItemHomeGemBinding
import com.hara.kaera.domain.entity.HomeWorryListEntity
import com.hara.kaera.feature.detail.DetailAfterActivity
import com.hara.kaera.feature.home.FloatingAnimation
import com.hara.kaera.feature.util.GlobalDiffCallBack
import timber.log.Timber

class HomeStoneAdapter(
    private val goToDetailBeforeActivity: (worryId: Int) -> Unit
) :
    ListAdapter<HomeWorryListEntity.HomeWorry, HomeStoneAdapter.HomeStoneViewHolder>(
        GlobalDiffCallBack()
    ) {

    private lateinit var inflater: LayoutInflater

    class HomeStoneViewHolder(val binding: ItemHomeGemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeStoneViewHolder {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
        return HomeStoneViewHolder(ItemHomeGemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: HomeStoneViewHolder, position: Int) {
        val curItem = getItem(position)
        with(holder.binding) {
            isSolved = false // 원석
            gemData = curItem

            val delay = (0..700).random()
            FloatingAnimation.floatingAnimationWithValueAnimator(
                delay,
                900,
                this.root,
                -30F
            ).start()

            // 고민 전 상세보기로 이동
            root.setOnClickListener { stone ->
                Timber.e("ABC " + curItem.templateId)
                goToDetailBeforeActivity(curItem.worryId)
            }
        }
    }
}