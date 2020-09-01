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
class SubscriptionCheckAdapter(
    val context: Context,
    var subsCheckList: ArrayList<Subscription>,
    private val selectedItemCount: TextView,
    private val subsList: ArrayList<Subscription>,
    private val subsAdapter: SubscriptionAdapter
) :
    RecyclerView.Adapter<SubscriptionCheckAdapter.Holder>() {

    var checkedList = ArrayList<Subscription>()

    init {
        checkedList = subsCheckList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.activity_subscription_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return checkedList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {


        holder.bind(checkedList[position], context)

        holder.chk?.setOnCheckedChangeListener(null)
        // 체크박스 부분

        holder.chk?.isChecked = checkedList[position].checked

        holder.chk?.setOnCheckedChangeListener { // 체크 표시할 때
                _, isChecked ->
            if (!isChecked) { // 체크를 해제한 경우에만
                val getName: String = holder.itemView.subs_name.text as String
                for (i in subsCheckList) { // 체크한 아이템의 이름으로 위치찾기
                    if (i.name == getName) {
                        subsCheckList.remove(i)
                        for (j in subsList) { // 전체 구독리스트를 나타내는 리사이클러뷰의 체크한 아이템 체크 해제
                            if (j.name == getName) {
                                j.checked = false
                                subsAdapter.notifyDataSetChanged()
                            }
                        }
                        notifyDataSetChanged()
                        break
                    }
                }
                selectedItemCount.text = checkedList.size.toString() + "/10"
            }

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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}



