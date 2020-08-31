package com.ppcomp.knu.activity

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.ppcomp.knu.R
import kotlinx.android.synthetic.main.weblayout.*
class WebViewActivity : AppCompatActivity() {

    lateinit var mWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weblayout)

        // 웹뷰 셋팅
        mWebView = webView //xml 자바코드 연결
        mWebView.getSettings().setJavaScriptEnabled(true) //자바스크립트 허용
        mWebView.loadUrl("http://www.naver.com") //웹뷰 실행
        mWebView.setWebChromeClient(WebChromeClient()) //웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        mWebView.setWebViewClient(WebViewClientClass()) //새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean { //뒤로가기 버튼 이벤트
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) { //웹뷰에서 뒤로가기 버튼을 누르면 뒤로가짐
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private class WebViewClientClass : WebViewClient() {
        //페이지 이동
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            Log.d("check URL", url)
            view.loadUrl(url)
            return true
        }
    }
}