package com.ppcomp.knu.adapter

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.noticeData.Alarm
import com.ppcomp.knu.`object`.noticeData.Notice

class AlarmViewHolder(parent: ViewGroup, private val onClick: (Alarm) -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.fragment_alarm_item, parent, false)
) {

    val alramView = itemView.findViewById<CardView>(R.id.alarm_view)
    val alarmTitle = itemView.findViewById<TextView>(R.id.title)
    val alarmDate = itemView.findViewById<TextView>(R.id.date)
    val alarmColorZone = itemView.findViewById<LinearLayout>(R.id.color_zone)

    @RequiresApi(Build.VERSION_CODES.N)
    fun bindTo(alarm: Alarm?) {
        val spannedTitle = Html.fromHtml(alarm?.title, Html.FROM_HTML_MODE_LEGACY)
        alarmTitle.text = spannedTitle
        alarmDate.text = alarm?.date
        if (alarm != null) {
            alarmColorZone.setBackgroundColor(alarm.color)
        }
        itemView.setOnClickListener {
            if (alarm != null) {
                onClick(alarm)
            }
        }
    }
}