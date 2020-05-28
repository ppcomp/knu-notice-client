package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class SubscriptionAdapter(val context: Context, val subsList: ArrayList<Subscription>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.subscription_item, null)

        /* 위에서 생성된 view를 res-layout-subscription_item.xml 파일의 각 View와 연결하는 과정이다. */
        val subsName = view.findViewById<TextView>(R.id.subs_name)
        val subsCheck = view.findViewById<CheckBox>(R.id.subs_checkbox)
       // val subsUrl = view.findViewById<TextView>(R.id.url)

        /* ArrayList<Subscription>의 변수를 TextView에 담는다. */
        val subscription = subsList[position]
        subsName.text = subscription.name
        subsCheck.isChecked = false
        //subsUrl.text = subscription.url
        return view
    }

    override fun getItem(position: Int): Any {
        return subsList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return subsList.size
    }

    fun getName(position: Int): String{
        return subsList[position].name
    }




}