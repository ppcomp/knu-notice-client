package com.ppcomp.knu.activity

import RestApiService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.`object`.Subscription
import com.ppcomp.knu.`object`.UserInfo
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


/**
 * intro 기능, intro후 mainActivity 실행
 * @author 김상은
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()

        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val ed = pref.edit()
        val getId = pref.getString("UID", "")
        val checkFirstUser = pref.getString("First?", "Yes")
        var subsList = arrayListOf<Subscription>()
        val serverUrl = "http://15.165.178.103/notice/list"
        val subscriptionList = pref.getString("Subs", "")?.split("+")
        val set: MutableSet<String> = mutableSetOf("")
        if (subscriptionList != null) {
            for (i in 0 until subscriptionList.count()) {
                set.add(subscriptionList[i])
            }
        }
        // 구독리스트들을 "+" 기준으로 나눠서 set에 저장

        try {
            val stream = URL(serverUrl).openConnection() as HttpURLConnection
            var read = BufferedReader(InputStreamReader(stream.inputStream, "UTF-8"))
            val response = read.readLine()
            val jArray = JSONArray(response)

            for (i in 0 until jArray.length()) {
                val obj = jArray.getJSONObject(i)
                val name = obj.getString("name")
                val getUrl = obj.getString("api_url")
                val url = getUrl.split("/")
                val confirmCheck: Boolean = set.contains(name)// 저장된 게시판인지 확인하기위한 변수
                val line =
                    Subscription(
                        name,
                        confirmCheck,
                        url[2]
                    )
                subsList.add(line)
            }
        } catch (e: Exception) {
            val line = Subscription("오류", false, "")
        }

        subsList.sortWith(Comparator { data1, data2 -> data1.name.compareTo(data2.name)})
        subsList.sortBy { data -> data.name }

        val userLocalData = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = userLocalData!!.edit()
        val makeGson = GsonBuilder().create()
        var listType : TypeToken<ArrayList<Subscription>> = object : TypeToken<ArrayList<Subscription>>() {}
        var strContact = makeGson.toJson(subsList, listType.type)
        editor.putString("testsub", strContact)
        editor.commit()

        if (getId.equals("")) { // ID가 등록이 안되어있을경우

            val toast = Toast.makeText(this, "신규 사용자입니다. \n구독리스트 설정화면으로 이동합니다.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER,0,0)
            toast.show()
            var uniqueID = UUID.randomUUID().toString()
            val userInfo = UserInfo(
                id = uniqueID,
                id_method = "guid",
                keywords = null,
                subscriptions = null
            )
            ed.putString("UID", uniqueID)
            ed.apply()

            val apiService = RestApiService()
            apiService.addUser(userInfo) {
                if (it?.id != null) {
                    // it = newly added user parsed as response
                    // it?.id = newly added user ID
                } else {
                    Toast.makeText(this, "ID 등록 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if(checkFirstUser.equals("Yes")) // 신규 사용자일시 구독리스트 설정, 아닐시 메인화면
        {
            var intent = Intent(this, SubscriptionActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



    }

}

