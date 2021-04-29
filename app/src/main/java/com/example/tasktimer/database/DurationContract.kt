package com.example.tasktimer.database

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

class DurationContract {

    companion object{
        const val TABLE_NAME = "viewTaskDuration"
        //Uri access to Duration view
        val CONTENT_URI: Uri = Uri.withAppendedPath(Provider.CONTENT_AUTHORITY_URI, TABLE_NAME)

        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.${Provider.CONTENT_AUTHORITY}.$TABLE_NAME"
        const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.${Provider.CONTENT_AUTHORITY}.$TABLE_NAME"
    }

    class Columns private constructor() : BaseColumns {
        companion object{
            //Timings fields
            const val ID = BaseColumns._ID
            const val DURATION_NAME = TaskContract.Columns.TASKS_NAME
            const val DURATION_DESCRIPTION = TaskContract.Columns.TASKS_DESCRIPTION
            const val DURATION_START_TIME = TimingContract.Columns.TIMINGS_START_TIME
            const val DURATION_START_DATE = "StartDate"
            const val DURATION_DURATION = TimingContract.Columns.TIMINGS_DURATION

        }
    }

    object UriBuilder{
        fun getDurationId(uri: Uri): Long {
            return ContentUris.parseId(uri)
        }
    }
}