package com.ppcomp.knu.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ppcomp.knu.fragment.NoticeTabFragment
import com.ppcomp.knu.utils.PreferenceHelper

class NoticeTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var allTargets = PreferenceHelper.get("Urls", "")!!
    private var targets = allTargets.split("+")

    override fun getItemCount(): Int = targets.size + 1

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        Log.d("DEBUG", "tab position : $position")
        val fragment = NoticeTabFragment()
        fragment.arguments = Bundle().apply {
            if (position == 0) {
                putString("target", allTargets)
            } else {
                putString("target", targets[position - 1])
            }
        }
        return fragment
    }
}
