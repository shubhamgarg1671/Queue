package com.example.queue.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.queue.R
import com.example.queue.fragment.tab_1
import com.example.queue.fragment.tab_2

class viewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    val TAG = "viewPagerAdapter"
    override fun getCount(): Int {
        return 2;
    }

    override fun getItem(position: Int): Fragment {
        Log.d(TAG, "getItem() called with: position = $position")
        var fragment: Fragment? = null
        if (position == 0) fragment = tab_1()
        else if (position == 1) fragment = tab_2()

        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        Log.d(TAG, "getPageTitle() called with: position = $position")
        var title: String? = null
        if (position == 0)
            title = "Move in Queue"
        else if (position == 1)
            title  = "New Queue"
        return title
    }
}