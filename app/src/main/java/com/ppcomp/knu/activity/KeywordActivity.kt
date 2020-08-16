package com.ppcomp.knu.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.adapter.KeywordAdapter
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Keyword
import kotlinx.android.synthetic.main.activity_keyword.*
import kotlinx.android.synthetic.main.activity_keyword_toolbar.*

/**
 * 어떤 데이터(ArrayList)와 어떤 RecyclerView를 쓸 것인지 설정하는 Activity
 * @author 상은, 정준
 */

class KeywordActivity : AppCompatActivity() {
    var keywordList = arrayListOf<Keyword>()
    private lateinit var inputKeyword: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyword)

        StrictMode.enableDefaults()
        inputKeyword = findViewById(R.id.keywordInput) as TextView

        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val ed = pref.edit()

        val keyword = pref.getString("Keys", "")

        if (keyword != null) { // 첫 화면을 띄울 시 키워드들을 리사이클러뷰에 등록
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

        keywordInput.setOnKeyListener(object : View.OnKeyListener { // 엔터키누르면 등록버튼을 자동으로 누르도록
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event != null) {
                    if (event.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        keywordEnrollment.performClick()
                        return true
                    }
                }
                return false
            }
        })


        keywordEnrollment.setOnClickListener { // 버튼 클릭시 동작
            val getValue = keywordInput.text.toString()
            val getKeyword = pref.getString("Keys", "")
            val keyworditem = pref.getString("Keys", "오류")?.split("+")

            val set: MutableSet<String> = mutableSetOf("")

            if (getValue == "") { // 입력값이 없을 경우
                Toast.makeText(this, "입력된 키워드가 없습니다.", Toast.LENGTH_SHORT).show()
            } else if (getKeyword.equals("") && (getValue != "")) { // 입력값이 없고, 키워드 저장값이 없을 경우
                ed.putString("Keys", getValue)
                ed.apply()
                keywordList.add(Keyword(getValue))
                keyAdapter.notifyDataSetChanged()
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                GlobalApplication.UserInfoUpload()  //서버에 업로드
            } else { // 키워드가 있을경우 -> 중복확인 필요
                val getKeywordList = pref.getString("Keys", "")?.split("+")
                if (getKeywordList != null) { // 중복확인하기 위해서 set에 모두 삽입
                    for (i in 0 until getKeywordList.count()) {
                        set.add(getKeywordList[i])
                    }
                }
                val keyOverlap: Boolean = set.contains(getValue)// 저장된 게시판인지 확인하기위한 변수
                if (!keyOverlap) { // 중복이 아닌 키워드를 입력한 경우(올바른 경우)
                    val storeKeyword = getKeyword + "+" + getValue
                    ed.putString("Keys", storeKeyword)
                    ed.apply()
                    keywordList.add(Keyword(getValue))
                    keyAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                    GlobalApplication.UserInfoUpload()  //서버에 업로드
                } else { // 중복 키워드를 입력한 경우(문제 발생)
                    if (keyworditem != null) {
                        for (keyword in keyworditem) {
                            if (keyword.equals(getValue)) {
                                Toast.makeText(this, "이미 존재합니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            inputKeyword.setText("")   //텍스트 초기화


            GlobalApplication.iskeywordChange = true //키워드 변경사항 확인

        }

        testview.setOnClickListener { // 저장 잘되어있는지 보려고 만든 View
            testview.setText(pref.getString("Keys", ""))
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