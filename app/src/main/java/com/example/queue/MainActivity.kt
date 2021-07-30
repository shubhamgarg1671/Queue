package com.example.queue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.example.queue.adapter.viewPagerAdapter
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")

        val viewPager = findViewById<ViewPager>(R.id.viewPager);
        val viewPagerAdapter = viewPagerAdapter(
            supportFragmentManager
        );
        viewPager.setAdapter(viewPagerAdapter);
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        // It is used to join TabLayout with ViewPager.
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab select

                tab?.contentDescription = "shubham"
                Log.d(TAG, "onTabSelected() called with: tab = ${tab!!.text}")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
                Log.d(TAG, "onTabReselected() called with: tab = $tab")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
                Log.d(TAG, "onTabUnselected() called with: tab = $tab")
            }
        })
    }
}