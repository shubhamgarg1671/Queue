package com.example.queue.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.queue.R
import com.example.queue.fragment.tab_1
import com.example.queue.fragment.tab_2

class viewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val TAG = "viewPagerAdapter"
    override fun getCount(): Int {
        return 2;
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) fragment = tab_1()
        else if (position == 1) fragment = tab_2()

        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0)
            title = "Move in Queue"
        else if (position == 1)
            title  = "New Queue"
        return title
    }
}