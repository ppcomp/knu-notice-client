package com.ppcomp.knu.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ppcomp.knu.fragment.NoticeTabFragment

const val ARG_OBJECT = "object"

class NoticeTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var list = mutableListOf<String>()
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        Log.d("DEBUG", "tab position : $position")
        val fragment = NoticeTabFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}
