package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class SubscriptionAdapter(val context: Context, val subsList: ArrayList<Subscription>) :
    RecyclerView.Adapter<SubscriptionAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.subscription_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return subsList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder?.bind(subsList[position], context)

        // 체크박스 부분
        holder.chk!!.setOnCheckedChangeListener(null)
        holder.chk!!.setChecked(subsList.get(position).checked)
        holder.chk!!.setOnCheckedChangeListener { // 체크 표시할 때
                buttonView, isChecked ->
            subsList.get(holder.adapterPosition).checked = isChecked 
            // 체크 상태 저장
        }

    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val name = itemView?.findViewById<TextView>(R.id.subs_name)
        val chk = itemView?.findViewById<CheckBox>(R.id.subs_checkbox)

        fun bind(subscription: Subscription, context: Context) {
            name?.text = subscription.name
        }
    }

    fun getName(position: Int): String {
        return subsList[position].name
    }

    fun getChecked(position: Int): Boolean {
        return subsList[position].checked
    }

}




