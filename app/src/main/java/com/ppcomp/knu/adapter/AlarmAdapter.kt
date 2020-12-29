package com.ppcomp.knu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.noticeData.Alarm
import com.ppcomp.knu.fragment.AlarmFragment

/**
 * item의 어느요소를 어느 View에 넣을 것인지 연결해주는 Adapter
 * @author 상은
 */
class AlarmAdapter(
    val context: Context,
    var alarmList: ArrayList<Alarm>
) :
    RecyclerView.Adapter<AlarmAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.fragment_alarm_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlarmAdapter.Holder, position: Int) {
        holder.bind(alarmList[position], context)

    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val name = itemView?.findViewById<TextView>(R.id.alarm_title)
        val date = itemView?.findViewById<TextView>(R.id.alarm_date)

        fun bind(alarm: Alarm, context: Context) {
            name?.text = alarm.id
            date?.text = alarm.date.toString()
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
