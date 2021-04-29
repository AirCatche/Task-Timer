package com.example.tasktimer.ui.duration

import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktimer.R
import com.example.tasktimer.database.DurationContract
import java.security.InvalidParameterException
import java.util.*

class DurationFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var displayWeek: Boolean = false
    private var durationAdapter: DurationsRVAdapter? = null
    private val gCalendar = GregorianCalendar()


    companion object {
        private const val TAG = "DurationFragment"
        private const val LOADER_ID = 1

        private const val SELECTION_PARAM = "SELECTION"
        private const val SELECTION_ARGS_PARAM = "SELECTION_ARGS"
        private const val SORT_ORDER_PARAM = "SORT_ORDER"
        private const val CURRENT_DATE = "CURRENT_DATE"
        private const val DISPLAY_WEEK = "DISPLAY_WEEK"
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: created")
        super.onActivityCreated(savedInstanceState)

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_duration, container, false)
        val rv: RecyclerView = view.findViewById(R.id.rv_duration_item_list)
        rv.layoutManager = LinearLayoutManager(requireContext())
        if (durationAdapter == null) {
            durationAdapter = DurationsRVAdapter(requireContext(), null)
        }
        rv.adapter = durationAdapter
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(LOADER_ID,savedInstanceState,this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.duration_filter_period -> {
                displayWeek = !displayWeek
                activity?.invalidateOptionsMenu()
                LoaderManager.getInstance(this).restartLoader(LOADER_ID,arguments, this)
                return true
            }
            R.id.duration_filter_date -> {
                return true
            }
            R.id.duration_delete -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.duration_filter_period)
        if (item != null) {
            if (displayWeek) {
                item.setIcon(R.drawable.ic_baseline_search_24)
                item.setTitle(R.string.duration_filter_week)
            } else {
                item.setIcon(R.drawable.ic_baseline_help)
                item.setTitle(R.string.duration_filter_day)
            }
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when(id) {
            LOADER_ID -> {
                val projection = arrayOf(BaseColumns._ID,
                    DurationContract.Columns.DURATION_NAME,
                    DurationContract.Columns.DURATION_DESCRIPTION,
                    DurationContract.Columns.DURATION_DURATION,
                    DurationContract.Columns.DURATION_START_TIME,
                    DurationContract.Columns.DURATION_START_DATE)
                var selection: String? = null
                var selectionArgs: Array<String>? = null
                var sortOrder: String? = null
                args?.let {
                    selection = it.getString(SELECTION_PARAM)
                    selectionArgs = it.getStringArray(SELECTION_ARGS_PARAM)
                    sortOrder = it.getString(SORT_ORDER_PARAM)
                }
                return CursorLoader(requireContext(), DurationContract.CONTENT_URI,projection,selection, selectionArgs, sortOrder)
            }
            else -> {
                throw InvalidParameterException("$TAG .onCreateLoader called with invalid loader id $id")
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        Log.d(TAG, "onLoadFinished: entering")
        Log.d(TAG, "onLoadFinished: $data")
        durationAdapter?.swapCursor(data)
        val count = durationAdapter?.itemCount
        Log.d(TAG, "onLoadFinished: with $count entries")
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        Log.d(TAG, "onLoaderReset: starts")
        durationAdapter?.swapCursor(null)
    }


}