package com.ppcomp.knu.activity

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.`object`.Subscription
import com.ppcomp.knu.utils.PreferenceHelper
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


/**
 * intro 기능, intro후 mainActivity 실행
 * @author 김상은, 정준
 */
class SplashActivity : AppCompatActivity() {

    // For a flexible update, use AppUpdateType.FLEXIBLE
    // But, if you want to force to update, use AppUpdateType.IMMEDIATE
    private val appUpdateType = AppUpdateType.FLEXIBLE
    private val MY_REQUEST_CODE = 100
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()

        inAppUpdate()

        // Init singleton Object
        val preference = PreferenceHelper.getInstance(this)

        val content = this
        loadServerInfo(object : Callback {
            override fun success(data: String?) {
                PreferenceHelper.put("serverIP", data)
                loadSubscription()  //서버에서 전체 구독리스트 다운로드

                //firebase instanceId를 저장하는 코드
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("tokenSave", "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new Instance ID token
                        val fbId = task.result?.token
                        PreferenceHelper.put("fbId", fbId)
                        GlobalApplication.deviceInfoUpload(content) //서버에 id 등록
                    })
                GlobalApplication.isLaunchApp = true
                val intent = Intent(content, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun fail(errorMessage: String?) {
                val toast = Toast.makeText(
                    content,
                    "$errorMessage | 파이어베이스 서버에 접속할 수 없습니다. 어플을 재시작 해주세요.",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                // 인 앱 업데이트가 이미 실행중이었다면 계속해서 진행하도록
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        MY_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("AppUpdate", "Update flow failed! Result code: $resultCode")
                Toast.makeText(
                    this,
                    "착한선배를 이용하기 위해서는 업데이트가 필요해요.",
                    Toast.LENGTH_SHORT
                ).show()
                finishAffinity() // 앱 종료
            }
        }
    }

    private fun inAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(appUpdateType) // or IMMEDIATE or FLEXIBLE
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        appUpdateType,
                        this,
                        MY_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    Log.e("AppUpdater", "AppUpdateManager Error", e)
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * firebase data 수신 함수
     * 수신 완료, 혹은 취소시 callback 함수 호출
     * @author 정우
     */
    private fun loadServerInfo(
        callback:Callback
    ) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef: DatabaseReference = database.getReference("url").child("server")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback.success(dataSnapshot.value.toString());
            }

            override fun onCancelled(error: DatabaseError) {
                callback.fail(error.message);
            }
        })
    }

    /**
     * firebase database callback 수신 인터페이스
     * @author 정우
     */
    interface Callback {
        fun success(data: String?)
        fun fail(errorMessage: String?)
    }

    /**
     * 전체 구독리스트 다운로드 후 정렬
     * @author 상은, 정준
     */
    private fun loadSubscription() {
        var subList = arrayListOf<Subscription>()
        val serverUrl = "http://${PreferenceHelper.get("serverIP", "")}/notice/list"
        val subscriptionList = PreferenceHelper.get("subNames", "")?.split("+") // 저장된 학과를 나눠 ArrayList에 저장 -- 체크박스를 위한 용도
        val set: MutableSet<String> = mutableSetOf("")

        if (subscriptionList != null) {
            for (i in 0 until subscriptionList.count()) { // 중복 제거
                set.add(subscriptionList[i])
            }
        }

        try { // 서버 연결하여 구독리스트 불러오고 confirmCheck 변수로 체크박스 호환
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
                subList.add(line)
            }
        } catch (e: Exception) {
            val line = Subscription("오류", false, "")
        }

        subList.sortWith(Comparator { data1, data2 -> data1.name.compareTo(data2.name) })
        subList.sortBy { data -> data.name } // 정렬

        // Arraylist를 SharedPreferences에 저장
        val makeGson = GsonBuilder().create()
        var listType: TypeToken<ArrayList<Subscription>> = object : TypeToken<ArrayList<Subscription>>() {}
        var strConcat = makeGson.toJson(subList, listType.type)
        PreferenceHelper.put("subList", strConcat)
    }
}

