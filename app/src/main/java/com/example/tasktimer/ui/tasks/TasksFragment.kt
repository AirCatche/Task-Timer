package com.example.tasktimer.ui.tasks

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktimer.*
import com.example.tasktimer.database.TaskContract
import com.example.tasktimer.database.TimingContract
import com.example.tasktimer.database.entity.Task
import com.example.tasktimer.database.entity.Timing
import com.example.tasktimer.ui.CursorRecyclerViewAdapter
import com.example.tasktimer.ui.MainActivity
import com.example.tasktimer.ui.OnTaskClickListener
import java.lang.ClassCastException
import java.security.InvalidParameterException

class TasksFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>,
    OnTaskClickListener {
    private var taskCursorAdapter: CursorRecyclerViewAdapter? = null
    private var currentTiming: Timing? = null
    companion object {
        private const val TAG = "TasksDurationFragment"
        const val LOADER_ID = 0
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: starts")
        super.onActivityCreated(savedInstanceState)
        val activity = activity
        if (activity !is OnTaskClickListener) {
            throw ClassCastException(activity?.javaClass?.simpleName + "must implement OnTaskClickListener")
        }

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
        setTiming(currentTiming)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: starts")
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.task_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        if (taskCursorAdapter == null) {
            taskCursorAdapter = CursorRecyclerViewAdapter(null, this)
        }
        recyclerView.adapter = taskCursorAdapter
        Log.d(TAG, "onCreateView: returning")
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.d(TAG, "onCreateLoader: starts with $id id")
        val projection = arrayOf(TaskContract.Columns._ID, TaskContract.Columns.TASKS_NAME, TaskContract.Columns.TASKS_DESCRIPTION, TaskContract.Columns.TASKS_SORT_ORDER)
        val sortOrder = "${TaskContract.Columns.TASKS_SORT_ORDER},${TaskContract.Columns.TASKS_NAME} COLLATE NOCASE"
       when (id) {
           LOADER_ID -> {
               return CursorLoader(requireActivity(), TaskContract.CONTENT_URI, projection, null, null, sortOrder)
           }
           else -> {
               throw InvalidParameterException("$TAG.onCreateLoader called with invalid loader id $id")
           }

       }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        Log.d(TAG, "onLoadFinished: entering")
        Log.d(TAG, "onLoadFinished: $data")
        taskCursorAdapter?.swapCursor(data)
        val count = taskCursorAdapter?.itemCount
        Log.d(TAG, "onLoadFinished: with $count entries")
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        Log.d(TAG, "onLoaderReset: starts")
        taskCursorAdapter?.swapCursor(null)
    }

    override fun onEditClick(task: Task) {
        Log.d(TAG, "onEditClick: called")
        val listener = activity as OnTaskClickListener
        listener.onEditClick(task)

    }

    override fun onDeleteTask(task: Task) {
        Log.d(TAG, "onDeleteTask: starts")
        val listener = activity as OnTaskClickListener
        listener.onDeleteTask(task)
    }

    override fun onTaskLongClick(task: Task) {
        Log.d(TAG, "onTaskLongClick: starts")

        if (currentTiming!= null) {
            if (task._id == currentTiming!!.task._id) {
                saveTiming(currentTiming!!)
                currentTiming = null
                setTiming(null)
            } else {
                saveTiming(currentTiming!!)
                currentTiming = Timing(task = task)
                setTiming(currentTiming)
            }
        } else {
            currentTiming = Timing(task = task)
            setTiming(currentTiming)
        }
    }
    private fun saveTiming (currentTiming: Timing) {
        Log.d(TAG, "Entering saveTiming ")
        currentTiming.setDuration()
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val values = ContentValues()
        values.put(TimingContract.Columns.TIMINGS_TASK_ID, currentTiming.task._id)
        values.put(TimingContract.Columns.TIMINGS_START_TIME, currentTiming.startTime)
        values.put(TimingContract.Columns.TIMINGS_DURATION, currentTiming.duration)

        contentResolver.insert(TimingContract.CONTENT_URI, values)
        Log.d(TAG, "saveTiming: Exiting saveTiming")
    }

    @SuppressLint("SetTextI18n")
    private fun setTiming(timing: Timing?) {
        val taskName: TextView = requireActivity().findViewById(R.id.tv_current_task_time)
        if (timing!= null) {
            taskName.text = "Timing ${currentTiming!!.task.name}"
        } else {
            taskName.text = getString(R.string.no_task_message)
        }
    }
}