package com.example.tasktimer

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.tasktimer.menufragments.AboutFragment
import com.example.tasktimer.menufragments.SearchFragment
import com.example.tasktimer.menufragments.SettingsFragment
import com.example.tasktimer.menufragments.TasksDurationFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.AssertionError

class MainActivity : AppCompatActivity(), OnTaskClickListener, OnSaveClicked, DialogEvents {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var bottomNavFab: FloatingActionButton

    companion object {
        private const val TAG = "MainActivity"

        //landscapeMode
        private var twoPane = false
        private const val DELETE_DIALOG_ID = 1
        private var CACHED_BOTTOM_ITEM_ID: Int? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        CACHED_BOTTOM_ITEM_ID = savedInstanceState?.getInt("cachedMenuItemId")
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNavFab = findViewById(R.id.fab_create)
        bottomNav.background = null
        bottomNav.menu.getItem(2).isEnabled = false
        bottomNav.setOnNavigationItemSelectedListener(navListener)
        bottomNavFab.setOnClickListener(fabListener)
        bottomNav.itemTextAppearanceActive = 0


        if (findViewById<FrameLayout>(R.id.fragment_task_details) != null) {
            twoPane = true
        }

        Log.d(TAG, "onCreate: ends")
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment = Fragment()
        when (item.itemId) {
            R.id.nav_tasks -> {
                selectedFragment = TasksDurationFragment()
            }
            R.id.nav_search -> {
                selectedFragment = SearchFragment()
            }
            R.id.nav_settings -> {
                selectedFragment = SettingsFragment()
            }
            R.id.nav_about -> {
                selectedFragment = AboutFragment()
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, selectedFragment).commit()
        CACHED_BOTTOM_ITEM_ID = item.itemId
        return@OnNavigationItemSelectedListener true
    }
    private val fabListener = View.OnClickListener {
        editTask(null)
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
    override fun onStart() {
        super.onStart()
        if (CACHED_BOTTOM_ITEM_ID != null) {
            bottomNav.selectedItemId = CACHED_BOTTOM_ITEM_ID!!
            when (CACHED_BOTTOM_ITEM_ID) {
                R.id.nav_tasks -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, TasksDurationFragment()).commit()
                }
                R.id.nav_search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, SearchFragment()).commit()
                }
                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, SettingsFragment()).commit()
                }
                R.id.nav_about -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, AboutFragment()).commit()
                }
            }
        } else {
            CACHED_BOTTOM_ITEM_ID = R.id.nav_tasks
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, TasksDurationFragment()).commit()
        }
    }
    override fun onEditClick(task: Task) {
        editTask(task)
    }
    override fun onDeleteTask(task: Task) {
        Log.d(TAG, "onDeleteTask: starts")
        val dialog = AppDialog()
        val dialogArgs = Bundle()
        dialogArgs.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID)
        dialogArgs.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task._id, task.name))
        dialogArgs.putInt(AppDialog.DIALOG_POSITIVE_RID, (R.string.deldiag_positive_caption))
        dialogArgs.putLong("TaskId", task._id)

        dialog.arguments = dialogArgs
        dialog.show(supportFragmentManager, null)
    }
    override fun onSaveClicked() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_task_details)
        fragment?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }
    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onPositiveDialogResult: ")
        val taskId: Long = args.getLong("TaskId")
        if (BuildConfig.DEBUG && taskId == 0L) throw AssertionError("Task ID is zero")
        contentResolver.delete(TaskContract.UriBuilder.buildTaskUri(taskId), null, null)
    }

    override fun onNegativeDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onNegativeDialogResult: ")
    }

    override fun onDialogCancelled(dialogId: Int) {
        Log.d(TAG, "onDialogCancelled: ")
    }

    private fun editTask(task: Task?) {
        Log.d(TAG, "editTask: starts")
        if (twoPane) {
            Log.d(TAG, "editTask: in two-pane mode")
            val fragment = TaskEditorActivityFragment()
            val args = Bundle()
            args.putSerializable(Task::class.java.simpleName, task)
            fragment.arguments = args
            supportFragmentManager.beginTransaction().replace(R.id.fragment_task_details, fragment).commit()
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