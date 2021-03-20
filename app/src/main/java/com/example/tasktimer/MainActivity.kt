package com.example.tasktimer

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)
        //supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, TasksDurationFragment()).commit()


        Log.d(TAG, "onCreate: ends")
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment = Fragment()
        when(item.itemId) {
            R.id.nav_tasks -> {
                selectedFragment = TasksDurationFragment()
            }
            R.id.nav_search -> {
                selectedFragment = SearchFragment()
            }
            R.id.nav_create -> {
                selectedFragment = TasksCreationFragment()
            }
            R.id.nav_settings -> {
                selectedFragment = SettingsFragment()
            }
            R.id.nav_about -> {
                selectedFragment = AboutFragment()
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, selectedFragment).commit()
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.nav_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}