package com.ppcomp.knu

    import RestApiService
    import android.content.Context
    import android.content.Intent
    import android.os.Bundle
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import java.util.*

/**
 * intro 기능, intro후 mainActivity 실행
 * @author 김상은
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val ed = pref.edit()
        var uniqueID = UUID.randomUUID().toString()
        val getId = pref.getString("UID", uniqueID)
        ed.putString("UID", getId)
        ed.apply()

        if(getId.equals(uniqueID)) {
            Toast.makeText(this, "ID 록 실패", Toast.LENGTH_SHORT).show()
            val userInfo = UserInfo(
                id = getId,
                id_method = "guid",
                keywords = null,
                subscriptions = null
            )

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




        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}