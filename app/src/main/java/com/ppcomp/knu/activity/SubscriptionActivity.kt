package com.ppcomp.knu.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.adapter.SubscriptionAdapter
import com.ppcomp.knu.`object`.Subscription
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.activity_subscription_toolbar.*


/**
 * 어떤 데이터(ArrayList)와 어떤 RecyclerView를 쓸 것인지 설정하는 Activity
 * @author 상은, 정준
 */
class SubscriptionActivity : AppCompatActivity() {
    var subsList = arrayListOf<Subscription>()
    lateinit var strContact : String
    lateinit var makeGson : Gson
    lateinit var listType : TypeToken<ArrayList<Subscription>>
    lateinit var subsAdapter : SubscriptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        StrictMode.enableDefaults()

        val pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val ed = pref.edit()
        val isNewUser = pref.getBoolean("NewUser", true) // 신규 사용자 확인
        val lm = LinearLayoutManager(this)

        //전역변수 초기화
        listType = object : TypeToken<ArrayList<Subscription>>() {}
        strContact = pref.getString("testsub", "").toString()
        makeGson = GsonBuilder().create()
        subsList = makeGson.fromJson(strContact, listType.type)
        subsAdapter = SubscriptionAdapter(this, subsList)
        subsResult.adapter = subsAdapter

        // RecyclerView의 사이즈를 고정
        subsResult.layoutManager = lm
        subsResult.setHasFixedSize(true)

        setSupportActionBar(subscription_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(!isNewUser) //toolbar 설정 (신규 유저가 아닐 때만 True)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic)//뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        subsSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                this@SubscriptionActivity.subsAdapter.getFilter().filter(newText)
                return false
            }
        })

        subsSave.setOnClickListener {   // 저장 버튼 누를시
            saveSubsciption()   //체크된 구독리스트 저장
            GlobalApplication.UserInfoUpload()  //구독리스트 서버에 업로드
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()

            if (isNewUser) { // 신규 사용자일시 확인버튼이 메인을 띄우도록
                ed.putBoolean("NewUser", false)
                ed.apply()
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        correct.setOnClickListener { // 저장 잘되어있는지 보려고 만든 View
            correct.setText(pref.getString("Urls", ""))
        }
    }

    /**
     * 화면에 체크된 구독리스트 저장
     * @author 상은, 정준
     */
    fun saveSubsciption() {

        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val ed = pref.edit()
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
                subsList[i].checked = true
            } else {
                subsList[i].checked = false
            }
        }
        if (storeName.equals("")) {
            ed.putString("Subs", "")
            ed.putString("Urls", "")

            strContact = makeGson.toJson(subsList, listType.type)
            ed.putString("testsub", strContact)
            ed.apply()
            // 아무것도 선택 안하고 저장버튼 누를 시 reset
        } else {
            storeName = storeName.substring(0, storeName.length - 1)
            storeUrl = storeUrl.substring(0, storeUrl.length - 1)

            ed.putString("Subs", storeName)
            ed.putString("Urls", storeUrl)

            strContact = makeGson.toJson(subsList, listType.type)
            ed.putString("testsub", strContact)
            ed.apply()
            // 선택하고 저장버튼 누를시 Subs 라는 Key로 SharedPreferences에 저장
        }
        GlobalApplication.isSubsChange = true //구독리스트 변경사항 확인
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