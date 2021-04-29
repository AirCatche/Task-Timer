package com.example.tasktimer.ui.duration

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktimer.R
import com.example.tasktimer.database.DurationContract
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class DurationsRVAdapter(val context:Context?, private var cursor: Cursor?) : RecyclerView.Adapter<DurationsRVAdapter.ViewHolder>() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.task_duration_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor != null && cursor!!.count != 0) {
            if (!cursor!!.moveToPosition(position)) {
                throw IllegalStateException("Couldn't move cursor to position $position")
            }
            val name = cursor!!.getString(cursor!!.getColumnIndex(DurationContract.Columns.DURATION_NAME))
            val description = cursor!!.getString(cursor!!.getColumnIndex(DurationContract.Columns.DURATION_DESCRIPTION))
            val startTime = cursor!!.getLong(cursor!!.getColumnIndex(DurationContract.Columns.DURATION_START_TIME))
            val duration = cursor!!.getLong(cursor!!.getColumnIndex(DurationContract.Columns.DURATION_DURATION))

            holder.name.text = name
            holder.description?.text = description
            val userDate = dateFormat.format(startTime*1000)
            val totalDuration = formatDuration(duration)
            holder.startDate.text = userDate
            holder.duration.text = totalDuration
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    private fun formatDuration(duration: Long): String {
        val hours = duration / 3600
        val remainder = duration - (hours * 3600)
        val minutes = remainder/60
        val seconds = remainder - (60*minutes)
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }


    /**
     * Swap in a new Cursor, returning old cursor
     *
     * @param newCursor the new Cursor to be used
     * @return Returns the previously set Cursor or null if there wasn't one.
     */

    fun swapCursor (newCursor: Cursor?): Cursor? {
        if (newCursor == cursor) {
            return null
        }
        val oldCursor = cursor
        cursor = newCursor
        if (newCursor != null) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeRemoved(0, itemCount)
        }
        return oldCursor
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_duration_item_name)
        val description: TextView? = itemView.findViewById(R.id.tv_duration_item_description)
        val startDate: TextView = itemView.findViewById(R.id.tv_duration_item_start)
        val duration: TextView = itemView.findViewById(R.id.tv_duration_item_duration)
        val view: View = itemView
    }


}