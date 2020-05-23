package com.example.myapplication

    import android.content.Intent
    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity

/**
 * intro 기능, intro후 mainActivity 실행
 * @author 김상은
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}