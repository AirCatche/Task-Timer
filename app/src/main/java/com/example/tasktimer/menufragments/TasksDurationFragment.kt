package com.example.tasktimer.menufragments

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktimer.CursorRecyclerViewAdapter
import com.example.tasktimer.OnTaskClickListener
import com.example.tasktimer.R
import com.example.tasktimer.TaskContract
import java.security.InvalidParameterException

class TasksDurationFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var taskCursorAdapter: CursorRecyclerViewAdapter? = null
    companion object {
        private const val TAG = "TasksDurationFragment"
        const val LOADER_ID = 0
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: starts")
        super.onActivityCreated(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: starts")
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.task_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        taskCursorAdapter = CursorRecyclerViewAdapter(null, activity as OnTaskClickListener)
        recyclerView.adapter = taskCursorAdapter

        Log.d(TAG, "onCreateView: returning")
        return view
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
}