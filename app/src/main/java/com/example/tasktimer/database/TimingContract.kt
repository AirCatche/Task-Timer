package com.example.tasktimer.database

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

class TimingContract {

    companion object{
        const val TABLE_NAME = "Timings"
        //Uri access to Timings table
        val CONTENT_URI: Uri = Uri.withAppendedPath(Provider.CONTENT_AUTHORITY_URI, TABLE_NAME)

        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.${Provider.CONTENT_AUTHORITY}.$TABLE_NAME"
        const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.${Provider.CONTENT_AUTHORITY}.$TABLE_NAME"
    }

    class Columns private constructor() : BaseColumns {
        companion object{
            //Timings fields
            const val _ID = BaseColumns._ID
            const val TIMINGS_TASK_ID = "TaskId"
            const val TIMINGS_START_TIME = "StartTime"
            const val TIMINGS_DURATION = "Duration"
        }
    }

    object UriBuilder{
        fun buildTimingUri (timingId: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, timingId)
        }

        fun getTimingId(uri: Uri): Long {
            return ContentUris.parseId(uri)
        }
    }
}