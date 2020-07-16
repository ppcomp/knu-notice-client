package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import kotlinx.android.synthetic.main.fragment_user_info.*


/**
 * 회원정보를 보여주는 프래그먼트
 * @author 정준
 */
class UserInfoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    /**
     * 로그아웃 버튼 누를시 로그아웃 실행 후 LoginFragment로 전환
     * @author 정준
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_logout.setOnClickListener {
            Toast.makeText(activity, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                override fun onCompleteLogout() {
                    GlobalApplication.isLogin = false   //로그인 상태 업데이트
                    (activity as MainActivity).addFragment(LoginFragment())
                }
            })
        }
    }
}