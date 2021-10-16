package com.zen.alchan.ui.editor

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.zen.alchan.databinding.ListAdvancedScoringBinding
import com.zen.alchan.helper.extensions.roundToOneDecimal
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import type.ScoreFormat

class AdvancedScoringRvAdapter(
    list: List<Pair<String, Double>>,
    private val scoreFormat: ScoreFormat,
    private val listener: AdvancedScoringListener
) : BaseRecyclerViewAdapter<Pair<String, Double>, ListAdvancedScoringBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListAdvancedScoringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListAdvancedScoringBinding) : ViewHolder(binding) {
        override fun bind(item: Pair<String, Double>, index: Int) {
            binding.advancedScoringName.text = item.first
            binding.advancedScoringValue.setText(if (item.second == 0.0) "" else item.second.roundToOneDecimal())
            binding.advancedScoringValue.addTextChangedListener {
                var newScore = it.toString().toDoubleOrNull() ?: 0.0
                newScore = if (scoreFormat == ScoreFormat.POINT_100 && newScore > 100)
                    100.0
                else if (scoreFormat == ScoreFormat.POINT_10_DECIMAL && newScore > 10)
                    10.0
                else
                    newScore

                listener.getNewAdvancedScore(Pair(item.first, newScore))
            }
        }
    }

    interface AdvancedScoringListener {
        fun getNewAdvancedScore(newScore: Pair<String, Double>)
    }
}