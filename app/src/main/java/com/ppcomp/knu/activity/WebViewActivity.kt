package com.ppcomp.knu.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.`object`.noticeData.NoticeViewModel
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.activity_weblayout.*

/**
 * WebView Activity
 * 웹 주소를 가지고 앱안에서 웹페이지를 보여줌
 * @author 정우, 정준
 */
class WebViewActivity : AppCompatActivity() {

    private var menu: Menu? = null
    private lateinit var mWebView: WebView
    private lateinit var link: String
    private lateinit var title: String
    private var bookmark = false
    private lateinit var notice: Notice
    private lateinit var bookmarkViewModel: NoticeViewModel
    private var webViewLoadCount = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weblayout)
        val searchIcon = findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE
        link = intent.getStringExtra("link")!!
        title = intent.getStringExtra("title")!!
        bookmark = intent.getBooleanExtra("bookmark", false)
        notice = intent.getParcelableExtra("notice")!!
        bookmarkViewModel = ViewModelProvider(this).get(NoticeViewModel::class.java)

        // toolbar setting
        setSupportActionBar(main_layout_toolbar) //toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic) //뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        // 웹뷰 셋팅
        mWebView = webView //xml 자바코드 연결
        mWebView.settings.defaultTextEncodingName = "UTF-8"
        mWebView.settings.javaScriptEnabled = true //자바스크립트 허용
        mWebView.settings.builtInZoomControls = true //페이지 줌 기능 허용
        mWebView.settings.displayZoomControls = false //줌 컨트롤 박스 제거
        mWebView.webChromeClient = WebChromeClient() // To allow js alert
        mWebView.webViewClient = object : WebViewClient() { // To use webView instead of chrome
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webViewLoadCount += 1
                view.loadUrl(url)
                return true
            }
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                if(error.toString() == "SSLError")
                    handler.cancel()
                else
                    handler.proceed() // Ignore SSL certificate errors
            }
        }
        mWebView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            webViewLoadCount -= 1

            // chrome 에서 다운로드시
             val indent = Intent(Intent.ACTION_VIEW)
             indent.data = Uri.parse(url)
             startActivity(indent)

            // webview 에서 다운로드시
            // 파일명 깨짐
            // sw사업단에서 다운로드시 다운로드 지연 발생 + webview에서 다른 페이지 방문 안 되고 죽음
        }
        if (Build.VERSION.SDK_INT >= 19) {  // webview 가속 (속도 증가)
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.loadUrl(link) //웹뷰 실행
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.webview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                val extraText = "[$title]\n$link\n\n\n" +
                        "이 글은 착한선배로부터 공유됐어요 😃\n" +
                        "착한선배 - 강원대 공지 알림이\n" +
                        "다운로드하러 가기 ☞ https://play.google.com/store/apps/details?id=com.ppcomp.knu"
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, "공유하기"))
                true
            }
            android.R.id.home -> {  //툴바 뒤로가기
                finish()
                true
            }
            else -> true
        }
    }

    override fun onBackPressed() {
        if (mWebView.canGoBack() && webViewLoadCount > 1) {
            webViewLoadCount -= 1
            mWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}