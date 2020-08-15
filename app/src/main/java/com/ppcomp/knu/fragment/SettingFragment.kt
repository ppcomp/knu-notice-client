package com.ppcomp.knu.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.activity.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

/**
 * 하단 바 '세팅'페이지의  kt
 * @author 희진, 정준
 */
class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

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
        view.login.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                if(GlobalApplication.isLogin) {
                    val intent = Intent(context, UserInfoActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        })
        view.license.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, LicenseActivity::class.java)
                startActivity(intent)
            }
        })
        view.maker.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, MakerActivity::class.java)
                startActivity(intent)
            }
        })
        return view
    }

}