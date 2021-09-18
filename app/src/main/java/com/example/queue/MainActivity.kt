package com.example.queue

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.*
import com.example.queue.fragment.About
import com.example.queue.fragment.Home
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val TAG = "MainActivity"
    var currentlyAtHome:Boolean = true
    lateinit var drawer:DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")

        setSupportActionBar(findViewById(R.id.toolbar))
        drawer = findViewById(R.id.drawer)
        val drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val sideNavigationView = findViewById<NavigationView>(R.id.navigationView)
        sideNavigationView.setNavigationItemSelectedListener(this)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected() called with: item = $item")
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                Log.d(TAG, "onNavigationItemSelected() called with: item = ${item.itemId} home")
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<Home>(R.id.nav_host_fragment)
                }
                currentlyAtHome = true
            }
            R.id.nav_about -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<About>(R.id.nav_host_fragment)
                }
                currentlyAtHome = false
                Log.d(TAG, "onNavigationItemSelected() called with: item = ${item.itemId} about")
            }
        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (currentlyAtHome) {
                super.onBackPressed()
            } else {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<Home>(R.id.nav_host_fragment)
                }
                currentlyAtHome = true
            }
        }
    }
}