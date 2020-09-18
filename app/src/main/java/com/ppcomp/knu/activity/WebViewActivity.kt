package com.ppcomp.knu.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ppcomp.knu.R
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.weblayout.*
import java.net.URLDecoder


class WebViewActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weblayout)
        val searchIcon = findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE
        val link = intent.getStringExtra("link")

        // toolbar setting
        setSupportActionBar(main_layout_toolbar) //toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic) //뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        // 웹뷰 셋팅
        mWebView = webView //xml 자바코드 연결
        mWebView.settings.defaultTextEncodingName = "UTF-8"
        mWebView.settings.javaScriptEnabled = true //자바스크립트 허용
        mWebView.loadUrl(link) //웹뷰 실행
        mWebView.webChromeClient = WebChromeClient() //웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        mWebView.webViewClient = WebViewClientClass() //새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용

        mWebView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            // chrome 에서 다운로드, but 파일명 안 깨짐
            // val indent = Intent(Intent.ACTION_VIEW)
            // indent.data = Uri.parse(url)
            // startActivity(indent)

            // webview 에서 다운로드, but 파일명 깨짐
            try {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setMimeType(mimeType)
                request.addRequestHeader("User-Agent", userAgent)
                request.setDescription("Downloading file")
                val decodedContentDisposition = URLDecoder.decode(contentDisposition, "UTF-8")
                val fileName = URLUtil.guessFileName(url, decodedContentDisposition, mimeType)
                request.setTitle(fileName)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(applicationContext, "Downloading File", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        Toast.makeText(baseContext, "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG)
                            .show()
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            110
                        )
                    } else {
                        Toast.makeText(baseContext, "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG)
                            .show()
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            110
                        )
                    }
                }
            }
        }
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