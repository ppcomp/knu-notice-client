package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.view.Menu
import kotlinx.android.synthetic.main.main_layout.*
//import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.main_toolbar.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        main_navigationView.setNavigationItemSelectedListener (this)
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        val fab: ImageButton = findViewById(R.id.menubutton)    //버튼 변수에 넣기
        fab.setOnClickListener {   //클릭시
            main_drawer_layout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
        }

        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {   //메뉴 클릭시 이동(아직 안됨)
        val id = item.itemId
        if(id == R.id.sub_list){
            val intent = Intent(this,sub_list::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {  //뒤로가기 버튼.
        if(main_drawer_layout.isDrawerOpen(GravityCompat.START)){
            main_drawer_layout.closeDrawers()
        }else{
            super.onBackPressed()
        }
    }
    }


