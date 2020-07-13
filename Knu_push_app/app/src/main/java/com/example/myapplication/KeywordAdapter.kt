package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

/**
 * item의 어느요소를 어느 View에 넣을 것인지 연결해주는 Adapter
 * @author 상은
 */
class KeywordAdapter(val context: Context, val keywordList: ArrayList<Keyword>) :
    RecyclerView.Adapter<KeywordAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.keyword_item, parent, false)
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

            val loadPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
            val ed = loadPreferences.edit()
            val getKeyword = loadPreferences.getString("Keys", "")
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

            ed.putString("Keys", completeKeyword)
            ed.apply()
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



