package com.ppcomp.knu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Subscription
import com.ppcomp.knu.utils.HangulUtils
import kotlinx.android.synthetic.main.activity_subscription_item.view.*
import kotlin.collections.ArrayList

/**
 * item의 어느요소를 어느 View에 넣을 것인지 연결해주는 Adapter
 * @author 상은
 */
class SubscriptionAdapter(
    val context: Context,
    var subsList: ArrayList<Subscription>,
    private val selectedItemCount: TextView,
    private val myToast: Toast,
    private var checkList: ArrayList<Subscription>?,
    private var checkListAdapter: SubscriptionCheckAdapter?
) :
    RecyclerView.Adapter<SubscriptionAdapter.Holder>(), Filterable {

    var subsFilterList = ArrayList<Subscription>()

    init {
        subsFilterList = subsList
    }

    override fun getFilter(): Filter { // 검색 기능
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString().toLowerCase()
                subsFilterList = if (charSearch.isEmpty()) {
                    subsList
                } else {
                    val resultList = ArrayList<Subscription>()
                    for (row in subsList) {
                        val iniName = HangulUtils.getHangulInitialSound(row.name, charSearch);
                        if (iniName.indexOf(charSearch) >= 0) { // 초성검색어가 있으면 해당 데이터 리스트에 추가
                            resultList.add(row)
                        } else if (row.name.toLowerCase().contains(charSearch)) {
                            resultList.add(row)
                        }
                    }
                    resultList
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
        val view =
            LayoutInflater.from(context).inflate(R.layout.activity_subscription_item, parent, false)
        selectedItemCount.text = getSelectedItemCount().toString() + "/10"
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return subsFilterList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {


        holder.bind(subsFilterList[position], context)

        holder.chk?.setOnCheckedChangeListener(null)
        // 체크박스 부분

        holder.chk?.isChecked = subsFilterList[position].checked

        holder.chk?.setOnCheckedChangeListener { // 체크 표시할 때
                _, isChecked ->
            val getCheckedCount = getSelectedItemCount()
            val getName: String = holder.itemView.subs_name.text as String
            if (getCheckedCount >= 10 && isChecked) { // 체크가 10개를 초과한 경우에서 체크를 할 때
                myToast.setText("구독리스트가 10개를 초과했습니다")
                myToast.show()
                holder.chk?.isChecked = false
            } else {
                for (i in subsList) {
                    if (i.name == getName) {
                        i.checked = isChecked
                            if (isChecked && checkList?.contains(i) == false) {
                                checkList?.add(i) // 체크한경우 체크리스트에 체크값 추가
                            } else if(!isChecked) {
                                checkList?.remove(i) // 체크해제한 경우 값 제거
                            }
                        checkListAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                selectedItemCount.text = getSelectedItemCount().toString() + "/10"
            }

        holder.name?.setOnClickListener { // 학과 이름 누르면 체크되도록
            holder.chk?.isChecked = !holder.chk?.isChecked!!
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

    fun setCheckAll(boolean: Boolean) {
        for (ckbox in subsList) {
            if (ckbox.checked == !boolean)
                ckbox.checked = boolean

        }
        notifyDataSetChanged()
    }

    private fun getSelectedItemCount(): Int {
        return if (checkList != null) {
            var count = 0
            for (ckbox in subsList) {
                if (ckbox.checked)
                    count++
            }
            count
        }else{
            0
        }
    }

    fun setCheckList(getCheckList : ArrayList<Subscription>){
        checkList = getCheckList
    }

    fun setCheckListAdapter(getCheckListAdapter : SubscriptionCheckAdapter){
        checkListAdapter = getCheckListAdapter
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}



