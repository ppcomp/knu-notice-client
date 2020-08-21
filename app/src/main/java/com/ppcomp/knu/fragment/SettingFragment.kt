package com.ppcomp.knu.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.activity.*
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_setting.view.*

/**
 * 하단 바 '세팅'페이지의  kt
 * @author 희진, 정준, 정우
 */
class SettingFragment : Fragment() {

    private lateinit var searchIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        view.alarmSwitch.isChecked = PreferenceHelper.get("alarmSwitch", false)
        view.alarmSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            PreferenceHelper.put("alarmSwitch", isChecked)
            GlobalApplication.UserInfoUpload()
        }
        view.subscriptionSetting.setOnClickListener {
            val intent = Intent(context, SubscriptionActivity::class.java)
            startActivity(intent)
        }
        view.keywordSetting.setOnClickListener {
            val intent = Intent(context, KeywordActivity::class.java)
            startActivity(intent)
        }
        view.login.setOnClickListener {
            if(GlobalApplication.isLogin) {
                val intent = Intent(context, UserInfoActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        view.license.setOnClickListener {
            val intent = Intent(context, LicenseActivity::class.java)
            startActivity(intent)
        }
        view.maker.setOnClickListener {
            val intent = Intent(context, MakerActivity::class.java)
            startActivity(intent)
        }
        view.inquire.setOnClickListener{
            val email = Intent(Intent.ACTION_SEND)
            email.setType("plain/text")
            val address = arrayOf("email@address.com")
            email.putExtra(Intent.EXTRA_EMAIL,address)
            email.putExtra(Intent.EXTRA_SUBJECT,"[앱 이름] 오류 | 요청할 사항이 있습니다!")
            startActivity(email)
        }

        searchIcon = view!!.findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE
        return view
    }
}