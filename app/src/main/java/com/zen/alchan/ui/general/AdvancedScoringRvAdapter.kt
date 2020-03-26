package com.zen.alchan.ui.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.AdvancedScoresItem
import com.zen.alchan.helper.removeTrailingZero
import kotlinx.android.synthetic.main.list_advanced_scoring_input.view.*

class AdvancedScoringRvAdapter(private val list: List<AdvancedScoresItem>,
                               private val listener: AdvancedScoringListener
) : RecyclerView.Adapter<AdvancedScoringRvAdapter.ViewHolder>() {

    interface AdvancedScoringListener {
        fun passScores(index: Int, newScore: Double)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_advanced_scoring_input, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.criteriaLabel.text = item.criteria
        holder.criteriaScoreField.setText(item.score.removeTrailingZero())
        holder.criteriaScoreField.addTextChangedListener {
            try {
                if (it.isNullOrBlank()) {
                    listener.passScores(position, 0.0)
                } else {
                    var newScore = it.toString().toDouble()
                    listener.passScores(position, newScore)
                }
            } catch (e: Exception) {
                // do nothing
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val criteriaLabel = view.criteriaLabel!!
        val criteriaScoreField = view.criteriaScoreField!!
    }
}