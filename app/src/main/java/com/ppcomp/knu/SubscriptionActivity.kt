package com.ppcomp.knu

import RestApiService
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.subscription_toolbar.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * 어떤 데이터(ArrayList)와 어떤 RecyclerView를 쓸 것인지 설정하는 Activity
 * @author 상은
 */
class SubscriptionActivity : AppCompatActivity() {
    var subsList = arrayListOf<Subscription>()
    lateinit var adapter: SubscriptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        //parsing 부분


        StrictMode.enableDefaults()
        val serverUrl = "http://15.165.178.103/notice/list"
        try {
            val stream = URL(serverUrl).openConnection() as HttpURLConnection
            var read = BufferedReader(InputStreamReader(stream.inputStream, "UTF-8"))
            val response = read.readLine()
            val jArray = JSONArray(response)

            val loadPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
            // 저장된 구독리스트 불러옴
            val subscriptionList = loadPreferences.getString("Subs", "")?.split("+")
            val set: MutableSet<String> = mutableSetOf("")
            if (subscriptionList != null) {
                for (i in 0 until subscriptionList.count()) {
                    set.add(subscriptionList[i])
                }
            }
            // 구독리스트들을 "+" 기준으로 나눠서 set에 저장

            for (i in 0 until jArray.length()) {
                val obj = jArray.getJSONObject(i)
                val name = obj.getString("name")
                val getUrl = obj.getString("api_url")
                val url = getUrl.split("/")
                val confirmCheck: Boolean = set.contains(name)// 저장된 게시판인지 확인하기위한 변수
                val line = Subscription(name, confirmCheck, url[2], i)
                subsList.add(line)
            }
        } catch (e: Exception) {
            val line = Subscription("오류", false, "", 9999)
        }

        val subsAdapter = SubscriptionAdapter(this, subsList)
        subsResult.adapter = subsAdapter
        adapter = subsAdapter

        val lm = LinearLayoutManager(this)
        subsResult.layoutManager = lm
        subsResult.setHasFixedSize(true)
        // RecyclerView의 사이즈를 고정

        setSupportActionBar(subscription_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back)//뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        subsSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)
                return false
            }

        })


        subsSave.setOnClickListener { // 저장 버튼 누를시
            val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
            val ed = pref.edit()
            // SharedPreferences 호출
            var storeName: String = ""
            var storeUrl: String = ""
            for (i in 0 until subsList.count()) {
                var ch: Boolean = subsAdapter.getChecked(i)
                var name: String
                var url: String
                if (ch == true) {
                    name = subsAdapter.getName(i)
                    url = subsAdapter.getUrl(i)
                    storeName = storeName + name + "+"
                    storeUrl = storeUrl + url + "+"
                }
            }
            if (storeName.equals("")) {
                ed.putString("Subs", "")
                ed.putString("Urls", "")
                ed.apply()
                // 아무것도 선택 안하고 저장버튼 누를 시 rest
            } else {
                storeName = storeName.substring(0, storeName.length - 1)
                storeUrl = storeUrl.substring(0, storeUrl.length - 1)

                ed.putString("Subs", storeName)
                ed.putString("Urls", storeUrl)
                ed.apply()
                // 선택하고 저장버튼 누를시 Subs 라는 Key로 SharedPreferences에 저장
            }

            val apiService = RestApiService()
            var getUID = pref.getString("UID", "")
            var getKeywords: String? = pref.getString("Keys", "")
            var getSubscriptions: String? = pref.getString("Urls", "")

            val userInfo = UserInfo(
                id = getUID,
                id_method = "guid",
                keywords = getKeywords,
                subscriptions = if (getSubscriptions == "") null else getSubscriptions
            )

            apiService.modifyUser(userInfo) {
                if (it?.id != null) {
                    // it = newly added user parsed as response
                    // it?.id = newly added user ID
                } else {

                }
            }
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            // 메세지

        }

        correct.setOnClickListener { // 저장 잘되어있는지 보려고 만든 View
            val pref2 = getSharedPreferences("pref", Context.MODE_PRIVATE)
            correct.setText(pref2.getString("Urls", ""))
        }
    }
    /**
     * 화면 터치시 키보드 숨김
     * @author 희진
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        return super.dispatchTouchEvent(ev)
    }
}