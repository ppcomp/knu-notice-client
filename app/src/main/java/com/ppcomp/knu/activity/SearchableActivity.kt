package com.ppcomp.knu.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.ppcomp.knu.R
import com.ppcomp.knu.fragment.NoticeFragment
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_searchable.*


class SearchableActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val noticeFragment = NoticeFragment()
    private var searchQuery: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)

        // 뒤로가기 왜 안 되는지?
        setSupportActionBar(search_toolbar) //toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic) //뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        // SearchView 에서 filtering 적용할 방법 찾지 못함
//        (search_view as EditText).filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
//            val ps: Pattern =
//                Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$")
//            if (source == "" || ps.matcher(source).matches()) {
//                return@InputFilter source
//            }
//            Toast.makeText(this, "한글, 영문, 숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
//            ""
//        })
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                if (!noticeFragment.isAdded) {
                    fragmentTransaction.add(R.id.frameLayout, noticeFragment)
                    fragmentTransaction.commit()
                }
                noticeFragment.searchRun(query!!, getTarget())
                currentFocus?.clearFocus()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        search_view.isIconified = false     // 검색창에 자동 focus
        searchview_spinner.onItemSelectedListener = this
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (searchQuery != null && searchQuery != "") {
            noticeFragment.searchRun(searchQuery!!, getTarget())
            currentFocus?.clearFocus()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    /**
     * 화면 터치시 키보드 숨김
     * @author 희진
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus?.clearFocus()
        }

        return super.dispatchTouchEvent(ev)
    }

    /**
     * Spinner 에 선택된 대상에 따라 검색할 대상 사이트 선정
     * @author 정우
     */
    private fun getTarget(): String {
        val option = searchview_spinner.selectedItem.toString()
        return if (option == "전체") {
            "all"
        } else {
            PreferenceHelper.get("Urls", "")!!
        }
    }
}