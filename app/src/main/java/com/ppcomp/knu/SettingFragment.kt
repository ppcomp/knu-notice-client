package com.ppcomp.knu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_setting.view.*

/**
 * 하단 바 '세팅'페이지의  kt
 * @author 희진
 */
class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_setting, container, false)

        view.subscriptionSetting.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, SubscriptionActivity::class.java)
                startActivity(intent)
            }
        })
        view.keywordSetting.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, KeywordActivity::class.java)
                startActivity(intent)
            }
        })

        return view
    }

}