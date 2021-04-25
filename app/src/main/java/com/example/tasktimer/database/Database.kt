package com.example.tasktimer.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Database class.
 * The only class that should use DB is [Provider].
 */

class Database private constructor(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION) {

    companion object{
        const val TAG = "Database"
        const val DATABASE_NAME = "TaskTimer.db"
        const val DATABASE_VERSION = 2

        private var instance: Database? = null
        operator fun invoke(context: Context) = synchronized(this) {
            if (instance == null) {
                instance = Database(context)
            }
            instance
        }

    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "onCreate: starts")
        val sql = "CREATE TABLE ${TaskContract.TABLE_NAME} (" +
                "${TaskContract.Columns._ID} INTEGER PRIMARY KEY NOT NULL, " +
                "${TaskContract.Columns.TASKS_NAME} TEXT NOT NULL, " +
                "${TaskContract.Columns.TASKS_DESCRIPTION} TEXT, " +
                "${TaskContract.Columns.TASKS_SORT_ORDER} INTEGER );"

        Log.d(TAG, sql)
        db?.execSQL(sql)

        addTimingTable(db)
        addDurationView(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: starts")
        when(oldVersion) {
            1 -> {
                addTimingTable(db)
            }
            2 -> {
                addDurationView(db)
            }
            else -> {
                throw IllegalStateException("onUpgrade() with unknown newVersion: $newVersion")
            }
        }
        Log.d(TAG, "onUpgrade: ends")
    }

    private fun addTimingTable(db: SQLiteDatabase?) {
        var sql = "CREATE TABLE ${TimingContract.TABLE_NAME} (" +
                "${TimingContract.Columns._ID} INTEGER PRIMARY KEY NOT NULL, " +
                "${TimingContract.Columns.TIMINGS_TASK_ID} INTEGER NOT NULL, " +
                "${TimingContract.Columns.TIMINGS_START_TIME} INTEGER, " +
                "${TimingContract.Columns.TIMINGS_DURATION} INTEGER );"

        Log.d(TAG, sql)
        db?.execSQL(sql)

        sql = "CREATE TRIGGER Remove_Task" +
                " AFTER DELETE ON ${TaskContract.TABLE_NAME}" +
                " FOR EACH ROW" +
                " BEGIN" +
                " DELETE FROM ${TimingContract.TABLE_NAME}" +
                " WHERE ${TimingContract.Columns.TIMINGS_TASK_ID} = OLD.${TaskContract.Columns._ID}" +
                "; END"

        Log.d(TAG, sql)
        db?.execSQL(sql)
    }

    private fun addDurationView(db: SQLiteDatabase?) {
        /*
         CREATE VIEW vwTaskDurations AS
         SELECT Timings._id,
         Tasks.Name,
         Tasks.Description,
         Timings.StartTime,
         DATE(Timings.StartTime, 'unixepoch') AS StartDate,
         SUM(Timings.Duration) AS Duration
         FROM Tasks INNER JOIN Timings
         ON Tasks._id = Timings.TaskId
         GROUP BY Tasks._id, StartDate;
         */
        val sSQL = ("CREATE VIEW " + DurationContract.TABLE_NAME
                + " AS SELECT " + TimingContract.TABLE_NAME + "." + TimingContract.Columns._ID + ", "
                + TaskContract.TABLE_NAME + "." + TaskContract.Columns.TASKS_NAME + ", "
                + TaskContract.TABLE_NAME + "." + TaskContract.Columns.TASKS_DESCRIPTION + ", "
                + TimingContract.TABLE_NAME + "." + TimingContract.Columns.TIMINGS_START_TIME + ","
                + " DATE(" + TimingContract.TABLE_NAME + "." + TimingContract.Columns.TIMINGS_START_TIME
                + ", 'unixepoch')"
                + " AS " + DurationContract.Columns.DURATION_START_DATE + ","
                + " SUM(" + TimingContract.TABLE_NAME + "." + TimingContract.Columns.TIMINGS_DURATION + ")"
                + " AS " + DurationContract.Columns.DURATION_DURATION
                + " FROM " + TaskContract.TABLE_NAME + " JOIN " + TimingContract.TABLE_NAME
                + " ON " + TaskContract.TABLE_NAME + "." + TaskContract.Columns._ID + " = "
                + TimingContract.TABLE_NAME) + ".${TimingContract.Columns.TIMINGS_TASK_ID} GROUP BY " +
                "${DurationContract.Columns.DURATION_START_DATE}, ${DurationContract.Columns.DURATION_NAME};"
        db?.execSQL(sSQL)
    }
}


