package com.ppcomp.knu.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Subscription
import com.ppcomp.knu.adapter.SubscriptionAdapter
import com.ppcomp.knu.adapter.SubscriptionCheckAdapter
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.activity_subscription.*


/**
 * 어떤 데이터(ArrayList)와 어떤 RecyclerView를 쓸 것인지 설정하는 Activity
 * @author 상은, 정준
 */
class SubscriptionActivity : AppCompatActivity() {
    private var subList = arrayListOf<Subscription>()
    private var subsCheckList = arrayListOf<Subscription>()
    private var subsCheckListSize = 0
    private lateinit var strConcat: String
    private lateinit var makeGson: Gson
    private lateinit var listType: TypeToken<ArrayList<Subscription>>
    lateinit var subsAdapter: SubscriptionAdapter
    private lateinit var subsCheckAdapter: SubscriptionCheckAdapter
    private lateinit var searchIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        StrictMode.enableDefaults()

        val isNewUser = PreferenceHelper.get("NewUser", false) // 신규 사용자 확인

        val subsManager = LinearLayoutManager(this)
        val checkManager = LinearLayoutManager(this)

        var myToast: Toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        val itemCount = findViewById<TextView>(R.id.itemCount)

        //전역변수 초기화
        listType = object : TypeToken<ArrayList<Subscription>>() {}
        strConcat = PreferenceHelper.get("subList", "").toString()
        makeGson = GsonBuilder().create()
        subList = makeGson.fromJson(strConcat, listType.type)

        val subCodes = PreferenceHelper.get("subCodes","")
        val subCodeList: List<String>? = subCodes?.split("+")

        for (i in subList) {
            for(j in subCodeList!!) {
                if(i.code == j) {
                    i.checked = true
                }
            }

            if (i.checked) {
                subsCheckList.add(i)
            }
        }
        subsCheckListSize = subsCheckList.size

        subsAdapter = SubscriptionAdapter(this, subList, itemCount, myToast, null, null)
        subsCheckAdapter =
            SubscriptionCheckAdapter(this, subsCheckList, itemCount, subList, subsAdapter)

        subsAdapter.setCheckList(subsCheckList)
        subsAdapter.setCheckListAdapter(subsCheckAdapter)

        val offset = convertDpToPx(40)
        subsCheckAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                nested_scroll_view.scrollTo(
                    0,
                    maxOf(
                        0,
                        nested_scroll_view.scrollY + offset * (subsCheckList.size - subsCheckListSize)
                    )
                )
                subsCheckListSize = subsCheckList.size
            }
        })

        checkSubs.adapter = subsCheckAdapter
        subsResult.adapter = subsAdapter

        checkSubs.layoutManager = checkManager
        subsResult.layoutManager = subsManager

        val title = findViewById<TextView>(R.id.state_title)
        title.text = "구독리스트 설정"

        searchIcon = findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE

        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(!GlobalApplication.isNewUser) //toolbar 설정 (신규 유저가 아닐 때만 True)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic)//뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        subsSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                this@SubscriptionActivity.subsAdapter.filter.filter(newText)
                return false
            }
        })

        subsSave.setOnClickListener {   // 저장 버튼 누를시
            saveSubscription()   //체크된 구독리스트 저장
            GlobalApplication.deviceInfoUpdate(this)  //구독리스트 서버에 업로드
            GlobalApplication.userInfoUpload(this)  //유저정보 등록
            if (GlobalApplication.isNewUser) { // 신규 사용자일시 확인버튼이 메인화면을 띄우도록
                GlobalApplication.isNewUser = false
                PreferenceHelper.put("NewUser",false)   //신규사용자 false
                myToast.setText("환영합니다.")
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
                myToast.setText("구독리스트가 저장되었습니다")
            myToast.show()
        }

        if (PreferenceHelper.get("isAdmin", false)) {
            subs_allCheckbox.visibility = View.VISIBLE
        } else {
            subs_allCheckbox.visibility = View.GONE
        }
        subs_allCheckbox.setOnCheckedChangeListener { // 모든 체크박스 표시할 때
                _, isChecked ->
            subsAdapter.setCheckAll(isChecked)
        }
    }

    /**
     * 화면에 체크된 구독리스트 저장
     * @author 상은, 정준
     */
    private fun saveSubscription() {
        var storeName: String = ""
        var storeUrl: String = ""

        for (i in 0 until subList.count()) {
            var ch: Boolean = subsAdapter.getChecked(i)
            var name: String
            var code: String
            if (ch) {
                name = subsAdapter.getName(i)
                code = subsAdapter.getUrl(i)
                storeName = "$storeName$name+"
                storeUrl = "$storeUrl$code+"
                subList[i].checked = true
            } else {
                subList[i].checked = false
            }
        }
        if (storeName == "") {
            PreferenceHelper.put("subNames", "")
            PreferenceHelper.put("subCodes", "")

            strConcat = makeGson.toJson(subList, listType.type)
            PreferenceHelper.put("subList", strConcat)
            // 아무것도 선택 안하고 저장버튼 누를 시 reset
        } else {
            storeName = storeName.substring(0, storeName.length - 1)
            storeUrl = storeUrl.substring(0, storeUrl.length - 1)

            PreferenceHelper.put("subNames", storeName)
            PreferenceHelper.put("subCodes", storeUrl)

            strConcat = makeGson.toJson(subList, listType.type)
            PreferenceHelper.put("subList", strConcat)
            // 선택하고 저장버튼 누를시 subNames 라는 Key로 SharedPreferences에 저장
        }
        GlobalApplication.isFragmentChange[0] = true    // 구독리스트 변경사항 확인
        GlobalApplication.isFragmentChange[1] = true    // 키워드 변경사항 확인
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

    private fun convertDpToPx(dp: Int): Int {
        val dm: DisplayMetrics = this.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), dm).toInt()
    }
}