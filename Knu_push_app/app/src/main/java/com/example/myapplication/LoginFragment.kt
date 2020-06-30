package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * 하단 바 '로그인'페이지의  kt
 * @author 희진
 */
class LoginFragment : Fragment() {

    companion object {  // 맨 처음 뜨는 fragment로 지정
        fun newInstance(): LoginFragment {
            val fragmentHome = LoginFragment()
            val args = Bundle()
            fragmentHome.arguments = args
            return fragmentHome
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_login, container, false)
    }

}
