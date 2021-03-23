package com.example.tasktimer

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.tasktimer.menufragments.AboutFragment
import com.example.tasktimer.menufragments.SearchFragment
import com.example.tasktimer.menufragments.SettingsFragment
import com.example.tasktimer.menufragments.TasksDurationFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), OnTaskClickListener {
    private lateinit var bottomNav: BottomNavigationView
    companion object{
        private const val TAG = "MainActivity"
        //landscapeMode
        private var twoPane = false
        private const val ADD_EDIT_FRAGMENT = "AddEditFragment"
        private var CACHED_BOTTOM_ITEM_ID: Int? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        CACHED_BOTTOM_ITEM_ID = savedInstanceState?.getInt("cachedMenuItemId")
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)
        bottomNav.itemTextAppearanceActive = 0

        Log.d(TAG, "onCreate: ends")
    }

    override fun onStart() {
        super.onStart()
        if (CACHED_BOTTOM_ITEM_ID != null) {
            bottomNav.selectedItemId = CACHED_BOTTOM_ITEM_ID!!
            when(CACHED_BOTTOM_ITEM_ID) {
                R.id.nav_tasks -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, TasksDurationFragment()).commit()
                }
                R.id.nav_search -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, SearchFragment()).commit()
                }
                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, SettingsFragment()).commit()
                }
                R.id.nav_about -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, AboutFragment()).commit()
                }
            }
        } else {
            CACHED_BOTTOM_ITEM_ID = R.id.nav_tasks
            supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, TasksDurationFragment()).commit()
        }
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
                editTask(null)
                return@OnNavigationItemSelectedListener false
            }
            R.id.nav_settings -> {
                selectedFragment = SettingsFragment()
            }
            R.id.nav_about -> {
                selectedFragment = AboutFragment()
            }
        }
        if (item.itemId != R.id.nav_create) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, selectedFragment).commit()
            CACHED_BOTTOM_ITEM_ID = item.itemId
        }
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_generate_data -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onEditClick(task: Task) {
        editTask(task)
    }

    override fun onDeleteTask(task: Task) {
        contentResolver.delete(TaskContract.UriBuilder.buildTaskUri(task._id), null, null)
    }

    private fun editTask(task:Task?) {
        Log.d(TAG, "editTask: starts")
        if (twoPane) {
            Log.d(TAG, "editTask: in two-pane mode")
        } else {
            Log.d(TAG, "editTask: in single-pane mode")
            val detailIntent = Intent(this, TaskEditorActivity::class.java)
            task?.let {
                detailIntent.putExtra(Task::class.java.simpleName, task)
            }
            startActivity(detailIntent)
        }
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt("cachedMenuItemId", bottomNav.selectedItemId)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomNav.selectedItemId = savedInstanceState.getInt("cachedMenuItemId")
    }
}