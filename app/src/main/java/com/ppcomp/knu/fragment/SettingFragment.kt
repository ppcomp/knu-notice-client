package com.ppcomp.knu.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ppcomp.knu.BuildConfig
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

        view.alarmSwitchSub.isChecked = PreferenceHelper.get("alarmSwitchSub", false)
        view.alarmSwitchSub.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            PreferenceHelper.put("alarmSwitchSub", isChecked)
            GlobalApplication.deviceInfoUpdate(requireActivity())
        }
        view.alarmSwitchKey.isChecked = PreferenceHelper.get("alarmSwitchKey", false)
        view.alarmSwitchKey.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            PreferenceHelper.put("alarmSwitchKey", isChecked)
            GlobalApplication.deviceInfoUpdate(requireActivity())
        }
        view.user_info.setOnClickListener {
            if(GlobalApplication.isLogin) {
                val intent = Intent(context, UserInfoActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        view.subscriptionSetting.setOnClickListener {
            val intent = Intent(context, SubscriptionActivity::class.java)
            startActivity(intent)
        }
        view.keywordSetting.setOnClickListener {
            val intent = Intent(context, KeywordActivity::class.java)
            startActivity(intent)
        }
        view.inquire.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bit.ly/3pVtks0"))
            startActivity(intent)
        }
        view.maker.setOnClickListener {
            val intent = Intent(context, MakerActivity::class.java)
            startActivity(intent)
        }
        view.license.setOnClickListener {
            val intent = Intent(context, LicenseActivity::class.java)
            startActivity(intent)
        }
        view.version.text = "Version: v${BuildConfig.VERSION_NAME}"
        searchIcon = view!!.findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE
        return view
    }
}