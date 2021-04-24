package com.example.tasktimer.ui

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktimer.R
import com.example.tasktimer.database.entity.Task
import com.example.tasktimer.database.TaskContract
import java.lang.IllegalStateException

class CursorRecyclerViewAdapter(private var cursor: Cursor?,private val listener: OnTaskClickListener?) : RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder>() {
    companion object {
        private const val TAG = "CursorRecyclerViewAdapt"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG, "onCreateViewHolder: new view requested")
        return TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false))

    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (cursor == null || cursor!!.count == 0) {
            holder.name.setText(R.string.instruction_heading)
            holder.description.setText(R.string.instruction_description)
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        } else {
            if (!cursor!!.moveToPosition(position)) {
                throw IllegalStateException("Couldn't move cursor to position $position")
            }
            val task = Task(cursor!!.getLong(cursor!!.getColumnIndex(TaskContract.Columns._ID)),
                            cursor!!.getString(cursor!!.getColumnIndex(TaskContract.Columns.TASKS_NAME)),
                            cursor!!.getString(cursor!!.getColumnIndex(TaskContract.Columns.TASKS_DESCRIPTION)),
                            cursor!!.getInt(cursor!!.getColumnIndex(TaskContract.Columns.TASKS_SORT_ORDER)))

            holder.name.text = task.name
            holder.description.text = task.description
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
            val buttonListener = View.OnClickListener {
                when(it.id) {
                    R.id.ib_edit_task -> {
                        listener?.onEditClick(task)
                    }
                    R.id.ib_delete_task -> {
                        listener?.onDeleteTask(task)
                    }

                    else -> {
                        Log.d(TAG, "onBindViewHolder: Unexpected button id")
                    }
                }
            }
            val itemLongListener = View.OnLongClickListener{
                Log.d(TAG, "onLongClick: starts")
                listener?.let {
                    it.onTaskLongClick(task)
                    true
                }
                false
            }
            holder.editButton.setOnClickListener(buttonListener)
            holder.deleteButton.setOnClickListener(buttonListener)
            holder.view.setOnLongClickListener(itemLongListener)
        }

    }
    override fun getItemCount(): Int {
         return if (cursor == null || cursor!!.count == 0) {
             1
         } else {
             cursor!!.count
         }
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

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_task_name)
        val description: TextView = itemView.findViewById(R.id.tv_task_description)
        val editButton: ImageButton = itemView.findViewById(R.id.ib_edit_task)
        val deleteButton: ImageButton = itemView.findViewById(R.id.ib_delete_task)

        val view: View = itemView
    }
}