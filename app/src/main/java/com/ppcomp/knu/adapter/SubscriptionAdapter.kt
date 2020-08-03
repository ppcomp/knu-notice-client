package com.ppcomp.knu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Subscription
import kotlinx.android.synthetic.main.subscription_item.view.*
import kotlin.collections.ArrayList

/**
 * item의 어느요소를 어느 View에 넣을 것인지 연결해주는 Adapter
 * @author 상은
 */
class SubscriptionAdapter(val context: Context, var subsList: ArrayList<Subscription>) :
    RecyclerView.Adapter<SubscriptionAdapter.Holder>(), Filterable {

    var subsFilterList = ArrayList<Subscription>()

    init {
        subsFilterList = subsList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString().toLowerCase()
                if (charSearch.isEmpty()) {
                    subsFilterList = subsList
                } else {
                    val resultList = ArrayList<Subscription>()
                    for (row in subsList) {
                        if (row.name.toLowerCase().contains(charSearch)) {
                            resultList.add(row)
                        }
                    }
                    subsFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = subsFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                subsFilterList = results?.values as ArrayList<Subscription>
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.subscription_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return subsFilterList.size
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.bind(subsFilterList[position], context) // 확실하지않은 부분
//        holder.itemView.subs_name.text = subsFilterList[position].name
//        holder.itemView.subs_checkbox.isChecked = subsFilterList[position].checked


        holder.chk?.setOnCheckedChangeListener(null)
        // 체크박스 부분

        holder.chk?.setChecked(subsFilterList.get(position).checked)

        holder.chk?.setOnCheckedChangeListener { // 체크 표시할 때
                buttonView, isChecked ->
            val getName: String = holder.itemView.subs_name.text as String
            for (i in subsList) {
                if (i.name.equals(getName)) {
                    i.checked = isChecked
                }
            }

        }

    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val name = itemView?.findViewById<TextView>(R.id.subs_name)
        val chk = itemView?.findViewById<CheckBox>(R.id.subs_checkbox)

        fun bind(subscription: Subscription, context: Context) {

            name?.text = subscription.name
            chk?.isChecked = subscription.checked
        }
    }

    fun getName(position: Int): String {
        return subsList[position].name
    }

    fun getChecked(position: Int): Boolean {
        return subsList[position].checked
    }

    fun getUrl(position: Int): String {
        return subsList[position].url
    }


}



