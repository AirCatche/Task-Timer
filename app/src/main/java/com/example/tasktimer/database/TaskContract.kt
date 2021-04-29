package com.example.tasktimer.database

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

class TaskContract {

    companion object{
        const val TABLE_NAME = "Tasks"
        //Uri access to Tasks table
        val CONTENT_URI: Uri = Uri.withAppendedPath(Provider.CONTENT_AUTHORITY_URI, TABLE_NAME)
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.${Provider.CONTENT_AUTHORITY}.$TABLE_NAME"
        const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.${Provider.CONTENT_AUTHORITY}.$TABLE_NAME"
    }

    class Columns private constructor() {
        companion object{
            //Tasks fields
            const val ID = BaseColumns._ID
            const val TASKS_NAME = "Name"
            const val TASKS_DESCRIPTION = "Description"
            const val TASKS_SORT_ORDER = "SortOrder"
        }
    }

    object UriBuilder{
        fun buildTaskUri (taskId: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, taskId)
        }
        fun getTaskId(uri: Uri): Long {
            return ContentUris.parseId(uri)
        }
    }
}