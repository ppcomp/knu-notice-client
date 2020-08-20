package com.ppcomp.knu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Keyword
import com.ppcomp.knu.utils.PreferenceHelper

/**
 * item의 어느요소를 어느 View에 넣을 것인지 연결해주는 Adapter
 * @author 상은
 */
class KeywordAdapter(val context: Context, val keywordList: ArrayList<Keyword>) :
    RecyclerView.Adapter<KeywordAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_keyword_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return keywordList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.bind(keywordList[position], context)

        holder.but?.setOnClickListener{ // 삭제 버튼 클릭시
            val deleteKeyword = keywordList[position].name
            keywordList.removeAt(position)
            notifyDataSetChanged()

            val getKeyword = PreferenceHelper.get("Keys", "")
            val getKeywordList = getKeyword?.split("+")
            var completeKeyword : String = ""
            for (i in 0 until getKeywordList!!.count()) {
                if(!getKeywordList[i].equals(deleteKeyword)) {
                    if(completeKeyword.equals("")) {
                        completeKeyword += getKeywordList[i]
                    }
                    else {
                        completeKeyword += "+" + getKeywordList[i]
                    }
                }
            }

            PreferenceHelper.put("Keys", completeKeyword)

            GlobalApplication.deviceInfoUpdate(context)
            GlobalApplication.isFragmentChange[1] = true //키워드 변경사항 확인
        }


    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val name = itemView?.findViewById<TextView>(R.id.key_name)
        val but = itemView?.findViewById<Button>(R.id.key_delete)

        fun bind(keyword: Keyword, context: Context) {
            name?.text = keyword.name
        }
    }

    fun getName(position: Int): String {
        return keywordList[position].name
    }
}



