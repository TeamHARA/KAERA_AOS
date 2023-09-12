package com.hara.kaera.presentation.home.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hara.kaera.databinding.ItemHomeGemBinding
import com.hara.kaera.domain.entity.HomeWorryListEntity
import com.hara.kaera.presentation.home.FloatingAnimation
import com.hara.kaera.presentation.util.GlobalDiffCallBack

class HomeStoneAdapter() :
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
        }
    }
}