package com.example.tasktimer.ui

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.tasktimer.*
import com.example.tasktimer.database.TaskContract
import com.example.tasktimer.database.entity.Task
import com.example.tasktimer.database.entity.Timing
import com.example.tasktimer.debug.TestData
import com.example.tasktimer.ui.about.AboutFragment
import com.example.tasktimer.ui.duration.DurationFragment
import com.example.tasktimer.ui.settings.SettingsFragment
import com.example.tasktimer.ui.tasks.OnSaveClicked
import com.example.tasktimer.ui.tasks.OnTaskClickListener
import com.example.tasktimer.ui.tasks.TaskEditorActivityFragment
import com.example.tasktimer.ui.tasks.TasksFragment
import com.google.android.material.bottomappbar.BottomAppBar

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.AssertionError

class MainActivity : AppCompatActivity(), OnTaskClickListener, OnSaveClicked, DialogEvents {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var bottomNavFab: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var addEditLayoutFragment: FrameLayout
    private lateinit var mainFragment: FrameLayout
    private var currentTiming: Timing? = null
    companion object {
        private const val TAG = "MainActivity"
        const val DIALOG_ID_DELETE = 1
        const val DIALOG_ID_CANCEL_EDIT = 2
        const val DIALOG_ID_CANCEL_EDIT_UP = 3
        private var CACHED_BOTTOM_ITEM_ID: Int? = null
        private var CACHED_BOTTOM_ITEM_KEY: String = "cachedBottomItemId"

        //landscapeMode
        var twoPane: Boolean = false
        var editMode: Boolean = false
    }

    //TODO("implement hide on scroll nested activity")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        CACHED_BOTTOM_ITEM_ID = savedInstanceState?.getInt("cachedMenuItemId")
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        addEditLayoutFragment = findViewById(R.id.fragment_task_details)
        mainFragment = findViewById(R.id.fragment_layout)
        bottomAppBar = findViewById(R.id.bottom_app_bar)
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNavFab = findViewById(R.id.fab_create)
        bottomNav.background = null
        bottomNav.menu.getItem(2).isEnabled = false
        bottomNav.setOnNavigationItemSelectedListener(navListener)
        bottomNavFab.setOnClickListener(fabListener)
        bottomNav.itemTextAppearanceActive = 0


        setScreenFragment()
        Log.d(TAG, "onCreate: ends")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_generate_data -> {
                TestData.generateTestData(contentResolver)
                true
            }
            android.R.id.home -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_task_details) as TaskEditorActivityFragment
                return if (fragment.canClose()) { super.onOptionsItemSelected(item) }
                else {
                    showConfirmation(DIALOG_ID_CANCEL_EDIT_UP)
                    true
                }
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
                        .replace(R.id.fragment_layout, TasksFragment()).commit()
                }
                R.id.nav_duration -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, DurationFragment()).commit()
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
                .replace(R.id.fragment_layout, TasksFragment()).commit()
        }
    }

    override fun onEditClick(task: Task) {
        editMode = true
        bottomAppBar.visibility = View.GONE
        bottomNavFab.visibility = View.GONE
        editTask(task)
    }

    override fun onDeleteTask(task: Task) {
        Log.d(TAG, "onDeleteTask: starts")
        val dialog = AppDialog()
        val dialogArgs = Bundle()
        dialogArgs.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE)
        dialogArgs.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task._id, task.name))
        dialogArgs.putInt(AppDialog.DIALOG_POSITIVE_RID, (R.string.deldiag_positive_caption))
        dialogArgs.putLong("TaskId", task._id)
        dialog.arguments = dialogArgs
        dialog.show(supportFragmentManager, null)
    }

    override fun onTaskLongClick(task: Task) {
        //Interface contract
    }

    override fun onSaveClicked() {
        bottomAppBar.visibility = View.VISIBLE
        bottomNavFab.visibility = View.VISIBLE
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_task_details)
        fragment?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
        if (!twoPane) {
            addEditLayoutFragment.visibility = View.GONE
            mainFragment.visibility = View.VISIBLE
        }
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onPositiveDialogResult: ")
        when (dialogId) {
            DIALOG_ID_DELETE -> {

                val taskId: Long = args.getLong("TaskId")
                if (BuildConfig.DEBUG && taskId == 0L) throw AssertionError("Task ID is zero")
                contentResolver.delete(TaskContract.UriBuilder.buildTaskUri(taskId), null, null)
            }
            DIALOG_ID_CANCEL_EDIT -> {

            }
            DIALOG_ID_CANCEL_EDIT_UP -> {

            }
        }

    }

    override fun onNegativeDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onNegativeDialogResult: ")
        bottomAppBar.visibility = View.VISIBLE
        bottomNavFab.visibility = View.VISIBLE
        when (dialogId) {
            DIALOG_ID_DELETE -> {
                //no action required
            }
            DIALOG_ID_CANCEL_EDIT -> {

            }
            DIALOG_ID_CANCEL_EDIT_UP -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_task_details)
                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(fragment).commit()
                    if (twoPane) {
                        if (dialogId == DIALOG_ID_CANCEL_EDIT) {
                        finish()
                        }
                    } else {
                        addEditLayoutFragment.visibility = View.GONE
                        mainFragment.visibility = View.VISIBLE
                    }
                } else {
                    finish()
                }
            }
        }
    }

    override fun onDialogCancelled(dialogId: Int) {
        Log.d(TAG, "onDialogCancelled: ")
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed: starts")
        val fragment: TaskEditorActivityFragment? = supportFragmentManager.findFragmentById(R.id.fragment_task_details) as TaskEditorActivityFragment?
        if (fragment == null || fragment.canClose()) {
            super.onBackPressed()
        } else {
            showConfirmation(DIALOG_ID_CANCEL_EDIT_UP)
        }

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(CACHED_BOTTOM_ITEM_KEY, bottomNav.selectedItemId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomNav.selectedItemId = savedInstanceState.getInt(CACHED_BOTTOM_ITEM_KEY)
    }

    private fun editTask(task: Task?) {
        Log.d(TAG, "editTask: starts")
        val fragment = TaskEditorActivityFragment()
        val args = Bundle()
        args.putSerializable(Task::class.java.simpleName, task)
        fragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.fragment_task_details, fragment).commit()
        if (!twoPane) {
            Log.d(TAG, "editTask: in single-pane mode")
            mainFragment.visibility = View.GONE
            addEditLayoutFragment.visibility = View.VISIBLE
        }
        Log.d(TAG, "editTask: exiting")
    }

    private fun showConfirmation (dialogId: Int) {
        val dialog = AppDialog()
        val args = Bundle().also {
            it.putInt(AppDialog.DIALOG_ID, dialogId)
            it.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.editDiag_cancelMessage))
            it.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.edinDiag_positive_caption)
            it.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.edinDiag_negative_caption)
        }
        dialog.arguments = args
        dialog.show(supportFragmentManager, null)
    }


    private fun setScreenFragment() {
        twoPane = (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        Log.d(TAG, "onCreate: two pane is $twoPane")
        val editing = supportFragmentManager.findFragmentById(R.id.fragment_task_details) != null
        val viewDuration = supportFragmentManager.findFragmentById(R.id.fragment_layout) is DurationFragment

        Log.d(TAG, "onCreate: editing is $editing")

        if (twoPane && viewDuration) {
            mainFragment.visibility = View.VISIBLE
            addEditLayoutFragment.visibility = View.GONE
        } else if (twoPane) {
            Log.d(TAG, "onCreate: twoPane mode")
            mainFragment.visibility = View.VISIBLE
            addEditLayoutFragment.visibility = View.VISIBLE
        }
        else if (editing) {
            Log.d(TAG, "onCreate: single pane")
            mainFragment.visibility = View.GONE
        } else {
            Log.d(TAG, "onCreate: single pane no editing")
            mainFragment.visibility = View.VISIBLE
            addEditLayoutFragment.visibility = View.GONE
        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment = Fragment()
        when (item.itemId) {
            R.id.nav_tasks -> {
                selectedFragment = TasksFragment()
            }
            R.id.nav_duration -> {
                selectedFragment = DurationFragment()
            }
            R.id.nav_settings -> {
                selectedFragment = SettingsFragment()
            }
            R.id.nav_about -> {
                selectedFragment = AboutFragment()
            }
        }
        if (twoPane && selectedFragment is TasksFragment) {
            mainFragment.visibility = View.VISIBLE
            addEditLayoutFragment.visibility = View.VISIBLE
        } else {
            mainFragment.visibility = View.VISIBLE
            addEditLayoutFragment.visibility = View.GONE
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, selectedFragment).commit()
        CACHED_BOTTOM_ITEM_ID = item.itemId
        return@OnNavigationItemSelectedListener true
    }
    private val fabListener = View.OnClickListener {
        bottomAppBar.visibility = View.GONE
        bottomNavFab.visibility = View.GONE
        editTask(null)
    }
}