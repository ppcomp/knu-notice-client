package com.ppcomp.knu

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import androidx.recyclerview.widget.LinearLayoutManager
import com.DataRunner.CountryTown.MadebyAdapter
import kotlinx.android.synthetic.main.activity_madeby.*
import org.json.JSONArray

class Madeby : AppCompatActivity() {

    private var madebyList = arrayListOf<MadebyData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_madeby)

        getMakers()
    }

    private fun getMakers() {
        val madeByAdapter =  MadebyAdapter(this, madebyList) { maker ->
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf(maker.makerEmail))
                putExtra(Intent.EXTRA_SUBJECT, "문의드립니다.")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        madeBy.adapter = madeByAdapter

        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(this)
        madeBy.layoutManager = lm
        madeBy.setHasFixedSize(true)

        //start
        StrictMode.enableDefaults()
        try {
//            val jsonString = loadData(this, "maker")
            val assetManager = resources.assets
            val inputStream = assetManager.open("madeby.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jArray = JSONArray(jsonString)

            // 모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {

                val obj = jArray.getJSONObject(i)

                val listLine = MadebyData(
                    obj.getString("이름"),
                    obj.getString("이메일"),
                    obj.getString("깃주소"),
                    obj.getString("소속"),
                    obj.getString("사진")
                )
                madebyList.add(listLine)
            }
        } catch (e: Exception) {
            val listLine = MadebyData(e.toString(), "오류", "오류", "오류", "오류")
            madebyList.add(listLine)
        }
    }
    fun loadData(context: Context, key: String): String? {
        val sharedPreferences =
            context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val data = sharedPreferences.getString(key, "")
        return data
    }
}