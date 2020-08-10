package com.ppcomp.knu.activity

import RestApiService
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.adapter.KeywordAdapter
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.UserInfo
import com.ppcomp.knu.`object`.Keyword
import kotlinx.android.synthetic.main.activity_keyword.*
import kotlinx.android.synthetic.main.activity_keyword_toolbar.*

/**
 * 어떤 데이터(ArrayList)와 어떤 RecyclerView를 쓸 것인지 설정하는 Activity
 * @author 상은, 정준
 */

class KeywordActivity : AppCompatActivity() {
    var keywordList = arrayListOf<Keyword>()
    private lateinit var inputKeyword : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyword)

        StrictMode.enableDefaults()
        inputKeyword = findViewById(R.id.keywordInput) as TextView

        val loadPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val keyword = loadPreferences.getString("Keys", "")

        if (keyword != null) {
            if (!keyword.equals("")) {
                val getkeywordList = keyword.split("+")
                for (i in 0 until getkeywordList.count()) {
                    val keywordName = getkeywordList[i]
                    val line = Keyword(keywordName)
                    keywordList.add(line)
                }
            }
        }

        val keyAdapter = KeywordAdapter(this, keywordList)
        keyResult.adapter = keyAdapter

        val lm = LinearLayoutManager(this)
        keyResult.layoutManager = lm
        keyResult.setHasFixedSize(true)
        // RecyclerView의 사이즈를 고정

        setSupportActionBar(keyword_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic)//뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기


        keywordEnrollment.setOnClickListener {
            val getValue = keywordInput.text.toString()

            val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
            val ed = pref.edit()

            val getKeyword = pref.getString("Keys", "")
            val keyworditem = pref.getString("Keys", "오류")?.split("+")
            val keywordSize = keyworditem!!.count()
            val set: MutableSet<String> = mutableSetOf("")

            if(getValue == ""){
                Toast.makeText(this, "입력된 키워드가 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else if (getKeyword.equals("") && (getValue != "")) { // 키워드가 아무것도 없을경우
                ed.putString("Keys",getValue)
                ed.apply()
                keywordList.add(Keyword(getValue))
                keyAdapter.notifyDataSetChanged()
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()

                GlobalApplication.UserInfoUpload()  //서버에 업로드

                for (keyword in keyworditem) {
                    if (keyword.equals(getValue)) {
                        Toast.makeText(this, "이미 존재합니다.", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else{ // 키워드가 있을경우 -> 중복확인 필요
                val getKeywordList = pref.getString("Keys", "")?.split("+")
                if (getKeywordList != null) { // 중복확인하기 위해서 set에 모두 삽입
                    for (i in 0 until getKeywordList.count()) {
                        set.add(getKeywordList[i])
                    }
                }
                val keyOverlap: Boolean = set.contains(getValue)// 저장된 게시판인지 확인하기위한 변수
                if (!keyOverlap) {
                    val storeKeyword = getKeyword + "+" + getValue
                    ed.putString("Keys", storeKeyword)
                    ed.apply()
                    keywordList.add(Keyword(getValue))
                    keyAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()

                    GlobalApplication.UserInfoUpload()  //서버에 업로드

                    for (keyword in keyworditem) {
                        if (keyword.equals(getValue)) {
                            Toast.makeText(this, "이미 존재합니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                else {
                    for (keyword in keyworditem) {
                        if (keyword.equals(getValue)) {
                            Toast.makeText(this, "이미 존재합니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            inputKeyword.setText("")        //텍스트 초기화
            testview.setOnClickListener { // 저장 잘되어있는지 보려고 만든 View
                val pref2 = getSharedPreferences("pref", Context.MODE_PRIVATE)
                testview.setText(pref2.getString("Keys", ""))
            }
            GlobalApplication.iskeywordChange = true //키워드 변경사항 확인

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