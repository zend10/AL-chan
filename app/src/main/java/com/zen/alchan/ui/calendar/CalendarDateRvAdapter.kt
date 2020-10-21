package com.zen.alchan.ui.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.DateItem
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_calendar_date.view.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarDateRvAdapter(private val context: Context,
                            private val list: List<DateItem>,
                            private val listener: CalendarDateListener
) : RecyclerView.Adapter<CalendarDateRvAdapter.ViewHolder>() {

    interface CalendarDateListener {
        fun passSelectedDate(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_calendar_date, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val dayFormat = SimpleDateFormat("EEE", Locale.US)
        val dateFormat = SimpleDateFormat("dd", Locale.US)
        holder.dayText.text = dayFormat.format(item.dateTimestamp)
        holder.dateText.text = dateFormat.format(item.dateTimestamp)

        if (item.isSelected) {
            holder.dayText.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeSecondaryColor))
            holder.dateText.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeSecondaryColor))
        } else {
            holder.dayText.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
            holder.dateText.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        }

        holder.itemView.setOnClickListener {
            listener.passSelectedDate(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText = view.dayText!!
        val dateText = view.dateText!!
    }
}