package com.ppcomp.knu.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.DataRunner.CountryTown.MakerAdapter
import com.ppcomp.knu.`object`.MakerData
import com.ppcomp.knu.R
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.activity_maker.*
import org.json.JSONArray

class MakerActivity : AppCompatActivity() {

    private lateinit var searchIcon: ImageView
    private lateinit var trashcan: ImageView
    private var makerList = arrayListOf<MakerData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maker)

        getMakers()

        searchIcon = findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = GONE

        trashcan = findViewById<ImageView>(R.id.trash_icon)
        trashcan.visibility = GONE

        val title = findViewById<TextView>(R.id.state_title)
        title.text = "만든이"

        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic)//뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

    }

    private fun getMakers() {
        val makerAdapter =  MakerAdapter(this, makerList) { maker ->
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf(maker.makerEmail))
                putExtra(Intent.EXTRA_SUBJECT, "문의드립니다.")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        maker.adapter = makerAdapter

        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(this)
        maker.layoutManager = lm
        maker.setHasFixedSize(true)

        //start
        StrictMode.enableDefaults()
        try {
//            val jsonString = loadData(this, "maker")
            val assetManager = resources.assets
            val inputStream = assetManager.open("maker.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jArray = JSONArray(jsonString)

            // 모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {

                val obj = jArray.getJSONObject(i)

                val listLine = MakerData(
                    obj.getString("이름"),
                    obj.getString("이메일"),
                    obj.getString("깃주소"),
                    obj.getString("소속"),
                    obj.getString("사진")
                )
                makerList.add(listLine)
            }
        } catch (e: Exception) {
            val listLine =
                MakerData(
                    e.toString(),
                    "오류",
                    "오류",
                    "오류",
                    "오류"
                )
            makerList.add(listLine)
        }
    }
}