package com.example.tasktimer.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

/**
* Provider for the TaskTimer.
* This is only class that knows about [Database]
*/

class Provider: ContentProvider() {

    private lateinit var openHelper: Database

    companion object{
        const val TAG = "Provider"
        const val CONTENT_AUTHORITY = "com.example.tasktimer.provider"
        private const val TASKS = 100
        private const val TASKS_ID = 101
        private const val TIMINGS = 200
        private const val TIMINGS_ID = 201
        private const val TASK_TIMING = 300
        private const val TASK_TIMING_ID = 301
        private const val TASK_DURATION = 400
        private const val TASK_DURATION_ID = 401
        val CONTENT_AUTHORITY_URI = Uri.parse("content://$CONTENT_AUTHORITY")!!
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        // content://com.example.tasktimer.provider/Tasks
        uriMatcher.addURI(CONTENT_AUTHORITY, TaskContract.TABLE_NAME, TASKS)
        // content://com.example.tasktimer.provider/Tasks/8
        uriMatcher.addURI(CONTENT_AUTHORITY, "${TaskContract.TABLE_NAME}/#", TASKS_ID)

//        uriMatcher.addURI(CONTENT_AUTHORITY, TimingContract.TABLE_NAME, TIMINGS)
//        uriMatcher.addURI(CONTENT_AUTHORITY, "${TimingContract.TABLE_NAME}/#", TIMINGS_ID)
//
//        uriMatcher.addURI(CONTENT_AUTHORITY, DurationContract.TABLE_NAME, TASK_DURATION)
//        uriMatcher.addURI(CONTENT_AUTHORITY, "${DurationContract.TABLE_NAME}/#", TASK_DURATION_ID)

    }

    override fun onCreate(): Boolean {
        openHelper = Database(context!!)!!
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {

        Log.d(TAG, "query: called URI: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "query: MATCH: $match")
        val queryBuilder = SQLiteQueryBuilder()
        when(match) {
            TASKS -> {
                queryBuilder.tables = TaskContract.TABLE_NAME
            }
            TASKS_ID ->{
                queryBuilder.tables = TaskContract.TABLE_NAME
                val taskId = TaskContract.UriBuilder.getTaskId(uri)
                queryBuilder.appendWhere("${TaskContract.Columns._ID} = $taskId")
            }

//            TIMINGS -> {
//                queryBuilder.tables = TimingContract.TABLE_NAME
//            }
//            TIMINGS_ID ->{
//                queryBuilder.tables = TimingContract.TABLE_NAME
//                val timingId: Long = TimingContract.timingId(uri)
//                queryBuilder.appendWhere("${TimingContract.Columns._ID} = $taskId")
//            }
//
//            TASK_DURATION -> {
//                queryBuilder.tables = DurationContract.TABLE_NAME
//            }
//            TASK_DURATION_ID ->{
//                queryBuilder.tables = DurationContract.TABLE_NAME
//                val durationId: Long = DurationContract.durationId(uri)
//                queryBuilder.appendWhere("${DurationContract.Columns._ID} = $taskId")
//            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        val db: SQLiteDatabase = openHelper.readableDatabase
        val cursor: Cursor = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder)
        Log.d(TAG, "query:  rows returned = ${cursor.columnNames}")
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor

    }

    override fun getType(uri: Uri): String {
        return when(uriMatcher.match(uri)) {
             TASKS -> TaskContract.CONTENT_TYPE

             TASKS_ID -> TaskContract.CONTENT_ITEM_TYPE

//            TIMINGS -> { return TaskContract.CONTENT_TYPE}
//            TIMINGS_ID ->{ return TaskContract.CONTENT_ITEM_TYPE}
//            TASK_DURATION -> { return TaskContract.CONTENT_TYPE}
//            TASK_DURATION_ID ->{ return TaskContract.CONTENT_ITEM_TYPE}
             else -> { throw IllegalArgumentException("Unknown URI: $uri") }
         }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        Log.d(TAG, "Entering insert with uri: $uri")
        val db: SQLiteDatabase
        val recordId: Long
        val returnUri =  when(uriMatcher.match(uri)) {
            TASKS -> {
                db = openHelper.writableDatabase
                recordId = db.insert(TaskContract.TABLE_NAME,null,values)
                if (recordId>=0) {
                    TaskContract.UriBuilder.buildTaskUri(recordId)
                } else {
                    throw SQLException("Failed insert into $uri")
                }
            }
//            TASKS_ID -> {
//
//            }
//            TIMINGS -> {
//                db = openHelper.writableDatabase
//                recordId = db.insert(TimingContract.BuildUri.buildTimingUri(recordId))
//                if (recordId>=0) {
//                    TaskContract.UriBuilder.buildTaskUri(recordId)
//                } else {
//                    throw SQLException("Failed insert into $uri")
//                }
//            }

//            TIMINGS_ID ->{ return TaskContract.CONTENT_ITEM_TYPE}
//            TASK_DURATION -> { return TaskContract.CONTENT_TYPE}
//            TASK_DURATION_ID ->{ return TaskContract.CONTENT_ITEM_TYPE}
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        if (recordId >= 0) {
            Log.d(TAG, "insert: Setting notifyChanged $uri")
            context!!.contentResolver.notifyChange(uri, null)
        }
        Log.d(TAG, "insert: Exiting with uri $returnUri")
        return returnUri

    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "delete called with URI: $uri")
        val db: SQLiteDatabase
        var selectionCriteria: String
        val deletedEntries: Int
        when(uriMatcher.match(uri)) {
            TASKS -> {
                db = openHelper.writableDatabase
                deletedEntries = db.delete(TaskContract.TABLE_NAME,selection, selectionArgs)
            }
            TASKS_ID -> {
                db = openHelper.writableDatabase
                val taskId: Long = TaskContract.UriBuilder.getTaskId(uri)
                selectionCriteria = "${TaskContract.Columns._ID} = $taskId"
                if ((selection != null) && (selection.isNotEmpty())) {
                    selectionCriteria += " AND ($selection)"
                }
                deletedEntries = db.delete(TaskContract.TABLE_NAME,selectionCriteria, selectionArgs)
            }
//            TIMINGS -> {
//                db = openHelper.writableDatabase
//                deletedEntries = db.delete(TimingContract.TABLE_NAME,selection, selectionArgs)
//            }
//            TIMINGS_ID -> {
//                db = openHelper.writableDatabase
//                val timingId: Long = TimingContract.UriBuilder.getTaskId(uri)
//                selectionCriteria = "TimingContract.Columns._ID = $timingId"
//                if ((selection != null) && (selection.isNotEmpty())) {
//                    selectionCriteria += " AND ($selection)"
//                }
//                deletedEntries = db.delete(TimingContract.TABLE_NAME,selectionCriteria, selectionArgs)
//            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        if (deletedEntries > 0) {
            Log.d(TAG, "Deleted $deletedEntries entries")
            context!!.contentResolver.notifyChange(uri,null)
        } else {
            Log.d(TAG, "Nothing deleted")
        }
        return deletedEntries
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "update called with URI: $uri")
        val db: SQLiteDatabase
        var selectionCriteria: String
        val updatedEntries: Int
        Log.d(TAG, "update: match is ${uriMatcher.match(uri)}")
        when(uriMatcher.match(uri)) {
            TASKS -> {
                db = openHelper.writableDatabase
                updatedEntries = db.update(TaskContract.TABLE_NAME,values,selection, selectionArgs)
            }
            TASKS_ID -> {
                db = openHelper.writableDatabase
                val taskId: Long = TaskContract.UriBuilder.getTaskId(uri)
                selectionCriteria = "${TaskContract.Columns._ID} = $taskId"
                if ((selection != null) && (selection.isNotEmpty())) {
                    selectionCriteria += " AND ($selection)"
                }
                updatedEntries = db.update(TaskContract.TABLE_NAME,values,selectionCriteria, selectionArgs)
            }
//            TIMINGS -> {
//                db = openHelper.writableDatabase
//                db.update(TimingContract.TABLE_NAME,values,selection, selectionArgs)
//            }
//            TIMINGS_ID -> {
//                db = openHelper.writableDatabase
//                val timingId: Long = TimingContract.UriBuilder.getTaskId(uri)
//                selectionCriteria = "TimingContract.Columns._ID = $timingId"
//                if ((selection != null) && (selection.isNotEmpty())) {
//                    selectionCriteria += " AND ($selection)"
//                }
//                db.update(TimingContract.TABLE_NAME,values,selectionCriteria, selectionArgs)
//            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        if (updatedEntries > 0) {
            Log.d(TAG, "Updated $updatedEntries entries")
            context!!.contentResolver.notifyChange(uri,null)
        } else {
            Log.d(TAG, "Nothing updated")
        }
        return updatedEntries
    }
}