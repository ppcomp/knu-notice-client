package com.ppcomp.knu

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import androidx.recyclerview.widget.LinearLayoutManager
import com.DataRunner.CountryTown.MakerAdapter
import kotlinx.android.synthetic.main.activity_maker.*

import org.json.JSONArray

class Maker : AppCompatActivity() {

    private var makerList = arrayListOf<MakerData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maker)

        getMakers()
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
            val listLine = MakerData(e.toString(), "오류", "오류", "오류", "오류")
            makerList.add(listLine)
        }
    }
    fun loadData(context: Context, key: String): String? {
        val sharedPreferences =
            context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val data = sharedPreferences.getString(key, "")
        return data
    }
}